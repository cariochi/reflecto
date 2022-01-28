package com.cariochi.reflecto.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import static lombok.AccessLevel.PRIVATE;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@Setter(PRIVATE)
@SuperBuilder
@Jacksonized
public class Bug extends Issue {

    private String[] tags;

}
