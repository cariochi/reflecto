package com.cariochi.reflecto.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter(PRIVATE)
@SuperBuilder
@Jacksonized
@EqualsAndHashCode(callSuper = true)
public class Bug extends Issue {

    private String[] tags;

}
