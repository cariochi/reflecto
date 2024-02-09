package com.cariochi.reflecto.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Enclosing {

    private final String summary;

    @Deprecated
    private final String deprecatedString = "has been";

    @Deprecated
    private final int deprecatedInt = -1;

    public First first = new First();

    public String methodFirst() {
        return "first";
    }

    public class First {

        public Second second = new Second();

        public class Second {

            public String third = "third";

            public String methodSecond() {
                return "second";
            }

        }

    }

}
