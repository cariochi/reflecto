package com.cariochi.reflecto.types;

import com.cariochi.reflecto.base.IsField;
import com.cariochi.reflecto.base.ReflectoAnnotations;
import com.cariochi.reflecto.base.ReflectoModifiers;
import com.cariochi.reflecto.base.Streamable;
import com.cariochi.reflecto.constructors.ReflectoConstructor;
import com.cariochi.reflecto.constructors.ReflectoConstructors;
import com.cariochi.reflecto.fields.ReflectoField;
import com.cariochi.reflecto.fields.ReflectoFields;
import com.cariochi.reflecto.invocations.model.Reflection;
import com.cariochi.reflecto.methods.ReflectoMethod;
import com.cariochi.reflecto.methods.ReflectoMethods;
import com.cariochi.reflecto.utils.FieldsUtils;
import com.cariochi.reflecto.utils.MethodsUtils;
import com.cariochi.reflecto.utils.TypesUtils;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.reflect.TypeUtils;

import static com.cariochi.reflecto.utils.ExpressionUtils.parseIndex;
import static com.cariochi.reflecto.utils.ExpressionUtils.splitExpression;
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
    private final ReflectoMethods methods = new ReflectoMethods(() -> MethodsUtils.collectMethods(this, false));

    @Getter(lazy = true)
    private final ReflectoConstructors constructors = new ReflectoConstructors(
            () -> Stream.of(actualClass().getConstructors()).map(constructor -> new ReflectoConstructor(constructor, this)).collect(toList()),
            parameterTypes -> new ReflectoConstructor(actualClass().getConstructor(parameterTypes), this)
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
                    : reflectoType.fields().find(exp).map(IsField::type).orElseThrow();
        }
        return reflectoType;
    }

    public String name() {
        return actualType().getTypeName();
    }

    public boolean is(Type type) {
        if(actualType() instanceof Class && type instanceof Class) {
            return ((Class<?>) type).isAssignableFrom(actualClass());
        }
        return TypeUtils.isAssignable(type instanceof Class ? actualClass() : actualType(), type);
    }

    public boolean isAssignableFrom(Type type) {
        if(actualType() instanceof Class && type instanceof Class) {
            return actualClass().isAssignableFrom((Class<?>) type);
        }
        return TypeUtils.isAssignable(type, type instanceof Class ? actualClass() : actualType());
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
        return actualClass().isArray();
    }

    public ArrayType asArray() {
        return new ArrayType();
    }

    public boolean isEnum() {
        return actualClass().isEnum();
    }

    public EnumType asEnum() {
        return new EnumType();
    }

    public boolean isTypeVariable() {
        return actualType() instanceof TypeVariable;
    }

    public Reflection reflect(Object instance) {
        return new ReflectoObject(instance, this);
    }

    public ReflectoType reflect(Type type) {
        return new ReflectoType(type, this);
    }

    public String getTypeName() {
        return actualType().getTypeName();
    }

    public ReflectoType superType() {
        return Optional.ofNullable(actualClass().getGenericSuperclass())
                .map(this::reflect)
                .orElse(null);
    }

    public List<ReflectoType> interfaces() {
        return Stream.of(actualClass().getGenericInterfaces())
                .map(this::reflect)
                .collect(toList());
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

        public List<Object> constants() {
            return asList(actualClass().getEnumConstants());
        }

    }

    public class Declared {

        @Getter(lazy = true)
        private final ReflectoConstructors constructors = new ReflectoConstructors(
                () -> Stream.of(actualClass().getDeclaredConstructors()).map(constructor -> new ReflectoConstructor(constructor, ReflectoType.this)).collect(toList()),
                parameterTypes -> new ReflectoConstructor(actualClass().getDeclaredConstructor(parameterTypes), ReflectoType.this)
        );


        @Getter(lazy = true)
        private final ReflectoFields fields = new ReflectoFields(
                () -> Stream.of(actualClass().getDeclaredFields())
                        .map(field -> new ReflectoField(field, ReflectoType.this))
                        .collect(toList())
        );


        @Getter(lazy = true)
        private final ReflectoMethods methods = new ReflectoMethods(
                () -> Stream.of(actualClass().getDeclaredMethods())
                        .map(method -> new ReflectoMethod(method, ReflectoType.this))
                        .collect(toList())
        );

        @Getter(lazy = true)
        private final ReflectoAnnotations annotations = new ReflectoAnnotations(() -> asList(actualClass().getDeclaredAnnotations()));

    }

    public class IncludeEnclosing {

        @Getter(lazy = true)
        private final ReflectoFields fields = new ReflectoFields(() -> FieldsUtils.collectFields(ReflectoType.this, true));

        @Getter(lazy = true)
        private final ReflectoMethods methods = new ReflectoMethods(() -> MethodsUtils.collectMethods(ReflectoType.this, true));

    }

    @Override
    public String toString() {
        return rawType.toString();
    }

}
