package com.example.fluffstroller.models;

public enum UserType {
    DOG_OWNER,
    STROLLER;

    public static UserType convertString(String user) {
        user = user.toUpperCase();

        if (user.contains("STROLLER")) {
            return UserType.STROLLER;
        }

        return UserType.DOG_OWNER;
    }
}
