package cn.nukkit.form.element;

import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@ToString
public final class ElementInput extends Element {

    private String placeholder;
    private String defaultText;

    public ElementInput(@Nonnull String elementId, @Nonnull String elementText) {
        super(ElementType.INPUT, elementId, elementText);
    }

    public ElementInput(@Nonnull String elementId, @Nonnull String elementText, @Nullable String placeholder) {
        super(ElementType.INPUT, elementId, elementText);
        this.placeholder = placeholder;
    }

    public ElementInput(@Nonnull String elementId, @Nonnull String elementText, @Nullable String placeholder, @Nullable String defaultText) {
        super(ElementType.INPUT, elementId, elementText);
        this.placeholder = placeholder;
        this.defaultText = defaultText;
    }

    @Nullable
    public String getPlaceholder() {
        return this.placeholder;
    }

    @Nonnull
    public ElementInput placeholder(@Nullable String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    @Nullable
    public String getDefaultText() {
        return this.defaultText;
    }

    @Nonnull
    public ElementInput defaultText(@Nullable String defaultText) {
        this.defaultText = defaultText;
        return this;
    }
}
