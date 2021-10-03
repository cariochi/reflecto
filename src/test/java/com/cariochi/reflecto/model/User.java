package com.cariochi.reflecto.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
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

}
