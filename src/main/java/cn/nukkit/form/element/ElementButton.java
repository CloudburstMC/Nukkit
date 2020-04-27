package cn.nukkit.form.element;

import cn.nukkit.form.util.ImageData;
import cn.nukkit.form.util.ImageType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@ToString
public final class ElementButton {

    @JsonIgnore
    private final ElementType elementType = ElementType.BUTTON;

    @JsonProperty("text")
    private String buttonText;
    @JsonProperty("image")
    private ImageData imageData;

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

    @Nullable
    public String getButtonText() {
        return this.buttonText;
    }

    @Nonnull
    public ElementButton text(@Nonnull String buttonText) {
        Preconditions.checkNotNull(buttonText, "The provided button text can not be null");

        this.buttonText = buttonText;
        return this;
    }

    @Nonnull
    public ElementButton imageType(@Nullable ImageType imageType) {
        this.imageData.imageType(imageType);
        return this;
    }

    @Nonnull
    public ImageData getImageData() {
        return this.imageData;
    }

    @Nonnull
    public ElementButton imageData(@Nullable String imageData) {
        this.imageData.imageData(imageData);
        return this;
    }
}
