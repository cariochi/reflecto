package com.cariochi.reflecto.exceptions;

import java.text.MessageFormat;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message, Object... arguments) {
        super(MessageFormat.format(message, arguments));
    }
}
