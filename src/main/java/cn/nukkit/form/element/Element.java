package cn.nukkit.form.element;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;

public class Element {

    @JsonProperty("type")
    private final ElementType elementType;
    @JsonProperty("text")
    private String elementText;

    Element(@Nonnull ElementType elementType, @Nonnull String elementText) {
        Preconditions.checkNotNull(elementType, "The provided element type can not be null");
        Preconditions.checkNotNull(elementText, "The provided element text can not be null");

        this.elementType = elementType;
        this.elementText = elementText;
    }

    @Nonnull
    public ElementType getElementType() {
        return this.elementType;
    }

    @Nonnull
    public String getElementText() {
        return this.elementText;
    }

    @Nonnull
    public Element text(@Nonnull String elementText) {
        Preconditions.checkNotNull(elementText, "The provided element text can not be null");
        this.elementText = elementText;
        return this;
    }
}
