package com.cariochi.reflecto.utils;

import com.cariochi.reflecto.fields.ReflectoField;
import com.cariochi.reflecto.types.ReflectoType;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;

import static java.util.stream.Collectors.toList;

@UtilityClass
public class FieldsUtils {

    public static List<ReflectoField> collectFields(ReflectoType declaringType, boolean includeEnclosing) {
        final List<ReflectoField> allFields = new ArrayList<>();
        ReflectoType current = declaringType;
        while (current != null) {
            for (Field field : current.actualClass().getDeclaredFields()) {
                final ReflectoField reflectoField = new ReflectoField(field, current);
                if (includeEnclosing && reflectoField.isSynthetic()) {
                    allFields.addAll(getEnclosingFields(reflectoField));
                } else {
                    allFields.add(reflectoField);
                }
            }
            current = current.superType();
        }
        return allFields;
    }

    private static List<ReflectoField> getEnclosingFields(ReflectoField syntheticField) {

        final ArrayList<ReflectoField> enclosingFields = new ArrayList<>();

        final List<ReflectoField> fields = Stream.of(syntheticField.type().actualClass().getDeclaredFields())
                .map(field -> new ReflectoField(field, syntheticField.type()))
                .peek(field -> field.syntheticParent(syntheticField))
                .collect(toList());

        fields.stream()
                .filter(field -> !field.isSynthetic())
                .forEach(enclosingFields::add);

        fields.stream()
                .filter(ReflectoField::isSynthetic)
                .map(FieldsUtils::getEnclosingFields)
                .flatMap(List::stream)
                .forEach(enclosingFields::add);

        return enclosingFields;
    }

}
