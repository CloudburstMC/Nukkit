package cn.nukkit.form.element;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;

public class Element {

    @JsonProperty("type")
    private final ElementType elementType;
    @JsonIgnore
    private String elementId;
    @JsonProperty("text")
    private String elementText;

    Element(@Nonnull ElementType elementType, @Nonnull String elementId, @Nonnull String elementText) {
        Preconditions.checkNotNull(elementType, "The provided element type can not be null");
        Preconditions.checkNotNull(elementId, "The provided element id can not be null");
        Preconditions.checkNotNull(elementText, "The provided element text can not be null");

        this.elementType = elementType;
        this.elementId = elementId;
        this.elementText = elementText;
    }

    @Nonnull
    public ElementType getElementType() {
        return this.elementType;
    }

    @Nonnull
    public String getElementId() {
        return this.elementId;
    }

    @Nonnull
    public Element id(@Nonnull String elementId) {
        Preconditions.checkNotNull(elementId, "The provided element id can not be null");
        this.elementId = elementId;
        return this;
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
