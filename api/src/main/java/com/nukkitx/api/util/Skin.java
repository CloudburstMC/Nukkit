package com.nukkitx.api.util;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Objects;

@Immutable
@ParametersAreNonnullByDefault
public class Skin {
    private final String skinId;
    private final byte[] skinData;
    private final byte[] capeData;
    private final String geometryName;
    private final String geometryData;

    public Skin(String skinId, byte[] skinData, byte[] capeData, String geometryName, String geometryData) {
        this.skinId = Preconditions.checkNotNull(skinId, "skinId");
        this.skinData = Preconditions.checkNotNull(skinData, "skinData");
        this.capeData = Preconditions.checkNotNull(capeData, "capeData");
        this.geometryName = Preconditions.checkNotNull(geometryName, "geometryName");
        this.geometryData = Preconditions.checkNotNull(geometryData, "geometryData");
    }

    @Nonnull
    public static Skin create(BufferedImage skin) {
        Preconditions.checkNotNull(skin, "image");
        Preconditions.checkArgument(skin.getHeight() == 32 && skin.getWidth() == 64, "Image is not 32x64");

        byte[] mcpeTexture = new byte[32 * 64 * 4];

        int at = 0;
        for (int i = 0; i < skin.getHeight(); i++) {
            for (int i1 = 0; i1 < skin.getWidth(); i1++) {
                int rgb = skin.getRGB(i, i1);
                mcpeTexture[at++] = (byte) ((rgb & 0x00ff0000) >> 16);
                mcpeTexture[at++] = (byte) ((rgb & 0x0000ff00) >> 8);
                mcpeTexture[at++] = (byte) (rgb & 0x000000ff);
                mcpeTexture[at++] = (byte) ((rgb >> 24) & 0xff);
            }
        }

        return new Skin("Standard_Custom", mcpeTexture, new byte[0], "geometry.humanoid.custom", "");
    }

    public String getSkinId() {
        return skinId;
    }

    public byte[] getCapeData() {
        return capeData;
    }

    public byte[] getSkinData() {
        return skinData;
    }

    public String getGeometryName() {
        return geometryName;
    }

    public String getGeometryData() {
        return geometryData;
    }

    public boolean equals(final Object o) {
        if (o == this) return true;
        if (!(o instanceof Skin)) return false;
        final Skin that = (Skin) o;
        return Objects.equals(this.skinId, that.skinId) &&
                Arrays.equals(this.skinData, that.skinData) &&
                Arrays.equals(this.capeData, that.capeData) &&
                Objects.equals(this.geometryName, that.geometryName) &&
                Objects.equals(this.geometryData, that.geometryData);
    }

    protected boolean canEqual(final Object other) {
        return other instanceof Skin;
    }

    public int hashCode() {
        return Objects.hash(skinId, skinData, capeData, geometryName, geometryData);
    }

    public String toString() {
        return "Skin(skinId=" + skinId + ", geometryName=" + geometryName + ')';
    }
}
