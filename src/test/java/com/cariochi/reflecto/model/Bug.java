package com.cariochi.reflecto.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import static lombok.AccessLevel.PRIVATE;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Getter
@Setter(PRIVATE)
@SuperBuilder
public class Bug extends Issue implements TestInfoAware {

    private String[] tags;

}
