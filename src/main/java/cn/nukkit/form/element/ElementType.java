package cn.nukkit.form.element;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

@ToString
public enum ElementType {

    @JsonProperty("button")
    BUTTON,
    @JsonProperty("dropdown")
    DROPDOWN,
    @JsonProperty("input")
    INPUT,
    @JsonProperty("label")
    LABEL,
    @JsonProperty("slider")
    SLIDER,
    @JsonProperty("step_slider")
    STEP_SLIDER,
    @JsonProperty("toggle")
    TOGGLE;
}
