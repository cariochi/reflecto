package com.cariochi.reflecto.utils;

import com.cariochi.reflecto.fields.ReflectoField;
import com.cariochi.reflecto.types.ReflectoType;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class FieldsUtils {

    public static List<ReflectoField> collectFields(ReflectoType declaringType) {
        final List<ReflectoField> allFields = new ArrayList<>();
        ReflectoType current = declaringType;
        while (current != null) {
            for (Field field : current.actualClass().getDeclaredFields()) {
                allFields.add(current.reflect(field));
            }
            current = current.superType();
        }
        return allFields;
    }

}
