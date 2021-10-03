package com.cariochi.reflecto.model;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class Issue {

    @Id
    private Integer id;

    private String summary;

    private User reporter;

    private List<User> watchers;

    private Map<String, String> details;

}
