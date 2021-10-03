package com.cariochi.reflecto.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Bug extends Issue {

    private String[] tags;

}
