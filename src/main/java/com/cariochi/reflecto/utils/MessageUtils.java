package com.cariochi.reflecto.utils;

import com.cariochi.reflecto.base.IsField;
import com.cariochi.reflecto.base.IsMethod;
import com.cariochi.reflecto.constructors.ReflectoConstructor;
import com.cariochi.reflecto.types.ReflectoType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MessageUtils {

    public static List<String> typeNames(Type... types) {
        return Stream.of(types)
                .map(type -> type == null ? "null" : type.getTypeName())
                .toList();
    }

    public static List<String> fieldNames(Stream<? extends IsField> fields) {
        return fields.map(IsField::name).toList();
    }

    public static List<String> typeNames(Stream<ReflectoType> types) {
        return types.map(ReflectoType::name).toList();
    }

    public static List<String> annotationNames(Stream<Annotation> annotations) {
        return annotations
                .map(annotation -> annotation.annotationType().getName())
                .toList();
    }

    public static List<String> enumNames(Stream<? extends Enum<?>> values) {
        return values.map(Enum::name).toList();
    }

    public static List<?> constructors(Stream<ReflectoConstructor> constructors) {
        return constructors.map(ReflectoConstructor::rawConstructor).toList();
    }

    public static List<?> methods(Stream<? extends IsMethod> methods) {
        return methods.map(IsMethod::rawMethod).toList();
    }
}
