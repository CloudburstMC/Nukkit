package cn.nukkit.form.element;

import lombok.ToString;

import javax.annotation.Nonnull;

@ToString
public final class ElementToggle extends Element {

    private boolean defaultValue;

    public ElementToggle(@Nonnull String elementId, @Nonnull String elementText) {
        super(ElementType.TOGGLE, elementId, elementText);
    }

    public ElementToggle(@Nonnull String elementId, @Nonnull String elementText, boolean defaultValue) {
        super(ElementType.TOGGLE, elementId, elementText);
        this.defaultValue = defaultValue;
    }

    public boolean isDefaultValue() {
        return this.defaultValue;
    }

    @Nonnull
    public ElementToggle defaultValue(boolean defaultValue) {
        this.defaultValue = defaultValue;
        return this;
    }
}
