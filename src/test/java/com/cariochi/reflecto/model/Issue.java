package com.cariochi.reflecto.model;

import java.util.List;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

import static lombok.AccessLevel.PRIVATE;

@EqualsAndHashCode
@ToString
@Getter
@Setter(PRIVATE)
@SuperBuilder
@Jacksonized
public class Issue {

    @Id
    private Integer id;

    private String summary;

    private User reporter;

    private List<User> watchers;

    private Map<String, String> details;

}
