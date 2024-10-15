package com.cariochi.reflecto.fields;

import com.cariochi.reflecto.base.Streamable;
import java.util.List;
import java.util.Optional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Accessors(fluent = true)
public class TargetFields implements Streamable<TargetField> {

    private final ReflectoFields fields;

    @Getter
    private final Object target;

    @Getter(lazy = true)
    private final List<TargetField> list = fields.stream().map(field -> field.withTarget(target)).collect(toList());

    public Optional<TargetField> find(String name) {
        return fields.find(name)
                .map(f -> f.withTarget(target));
    }

    public TargetField get(String name) {
        return fields.get(name).withTarget(target);
    }
}
