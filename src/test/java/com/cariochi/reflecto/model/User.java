package com.cariochi.reflecto.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import static lombok.AccessLevel.PRIVATE;

@EqualsAndHashCode
@ToString
@Getter
@Setter(PRIVATE)
@Builder
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
