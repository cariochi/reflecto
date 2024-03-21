package com.cariochi.reflecto.base;

import com.cariochi.reflecto.types.ReflectoType;
import java.lang.reflect.Executable;
import java.util.List;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Accessors(fluent = true)
public class ReflectoExceptions implements Streamable<ReflectoType> {

    private final Executable executable;
    private final ReflectoType declaringType;

    @Getter(lazy = true)
    private final List<ReflectoType> list = Stream.of(executable.getGenericExceptionTypes())
            .map(declaringType::reflect)
            .collect(toList());

}
