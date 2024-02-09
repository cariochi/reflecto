package com.cariochi.reflecto;

import com.cariochi.reflecto.model.Bug;
import com.cariochi.reflecto.model.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestData {

    private static final String[] TAGS = {"role", "user", "auth"};

    public static Bug bug() {
        return Bug.builder()
                .id(1)
                .summary("Invalid value")
                .reporter(new User(100, "qa"))
                .watchers(new ArrayList<>(List.of(
                        new User(101, "developer"),
                        new User(102, "manager")
                )))
                .details(new HashMap<>(Map.of(
                        "Sprint", "SPR-001",
                        "Component", "Authorization"
                )))
                .tags(TAGS)
                .build();
    }

    public static Bug modifiedBug() {
        return Bug.builder()
                .id(1)
                .summary("Modified bug")
                .reporter(new User(100, "qa"))
                .watchers(new ArrayList<>(List.of(
                        new User(1001, "java-dev"),
                        new User(1002, "pm")
                )))
                .details(new HashMap<>(Map.of(
                        "Sprint", "SPR-002",
                        "Component", "Authorization"
                )))
                .tags(TAGS)
                .build();
    }


}
