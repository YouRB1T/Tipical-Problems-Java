package com.tipicalproblemsjava.phone;

public class PhoneNotFoundException extends RuntimeException {

    public PhoneNotFoundException(Long id) {
        super("Телефон с id %d не был найден".formatted(id));
    }
}
