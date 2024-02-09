package com.cariochi.reflecto;

import com.cariochi.reflecto.constructors.ReflectoConstructor;
import com.cariochi.reflecto.fields.ReflectoField;
import com.cariochi.reflecto.invocations.model.NullReflection;
import com.cariochi.reflecto.invocations.model.Reflection;
import com.cariochi.reflecto.methods.ReflectoMethod;
import com.cariochi.reflecto.parameters.ReflectoParameter;
import com.cariochi.reflecto.types.ReflectoObject;
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

    public static Reflection reflect(Object instance) {
        return instance == null ? new NullReflection() : new ReflectoObject(instance, reflect(instance.getClass()));
    }

    public static ReflectoType reflect(Type type) {
        return new ReflectoType(type);
    }

    public static ReflectoField reflect(Field field) {
        return new ReflectoField(field, reflect(field.getDeclaringClass()));
    }

    public static ReflectoMethod reflect(Method method) {
        return new ReflectoMethod(method, reflect(method.getDeclaringClass()));
    }

    public static ReflectoParameter reflect(Parameter parameter) {
        return new ReflectoParameter(parameter, reflect(parameter.getDeclaringExecutable().getDeclaringClass()));
    }

    public static ReflectoConstructor reflect(Constructor constructor) {
        return new ReflectoConstructor(constructor, reflect(constructor.getDeclaringClass()));
    }

}
