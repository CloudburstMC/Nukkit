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
    @JsonIgnore
    private String elementId;

    @JsonProperty("text")
    private String buttonText;
    @JsonProperty("image")
    private ImageData imageData;

    @JsonIgnore
    private Runnable callback;

    public ElementButton(@Nonnull String elementId, @Nonnull String buttonText) {
        Preconditions.checkNotNull(elementId, "The provided element id can not be null");
        Preconditions.checkNotNull(buttonText, "The provided button text can not be null");

        this.elementId = elementId;
        this.buttonText = buttonText;
        this.imageData = new ImageData();
    }

    public ElementButton(@Nonnull String elementId, @Nonnull String buttonText, @Nullable ImageType imageType, @Nullable String imageData) {
        Preconditions.checkNotNull(elementId, "The provided element id can not be null");
        Preconditions.checkNotNull(buttonText, "The provided button text can not be null");

        this.elementId = elementId;
        this.buttonText = buttonText;
        this.imageData = new ImageData(imageType, imageData);
    }

    @Nonnull
    public String getElementId() {
        return this.elementId;
    }

    @Nonnull
    public ElementButton id(@Nonnull String elementId) {
        Preconditions.checkNotNull(elementId, "The provided element id can not be null");

        this.elementId = elementId;
        return this;
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

    public void triggerClick() {
        if (this.callback != null) {
            this.callback.run();
        }
    }

    @Nonnull
    public ElementButton onClick(@Nonnull Runnable callback) {
        Preconditions.checkNotNull(callback, "The provided answer callback can not be null");

        this.callback = callback;
        return this;
    }
}
