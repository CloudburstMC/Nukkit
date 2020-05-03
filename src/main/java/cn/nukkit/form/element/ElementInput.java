package cn.nukkit.form.element;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@ToString
@Getter
public final class ElementInput extends Element {

    private String placeholder;
    @JsonProperty("default")
    private String defaultText;

    public ElementInput(@Nonnull String elementText) {
        super(ElementType.INPUT, elementText);
    }

    public ElementInput(@Nonnull String elementText, @Nullable String placeholder) {
        super(ElementType.INPUT, elementText);
        this.placeholder = placeholder;
    }

    public ElementInput(@Nonnull String elementText, @Nullable String placeholder, @Nullable String defaultText) {
        super(ElementType.INPUT, elementText);
        this.placeholder = placeholder;
        this.defaultText = defaultText;
    }
}
