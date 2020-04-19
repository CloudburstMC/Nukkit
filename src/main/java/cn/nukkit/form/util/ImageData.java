package cn.nukkit.form.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.ToString;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

@JsonSerialize(using = ImageData.ImageDataSerializer.class)
@ToString
public final class ImageData {

    @JsonProperty("type")
    private ImageType imageType;
    @JsonProperty("data")
    private String imageData;

    public ImageData() {
    }

    public ImageData(@Nullable ImageType imageType, @Nullable String imageData) {
        this.imageType = imageType;
        this.imageData = imageData;
    }

    @Nullable
    public ImageType getImageType() {
        return this.imageType;
    }

    @Nonnull
    public ImageData imageType(@Nullable ImageType imageType) {
        this.imageType = imageType;
        return this;
    }

    @Nullable
    public String getImageData() {
        return this.imageData;
    }

    @Nonnull
    public ImageData imageData(String imageData) {
        this.imageData = imageData;
        return this;
    }

    static final class ImageDataSerializer extends JsonSerializer<ImageData> {

        @Override
        public void serialize(ImageData imageData, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            if (imageData.getImageData() == null || imageData.getImageData().isEmpty() || imageData.getImageType() == null) {
                jsonGenerator.writeNull();
                return;
            }

            jsonGenerator.writeStartObject();
            jsonGenerator.writeObjectField("type", imageData.getImageType());
            jsonGenerator.writeStringField("data", imageData.getImageData());
            jsonGenerator.writeEndObject();
        }
    }

}