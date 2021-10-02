package com.cariochi.reflecto.methods;

import lombok.Value;
import lombok.experimental.Accessors;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.substringBefore;

@Value
@Accessors(fluent = true)
public class Invocation {

    String name;
    Object[] args;
    boolean isMethod;

    public Invocation(String name, List<Object> params) {
        this.isMethod = name.contains("(");
        this.name = substringBefore(name, "(");
        this.args = params.toArray();
    }

}
