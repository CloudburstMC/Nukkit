package cn.nukkit.form.util;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FormType {
    SIMPLE("form"),
    MODAL("modal"),
    CUSTOM("custom_form");

    private final String jsonName;

    @JsonValue
    public String getJsonName() {
        return jsonName;
    }
}
