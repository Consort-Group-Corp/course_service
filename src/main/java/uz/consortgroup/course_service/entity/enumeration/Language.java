package uz.consortgroup.course_service.entity.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Language {
    RUSSIAN("RU"),
    ENGLISH("EN"),
    UZBEK("UZ");

    private final String value;

    Language(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static Language fromValue(String value) {
        for (Language language : Language.values()) {
            if (language.value.equalsIgnoreCase(value)) {
                return language;
            }
        }
        throw new IllegalArgumentException("Unknown language: " + value);
    }
}
