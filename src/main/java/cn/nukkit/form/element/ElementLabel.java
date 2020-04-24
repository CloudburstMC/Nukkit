package cn.nukkit.form.element;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ElementLabel extends Element {

    @JsonProperty
    private final String type = "label"; //This variable is used for JSON import operations. Do NOT delete :) -- @Snake1999
    private String text = "";

    public ElementLabel(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
