package com.cariochi.reflecto.types;

import com.cariochi.reflecto.base.IsField;
import com.cariochi.reflecto.base.ReflectoAnnotations;
import com.cariochi.reflecto.base.ReflectoModifiers;
import com.cariochi.reflecto.base.Streamable;
import com.cariochi.reflecto.constructors.ReflectoConstructor;
import com.cariochi.reflecto.constructors.ReflectoConstructors;
import com.cariochi.reflecto.exceptions.NotFoundException;
import com.cariochi.reflecto.fields.ReflectoField;
import com.cariochi.reflecto.fields.ReflectoFields;
import com.cariochi.reflecto.invocations.model.Reflection;
import com.cariochi.reflecto.methods.ReflectoMethod;
import com.cariochi.reflecto.methods.ReflectoMethods;
import com.cariochi.reflecto.parameters.ReflectoParameter;
import com.cariochi.reflecto.utils.FieldsUtils;
import com.cariochi.reflecto.utils.MethodsUtils;
import com.cariochi.reflecto.utils.TypesUtils;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.reflect.TypeUtils;

import static com.cariochi.reflecto.utils.ExpressionUtils.parseIndex;
import static com.cariochi.reflecto.utils.ExpressionUtils.splitExpression;
import static com.cariochi.reflecto.utils.MessageUtils.enumNames;
import static com.cariochi.reflecto.utils.MessageUtils.fieldNames;
import static java.util.Collections.emptyList;

/**
 * Generic-aware metadata wrapper around a Java {@link Type}.
 * <p>
 * {@code ReflectoType} resolves type variables against the type where they are
 * declared, so fields, method return types, constructor parameters, and nested
 * paths preserve generic information.
 *
 * <pre>{@code
 * ReflectoType type = Reflecto.reflect(Types.type(Dto.class, Integer.class));
 *
 * type.reflect("value").actualType();       // Integer
 * type.reflect("child.value").actualType(); // Integer
 * type.reflect("map[1][0]").actualType();   // Dto<Integer>
 * }</pre>
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Accessors(fluent = true)
public class ReflectoType {

    @Getter
    private final Type rawType;

    private final ReflectoType declaringType;

    @Getter(lazy = true)
    @EqualsAndHashCode.Include
    private final Type actualType = determineActualType();

    @Getter(lazy = true)
    private final Class<?> actualClass = determineActualClass();

    @Getter(lazy = true)
    private final ReflectoTypes types = new ReflectoTypes(
            () -> Stream.of(actualClass().getClasses()).map(ReflectoType.this::reflect).toList(),
            () -> Stream.of(actualClass().getDeclaredClasses()).map(ReflectoType.this::reflect).toList()
    );

    @Getter(lazy = true)
    private final ReflectoFields fields = new ReflectoFields(
            () -> FieldsUtils.collectFields(this),
            () -> Stream.of(actualClass().getDeclaredFields()).map(ReflectoType.this::reflect).toList()
    );

    @Getter(lazy = true)
    private final ReflectoMethods methods = new ReflectoMethods(
            this,
            () -> MethodsUtils.collectMethods(this),
            () -> Stream.of(actualClass().getDeclaredMethods()).map(ReflectoType.this::reflect).toList()
    );

    @Getter(lazy = true)
    private final ReflectoConstructors constructors = new ReflectoConstructors(
            () -> Stream.of(this.actualClass().getConstructors()).map(this::reflect).toList(),
            parameterTypes -> this.reflect(this.actualClass().getConstructor(parameterTypes)),
            () -> Stream.of(this.actualClass().getDeclaredConstructors()).map(this::reflect).toList(),
            parameterTypes -> this.reflect(this.actualClass().getDeclaredConstructor(parameterTypes))
    );

    @Getter(lazy = true)
    private final ReflectoAnnotations annotations = new ReflectoAnnotations(
            () -> List.of(actualClass().getAnnotations()),
            () -> List.of(actualClass().getDeclaredAnnotations())
    );

    @Getter(lazy = true)
    private final ReflectoModifiers modifiers = new ReflectoModifiers(actualClass().getModifiers());

    @Getter(lazy = true)
    private final TypeArguments arguments = new TypeArguments();

    /**
     * Creates a root type reflection.
     *
     * @param type reflected type
     */
    public ReflectoType(Type type) {
        this(type, null);
    }

    /**
     * Creates a type reflection within a declaring type context.
     * <p>
     * The declaring type is used to resolve type variables.
     *
     * @param type reflected type
     * @param declaringType context used for generic resolution
     */
    public ReflectoType(Type type, ReflectoType declaringType) {
        this.rawType = type;
        this.declaringType = declaringType;
    }

    /**
     * Resolves a nested type expression.
     * <p>
     * Field names navigate through fields. Indexes such as {@code [0]} and
     * {@code [1]} navigate through generic type arguments of the current
     * parameterized type.
     *
     * <pre>{@code
     * ReflectoType dto = Reflecto.reflect(Types.type(Dto.class, Integer.class));
     *
     * dto.reflect("child.value").actualType();     // Integer
     * dto.reflect("map[1][0].value").actualType(); // Integer
     * }</pre>
     *
     * @param expression type navigation expression
     * @return resolved type
     */
    public ReflectoType reflect(String expression) {
        final List<String> items = splitExpression(expression);
        ReflectoType reflectoType = this;
        for (String exp : items) {
            final ReflectoType currentType = reflectoType;
            reflectoType = exp.startsWith("[")
                    ? reflectoType.arguments().get(parseIndex(exp))
                    : reflectoType.fields().find(exp).map(IsField::type).orElseThrow(() -> new NotFoundException(
                            "Field {0} not found while resolving expression {1} on type {2}. Available fields: {3}",
                            exp,
                            expression,
                            currentType.name(),
                            fieldNames(currentType.fields().stream())));
        }
        return reflectoType;
    }

    /**
     * Wraps an object instance using this type as its metadata context.
     *
     * @param instance runtime instance
     * @return object reflection
     */
    public Reflection reflect(Object instance) {
        return new ReflectoObject(instance, this);
    }

    /**
     * Creates a nested type reflection using this type as the declaring context.
     *
     * @param type nested type
     * @return nested type reflection
     */
    public ReflectoType reflect(Type type) {
        return new ReflectoType(type, this);
    }

    /**
     * Wraps a Java method and resolves it against this type hierarchy.
     *
     * @param method Java reflection method
     * @return method wrapper
     */
    public ReflectoMethod reflect(Method method) {
        return new ReflectoMethod(method, () -> findDeclaringClass(method.getDeclaringClass()));
    }

    /**
     * Wraps a Java field and resolves it against this type hierarchy.
     *
     * @param field Java reflection field
     * @return field wrapper
     */
    public ReflectoField reflect(Field field) {
        return new ReflectoField(field, () -> findDeclaringClass(field.getDeclaringClass()));
    }


    /**
     * Wraps a Java parameter and resolves it against this type hierarchy.
     *
     * @param parameter Java reflection parameter
     * @return parameter wrapper
     */
    public ReflectoParameter reflect(Parameter parameter) {
        return new ReflectoParameter(parameter, () -> findDeclaringClass(parameter.getDeclaringExecutable().getDeclaringClass()));
    }

    /**
     * Wraps a Java constructor and resolves it against this type hierarchy.
     *
     * @param constructor Java reflection constructor
     * @return constructor wrapper
     */
    public ReflectoConstructor reflect(Constructor<?> constructor) {
        return new ReflectoConstructor(constructor, () -> findDeclaringClass(constructor.getDeclaringClass()));
    }

    /**
     * Returns the resolved type name.
     *
     * @return resolved type name
     */
    public String name() {
        return actualType().getTypeName();
    }

    /**
     * Checks whether this type is assignable to the target type using
     * autoboxing.
     *
     * @param toType target type
     * @return {@code true} when this type can be assigned to {@code toType}
     */
    public boolean is(Type toType) {
        return is(toType, true);
    }

    /**
     * Checks whether this type is assignable to the target type.
     * <p>
     * Generic arguments are considered, so {@code List<String>} is assignable
     * to {@code Collection<String>} but not to {@code Collection<Long>}.
     *
     * @param toType target type
     * @param autoboxing whether primitive-wrapper autoboxing is allowed
     * @return {@code true} when this type can be assigned to {@code toType}
     */
    public boolean is(Type toType, boolean autoboxing) {
        return TypesUtils.isAssignable(actualType(), toType, autoboxing);
    }

    /**
     * Checks whether values of the supplied type can be assigned to this type
     * using autoboxing.
     *
     * @param fromType source type
     * @return {@code true} when {@code fromType} can be assigned to this type
     */
    public boolean isAssignableFrom(Type fromType) {
        return isAssignableFrom(fromType, true);
    }

    /**
     * Checks whether values of the supplied type can be assigned to this type.
     *
     * @param fromType source type
     * @param autoboxing whether primitive-wrapper autoboxing is allowed
     * @return {@code true} when {@code fromType} can be assigned to this type
     */
    public boolean isAssignableFrom(Type fromType, boolean autoboxing) {
        return TypesUtils.isAssignable(fromType, actualType(), autoboxing);
    }

    /**
     * Views this type as another class in its hierarchy and resolves generic
     * arguments for that view.
     *
     * <pre>{@code
     * ReflectoType list = Reflecto.reflect(Types.listOf(String.class));
     * ReflectoType iterable = list.as(Iterable.class);
     * iterable.arguments().get(0).actualType(); // String
     * }</pre>
     *
     * @param toClass target class or interface
     * @return type view as {@code toClass}
     */
    public ReflectoType as(Class<?> toClass) {
        final Map<TypeVariable<?>, Type> typeArguments = TypeUtils.getTypeArguments(actualType(), toClass);
        final ParameterizedType parameterize = TypeUtils.parameterize(toClass, typeArguments);
        return new ReflectoType(parameterize, declaringType);
    }

    /**
     * Checks whether the object is an instance of the resolved class.
     *
     * @param obj object to test
     * @return {@code true} when the object is an instance of this type
     */
    public boolean isInstance(Object obj) {
        return actualClass().isInstance(obj);
    }

    /**
     * @return {@code true} when the raw type is parameterized
     */
    public boolean isParametrized() {
        return rawType instanceof ParameterizedType;
    }

    /**
     * @return {@code true} when the resolved class is primitive
     */
    public boolean isPrimitive() {
        return actualClass().isPrimitive();
    }

    /**
     * @return {@code true} when this type is an array or generic array
     */
    public boolean isArray() {
        return rawType instanceof GenericArrayType || actualClass().isArray();
    }

    /**
     * Returns array-specific operations.
     *
     * @return array type view
     * @throws UnsupportedOperationException when this type is not an array
     */
    public ArrayType asArray() {
        if (!isArray()) {
            throw new UnsupportedOperationException("Type %s is not an array".formatted(name()));
        }
        return new ArrayType();
    }

    /**
     * @return {@code true} when the resolved class is an enum
     */
    public boolean isEnum() {
        return actualClass().isEnum();
    }

    /**
     * Returns enum-specific operations.
     *
     * @return enum type view
     * @throws UnsupportedOperationException when this type is not an enum
     */
    public EnumType asEnum() {
        if (!isEnum()) {
            throw new UnsupportedOperationException("Type %s is not an enum".formatted(name()));
        }
        return new EnumType();
    }

    /**
     * @return {@code true} when the resolved type is a type variable
     */
    public boolean isTypeVariable() {
        return actualType() instanceof TypeVariable;
    }

    /**
     * Equivalent to {@link Type#getTypeName()} for the resolved type.
     *
     * @return resolved type name
     */
    public String getTypeName() {
        return actualType().getTypeName();
    }

    /**
     * Returns the direct generic superclass, or {@code null} when none exists.
     *
     * @return direct superclass type or {@code null}
     */
    public ReflectoType superType() {
        return Optional.ofNullable(actualClass().getGenericSuperclass())
                .map(this::reflect)
                .orElse(null);
    }

    /**
     * Returns all generic superclasses from nearest to farthest.
     *
     * @return superclass chain
     */
    public List<ReflectoType> allSuperTypes() {
        final List<ReflectoType> allSuperTypes = new ArrayList<>();
        final ReflectoType superType = superType();
        if (superType != null) {
            allSuperTypes.add(superType);
            allSuperTypes.addAll(superType.allSuperTypes());
        }
        return allSuperTypes;
    }

    /**
     * Returns direct generic interfaces.
     *
     * @return direct interface types
     */
    public List<ReflectoType> interfaces() {
        return Stream.of(actualClass().getGenericInterfaces())
                .map(this::reflect)
                .toList();
    }

    /**
     * Returns interfaces implemented by this type, its superclasses, and its
     * interfaces recursively.
     *
     * @return complete interface list
     */
    public List<ReflectoType> allInterfaces() {

        final List<ReflectoType> allInterfaces = new ArrayList<>(interfaces());

        final ReflectoType superType = superType();
        if (superType != null) {
            allInterfaces.addAll(superType.allInterfaces());
        }

        interfaces().stream()
                .flatMap(i -> i.allInterfaces().stream())
                .forEach(allInterfaces::add);

        return allInterfaces;
    }

    private Class<?> determineActualClass() {
        final Type type = actualType();
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            return (Class<?>) ((ParameterizedType) type).getRawType();
        }
        if (type instanceof GenericArrayType) {
            return (Class<?>) Types.arrayOf(asArray().componentType().actualClass());
        }
        return null;
    }

    private Type determineActualType() {
        return declaringType == null ? rawType : TypesUtils.getActualType(rawType, declaringType.actualType());
    }

    /**
     * Streamable view of generic arguments for this type.
     */
    @RequiredArgsConstructor
    @Accessors(fluent = true)
    public class TypeArguments implements Streamable<ReflectoType> {

        @Getter(lazy = true)
        private final List<ReflectoType> list = collectArguments();

        private List<ReflectoType> collectArguments() {
            if (!isParametrized()) {
                return emptyList();
            }
            final ParameterizedType parameterizedType = (ParameterizedType) actualType();
            return Stream.of(parameterizedType.getActualTypeArguments())
                    .map(t -> new ReflectoType(t, declaringType))
                    .toList();
        }

    }

    /**
     * Array-specific operations for a {@link ReflectoType}.
     */
    public class ArrayType {

        /**
         * Returns the component type.
         *
         * @return component type
         */
        public ReflectoType componentType() {
            final Type result = actualType() instanceof GenericArrayType
                    ? ((GenericArrayType) actualType()).getGenericComponentType()
                    : actualClass().getComponentType();
            return new ReflectoType(result, declaringType);
        }

    }

    /**
     * Enum-specific operations for a {@link ReflectoType}.
     */
    public class EnumType {

        /**
         * Returns enum constants as a streamable collection.
         *
         * @param <E> enum type
         * @return enum constants wrapper
         */
        public <E extends Enum<E>> EnumConstants<E> constants() {
            return new EnumConstants<>();
        }

    }

    /**
     * Streamable view of enum constants with lookup helpers.
     *
     * @param <E> enum type
     */
    public class EnumConstants<E extends Enum<E>> implements Streamable<E> {

        @Override
        public List<E> list() {
            return (List<E>) List.of(actualClass().getEnumConstants());
        }

        /**
         * Finds an enum constant by name.
         *
         * @param name enum constant name
         * @return matching constant, if present
         */
        public Optional<E> find(String name) {
            return find(name, false);
        }

        /**
         * Finds an enum constant by name.
         *
         * @param name enum constant name
         * @param ignoreCase whether case should be ignored
         * @return matching constant, if present
         */
        public Optional<E> find(String name, boolean ignoreCase) {
            return stream()
                    .filter(e -> ignoreCase ? e.name().equalsIgnoreCase(name) : e.name().equals(name))
                    .findFirst();
        }

        /**
         * Returns an enum constant by name.
         *
         * @param name enum constant name
         * @return matching constant
         */
        public Object get(String name) {
            return get(name, false);
        }

        /**
         * Returns an enum constant by name.
         *
         * @param name enum constant name
         * @param ignoreCase whether case should be ignored
         * @return matching constant
         * @throws NotFoundException when no constant matches
         */
        public Object get(String name, boolean ignoreCase) {
            return find(name, ignoreCase)
                    .orElseThrow(() -> new NotFoundException("Enum value {0} of {1} not found. Available values: {2}",
                            name,
                            actualClass().getName(),
                            enumNames(stream())));
        }

    }

    @Override
    public String toString() {
        return rawType.toString();
    }

    private ReflectoType findDeclaringClass(Class<?> declaringClass) {
        return Stream.of(Stream.of(this), allSuperTypes().stream(), allInterfaces().stream())
                .flatMap(Function.identity())
                .filter(type -> declaringClass.equals(type.actualClass()))
                .findFirst()
                .orElseGet(() -> reflect(declaringClass));
    }
}
