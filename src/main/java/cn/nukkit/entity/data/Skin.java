package cn.nukkit.entity.data;

import cn.nukkit.nbt.stream.FastByteArrayOutputStream;
import cn.nukkit.utils.SerializedImage;
import cn.nukkit.utils.SkinAnimation;
import com.google.common.base.Preconditions;
import lombok.ToString;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class Skin {
    private static final int PIXEL_SIZE = 4;

    public static final int SINGLE_SKIN_SIZE = 64 * 32 * PIXEL_SIZE;
    public static final int DOUBLE_SKIN_SIZE = 64 * 64 * PIXEL_SIZE;
    public static final int SKIN_128_64_SIZE = 128 * 64 * PIXEL_SIZE;
    public static final int SKIN_128_128_SIZE = 128 * 128 * PIXEL_SIZE;

    public static final String GEOMETRY_CUSTOM = convertLegacyGeometryName("geometry.humanoid.custom");
    public static final String GEOMETRY_CUSTOM_SLIM = convertLegacyGeometryName("geometry.humanoid.customSlim");

    private String skinId;
    private String skinResourcePatch;
    private SerializedImage skinData;
    private final List<SkinAnimation> animations = new ArrayList<>();
    private SerializedImage capeData;
    private String geometryData;
    private String animationData;
    private boolean premium;
    private boolean persona;
    private boolean capeOnClassic;
    private String capeId;

    public boolean isValid() {
        return true; //TODO: some sort of validation
    }

    public SerializedImage getSkinData() {
        if (skinData == null) {
            return SerializedImage.EMPTY;
        }
        return skinData;
    }

    public String getSkinId() {
        return skinId;
    }

    public void setSkinId(String skinId) {
        if (skinId == null || skinId.trim().isEmpty()) {
            return;
        }
        this.skinId = skinId;
    }

    public void setSkinData(byte[] skinData) {
        setSkinData(SerializedImage.fromLegacy(skinData));
    }

    public void setSkinData(BufferedImage image) {
        setSkinData(parseBufferedImage(image));
    }

    public void setSkinData(SerializedImage skinData) {
        Objects.requireNonNull(skinData, "skinData");
        this.skinData = skinData;
    }

    public void setSkinResourcePatch(String skinResourcePatch) {
        if (skinResourcePatch == null || skinResourcePatch.trim().isEmpty()) {
            skinResourcePatch = GEOMETRY_CUSTOM;
            return;
        }
        this.skinResourcePatch = skinResourcePatch;
    }

    public void setGeometryName(String geometryName) {
        if (geometryName == null || geometryName.trim().isEmpty()) {
            skinResourcePatch = GEOMETRY_CUSTOM;
            return;
        }

        this.skinResourcePatch = "{\"geometry\" : {\"default\" : \"" + geometryName + "\"}}";
    }

    public String getSkinResourcePatch() {
        if (this.skinResourcePatch == null) {
            return "";
        }
        return skinResourcePatch;
    }

    public SerializedImage getCapeData() {
        if (capeData == null) {
            return SerializedImage.EMPTY;
        }
        return capeData;
    }

    public String getCapeId() {
        if (capeId == null) {
            return "";
        }
        return capeId;
    }

    public void setCapeId(String capeId) {
        if (capeId == null || capeId.trim().isEmpty()) {
            capeId = null;
        }
        this.capeId = capeId;
    }

    public void setCapeData(byte[] capeData) {
        Objects.requireNonNull(capeData, "capeData");
        Preconditions.checkArgument(capeData.length == SINGLE_SKIN_SIZE, "Invalid legacy cape");
        setCapeData(new SerializedImage(64, 32, capeData));
    }

    public void setCapeData(BufferedImage image) {
        setCapeData(parseBufferedImage(image));
    }

    public void setCapeData(SerializedImage capeData) {
        Objects.requireNonNull(capeData, "capeData");
        this.capeData = capeData;
    }

    public String getGeometryData() {
        if (geometryData == null) {
            return "";
        }
        return geometryData;
    }

    public void setGeometryData(String geometryData) {
        Preconditions.checkNotNull(geometryData, "geometryData");
        if (!geometryData.equals(this.geometryData)) {
            this.geometryData = geometryData;
        }
    }

    public String getAnimationData() {
        if (animationData == null) {
            return "";
        }
        return animationData;
    }

    public void setAnimationData(String animationData) {
        Preconditions.checkNotNull(animationData, "animationData");
        if (!animationData.equals(this.animationData)) {
            this.animationData = animationData;
        }
    }

    public List<SkinAnimation> getAnimations() {
        return animations;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public boolean isPersona() {
        return persona;
    }

    public void setPersona(boolean persona) {
        this.persona = persona;
    }

    public boolean isCapeOnClassic() {
        return capeOnClassic;
    }

    public void setCapeOnClassic(boolean capeOnClassic) {
        this.capeOnClassic = capeOnClassic;
    }

    public String getFullSkinId() {
        return getSkinId() + '_' + getCapeId();
    }

    private static SerializedImage parseBufferedImage(BufferedImage image) {
        FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                Color color = new Color(image.getRGB(x, y), true);
                outputStream.write(color.getRed());
                outputStream.write(color.getGreen());
                outputStream.write(color.getBlue());
                outputStream.write(color.getAlpha());
            }
        }
        image.flush();
        return new SerializedImage(image.getWidth(), image.getHeight(), outputStream.toByteArray());
    }

    private static boolean isValidSkin(int length) {
        return length == SINGLE_SKIN_SIZE ||
                length == DOUBLE_SKIN_SIZE ||
                length == SKIN_128_64_SIZE ||
                length == SKIN_128_128_SIZE;
    }

    private static String convertLegacyGeometryName(String geometryName) {
        return "{\"geometry\" : {\"default\" : \"" + geometryName + "\"}}";
    }
}
