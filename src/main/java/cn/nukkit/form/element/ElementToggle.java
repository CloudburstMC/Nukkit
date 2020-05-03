package cn.nukkit.form.element;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.ToString;

import javax.annotation.Nonnull;

@ToString
public final class ElementToggle extends Element {

    @JsonProperty("default")
    private boolean defaultValue;

    public ElementToggle(@Nonnull String elementText) {
        super(ElementType.TOGGLE, elementText);
    }

    public ElementToggle(@Nonnull String elementText, boolean defaultValue) {
        super(ElementType.TOGGLE, elementText);
        this.defaultValue = defaultValue;
    }

    public boolean getDefaultValue() {
        return this.defaultValue;
    }
}
