package cn.nukkit.form.element;

import cn.nukkit.form.util.ImageData;
import cn.nukkit.form.util.ImageType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@ToString
@Getter
public final class ElementButton {

    @JsonIgnore
    private final ElementType elementType = ElementType.BUTTON;

    @JsonProperty("text")
    private final String buttonText;
    @JsonProperty("image")
    private final ImageData imageData;

    public ElementButton(@Nonnull String buttonText) {
        Preconditions.checkNotNull(buttonText, "The provided button text can not be null");

        this.buttonText = buttonText;
        this.imageData = new ImageData();
    }

    public ElementButton(@Nonnull String buttonText, @Nullable ImageType imageType, @Nullable String imageData) {
        Preconditions.checkNotNull(buttonText, "The provided button text can not be null");

        this.buttonText = buttonText;
        this.imageData = new ImageData(imageType, imageData);
    }
}
