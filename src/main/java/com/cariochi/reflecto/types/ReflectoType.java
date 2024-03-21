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
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

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
    private final ReflectoFields fields = new ReflectoFields(() -> FieldsUtils.collectFields(this, false));

    @Getter(lazy = true)
    private final ReflectoMethods methods = new ReflectoMethods(this, () -> MethodsUtils.collectMethods(this, false));

    @Getter(lazy = true)
    private final ReflectoConstructors constructors = new ReflectoConstructors(
        () -> Stream.of(actualClass().getConstructors()).map(this::reflect).collect(toList()),
        parameterTypes -> this.reflect(actualClass().getConstructor(parameterTypes))
    );

    @Getter(lazy = true)
    private final ReflectoAnnotations annotations = new ReflectoAnnotations(() -> asList(actualClass().getAnnotations()));

    @Getter(lazy = true)
    private final ReflectoModifiers modifiers = new ReflectoModifiers(actualClass().getModifiers());

    @Getter(lazy = true)
    private final TypeArguments arguments = new TypeArguments();

    @Getter
    private final Declared declared = new Declared();

    @Getter
    private final IncludeEnclosing includeEnclosing = new IncludeEnclosing();

    public ReflectoType(Type type) {
        this(type, null);
    }

    public ReflectoType(Type type, ReflectoType declaringType) {
        this.rawType = type;
        this.declaringType = declaringType;
    }

    public ReflectoType reflect(String expression) {
        final List<String> items = splitExpression(expression);
        ReflectoType reflectoType = this;
        for (String exp : items) {
            reflectoType = exp.startsWith("[")
                    ? reflectoType.arguments().get(parseIndex(exp))
                : reflectoType.fields().find(exp).map(IsField::type).orElseThrow(() -> new IllegalArgumentException(format("Field %s not found", exp)));
        }
        return reflectoType;
    }

    public Reflection reflect(Object instance) {
        return new ReflectoObject(instance, this);
    }

    public ReflectoType reflect(Type type) {
        return new ReflectoType(type, this);
    }

    public ReflectoMethod reflect(Method method) {
        return new ReflectoMethod(method, () -> findDeclaringClass(method.getDeclaringClass()));
    }

    public ReflectoField reflect(Field field) {
        return new ReflectoField(field, () -> findDeclaringClass(field.getDeclaringClass()));
    }


    public ReflectoParameter reflect(Parameter parameter) {
        return new ReflectoParameter(parameter, () -> findDeclaringClass(parameter.getDeclaringExecutable().getDeclaringClass()));
    }

    public ReflectoConstructor reflect(Constructor<?> constructor) {
        return new ReflectoConstructor(constructor, () -> findDeclaringClass(constructor.getDeclaringClass()));
    }

    public String name() {
        return actualType().getTypeName();
    }

    public boolean is(Type toType) {
        return is(toType, true);
    }

    public boolean is(Type toType, boolean autoboxing) {
        return TypesUtils.isAssignable(actualType(), toType, autoboxing);
    }

    public boolean isAssignableFrom(Type fromType) {
        return isAssignableFrom(fromType, true);
    }

    public boolean isAssignableFrom(Type fromType, boolean autoboxing) {
        return TypesUtils.isAssignable(fromType, actualType(), autoboxing);
    }

    public ReflectoType as(Class<?> toClass) {
        final Map<TypeVariable<?>, Type> typeArguments = TypeUtils.getTypeArguments(actualType(), toClass);
        final ParameterizedType parameterize = TypeUtils.parameterize(toClass, typeArguments);
        return new ReflectoType(parameterize, declaringType);
    }

    public boolean isInstance(Object obj) {
        return actualClass().isInstance(obj);
    }

    public boolean isParametrized() {
        return rawType instanceof ParameterizedType;
    }

    public boolean isPrimitive() {
        return actualClass().isPrimitive();
    }

    public boolean isArray() {
        return rawType instanceof GenericArrayType || actualClass().isArray();
    }

    public ArrayType asArray() {
        if (!isArray()) {
            throw new UnsupportedOperationException("Type is not array");
        }
        return new ArrayType();
    }

    public boolean isEnum() {
        return actualClass().isEnum();
    }

    public EnumType asEnum() {
        if (!isEnum()) {
            throw new UnsupportedOperationException("Type is not enum");
        }
        return new EnumType();
    }

    public boolean isTypeVariable() {
        return actualType() instanceof TypeVariable;
    }

    public String getTypeName() {
        return actualType().getTypeName();
    }

    public ReflectoType superType() {
        return Optional.ofNullable(actualClass().getGenericSuperclass())
                .map(this::reflect)
                .orElse(null);
    }

    public List<ReflectoType> allSuperTypes() {
        final List<ReflectoType> allSuperTypes = new ArrayList<>();
        final ReflectoType superType = superType();
        if (superType != null) {
            allSuperTypes.add(superType);
            allSuperTypes.addAll(superType.allSuperTypes());
        }
        return allSuperTypes;
    }

    public List<ReflectoType> interfaces() {
        return Stream.of(actualClass().getGenericInterfaces())
                .map(this::reflect)
                .collect(toList());
    }

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
                    .collect(toList());
        }

    }

    public class ArrayType {

        public ReflectoType componentType() {
            final Type result = actualType() instanceof GenericArrayType
                    ? ((GenericArrayType) actualType()).getGenericComponentType()
                    : actualClass().getComponentType();
            return new ReflectoType(result, declaringType);
        }

    }

    public class EnumType {

        public <E extends Enum<E>> EnumConstants<E> constants() {
            return new EnumConstants<>();
        }

    }

    public class EnumConstants<E extends Enum<E>> implements Streamable<E> {

        @Override
        public List<E> list() {
            return (List<E>) asList(actualClass().getEnumConstants());
        }

        public Optional<E> find(String name) {
            return find(name, false);
        }

        public Optional<E> find(String name, boolean ignoreCase) {
            return stream()
                .filter(e -> ignoreCase ? e.name().equalsIgnoreCase(name) : e.name().equals(name))
                .findFirst();
        }

        public Object get(String name) {
            return get(name, false);
        }

        public Object get(String name, boolean ignoreCase) {
            return find(name, ignoreCase)
                .orElseThrow(() -> new NotFoundException("Enum value {0} of {1} class not found", name, actualClass().getSimpleName()));
        }

    }

    public class Declared {

        @Getter(lazy = true)
        private final ReflectoConstructors constructors = new ReflectoConstructors(
            () -> Stream.of(actualClass().getDeclaredConstructors()).map(ReflectoType.this::reflect).collect(toList()),
            parameterTypes -> ReflectoType.this.reflect(actualClass().getDeclaredConstructor(parameterTypes))
        );


        @Getter(lazy = true)
        private final ReflectoFields fields = new ReflectoFields(
                () -> Stream.of(actualClass().getDeclaredFields())
                    .map(ReflectoType.this::reflect)
                        .collect(toList())
        );


        @Getter(lazy = true)
        private final ReflectoMethods methods = new ReflectoMethods(
            ReflectoType.this,
                () -> Stream.of(actualClass().getDeclaredMethods())
                    .map(ReflectoType.this::reflect)
                        .collect(toList())
        );

        @Getter(lazy = true)
        private final ReflectoAnnotations annotations = new ReflectoAnnotations(() -> asList(actualClass().getDeclaredAnnotations()));

    }

    public class IncludeEnclosing {

        @Getter(lazy = true)
        private final ReflectoFields fields = new ReflectoFields(() -> FieldsUtils.collectFields(ReflectoType.this, true));

        @Getter(lazy = true)
        private final ReflectoMethods methods = new ReflectoMethods(ReflectoType.this, () -> MethodsUtils.collectMethods(ReflectoType.this, true));

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
