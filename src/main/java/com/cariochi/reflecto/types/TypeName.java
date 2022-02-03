package com.cariochi.reflecto.types;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;
import static org.apache.commons.lang3.StringUtils.countMatches;
import static org.apache.commons.lang3.StringUtils.substringBefore;

@Getter
@EqualsAndHashCode
@NoArgsConstructor(access = PRIVATE)
public final class TypeName {

    private static final Set<String> PRIMITIVE_TYPES = Set.of("byte", "short", "int", "long", "float", "double", "char", "boolean");

    private String name;

    private int dimension;

    @EqualsAndHashCode.Exclude
    private TypeName parent;

    private final List<TypeName> arguments = new ArrayList<>();

    public static TypeName of(Type type, Type... typeArguments) {
        final TypeName typeName = new TypeName();
        typeName.name = substringBefore(type.getTypeName(), "[");
        typeName.arguments.addAll(Stream.of(typeArguments)
                .map(Type::getTypeName)
                .map(TypeName::parse)
                .peek(a -> a.parent = typeName)
                .collect(toList())
        );
        typeName.dimension = countMatches(type.getTypeName(), "[");
        return typeName;
    }

    public static TypeName parse(String type) {
        final StringTokenizer tokenizer = new StringTokenizer(type, ",<>[]", true);
        TypeName typeName = new TypeName();
        while (tokenizer.hasMoreTokens()) {
            final String token = tokenizer.nextToken();
            if ("<".equals(token)) {
                typeName = typeName.add();
            } else if (">".equals(token)) {
                typeName = typeName.getParent();
            } else if (",".equals(token)) {
                typeName = typeName.getParent().add();
            } else if ("[".equals(token)) {
                typeName.dimension++;
            } else if ("]".equals(token)) {
            } else {
                typeName.name = token.trim();
            }
        }
        return typeName;
    }

    public TypeName withParent(Class<?> type) {
        final TypeName parent = new TypeName();
        parent.name = substringBefore(type.getTypeName(), "[");
        parent.arguments.add(this);
        parent.dimension = countMatches(type.getTypeName(), "[");
        this.parent = parent;
        return parent;
    }

    public boolean isPrimitive() {
        return PRIMITIVE_TYPES.contains(name);
    }

    public List<TypeName> getArguments() {
        return List.copyOf(arguments);
    }

    public TypeName add() {
        final TypeName argument = new TypeName();
        argument.parent = this;
        arguments.add(argument);
        return argument;
    }

    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(name);
        if (!arguments.isEmpty()) {
            buffer.append("<")
                    .append(arguments.stream().map(Objects::toString).collect(joining(", ")))
                    .append(">");
        }
        buffer.append("[]".repeat(dimension));
        return buffer.toString();
    }

}
