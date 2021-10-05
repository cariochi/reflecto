package com.cariochi.reflecto.model;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import static lombok.AccessLevel.PRIVATE;

@Getter
@Setter(PRIVATE)
@Builder
@Jacksonized
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private Integer id;

    private String username;

    @Id
    public Integer getId() {
        return id;
    }

    private String sayHello(String name) {
        return "Hello " + name + " from " + username;
    }

}
