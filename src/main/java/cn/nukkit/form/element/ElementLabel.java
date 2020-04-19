package cn.nukkit.form.element;

import lombok.ToString;

import javax.annotation.Nonnull;

@ToString
public final class ElementLabel extends Element {

    public ElementLabel(@Nonnull String elementId, @Nonnull String elementText) {
        super(ElementType.LABEL, elementId, elementText);
    }
}
