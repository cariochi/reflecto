package com.cariochi.reflecto;

import com.cariochi.reflecto.constructors.ReflectoConstructor;
import com.cariochi.reflecto.fields.ReflectoField;
import com.cariochi.reflecto.invocations.model.NullReflection;
import com.cariochi.reflecto.invocations.model.Reflection;
import com.cariochi.reflecto.methods.ReflectoMethod;
import com.cariochi.reflecto.parameters.ReflectoParameter;
import com.cariochi.reflecto.proxy.ProxyType;
import com.cariochi.reflecto.proxy.ReflectoProxy;
import com.cariochi.reflecto.types.ReflectoType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import lombok.RequiredArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@RequiredArgsConstructor(access = PRIVATE)
public class Reflecto {

    public static ReflectoType reflect(Type type) {
        return new ReflectoType(type);
    }

    public static Reflection reflect(Object instance) {
        return instance == null ? new NullReflection() : reflect(instance.getClass()).reflect(instance);
    }

    public static ReflectoField reflect(Field field) {
        return reflect(field.getDeclaringClass()).reflect(field);
    }

    public static ReflectoMethod reflect(Method method) {
        return reflect(method.getDeclaringClass()).reflect(method);
    }

    public static ReflectoParameter reflect(Parameter parameter) {
        return reflect(parameter.getDeclaringExecutable().getDeclaringClass()).reflect(parameter);
    }

    public static ReflectoConstructor reflect(Constructor<?> constructor) {
        return reflect(constructor.getDeclaringClass()).reflect(constructor);
    }

    public static ProxyType proxy(Type... types) {
        return ReflectoProxy.createTypeProxy(types);
    }

}
