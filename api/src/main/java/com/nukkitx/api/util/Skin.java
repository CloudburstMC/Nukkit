package com.nukkitx.api.util;

import com.google.common.base.Preconditions;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;

@Value
@Nonnull
@ToString(exclude = {"skinData", "capeData", "geometryData"})
public class Skin {
    @NonNull
    private final String skinId;
    @NonNull
    private final byte[] skinData;
    @NonNull
    private final byte[] capeData;
    @NonNull
    private final String geometryName;
    @NonNull
    private final byte[] geometryData;

    @Nonnull
    @ParametersAreNonnullByDefault
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

        return new Skin("Standard_Custom", mcpeTexture, null, "geometry.humanoid.custom", null);
    }
}
