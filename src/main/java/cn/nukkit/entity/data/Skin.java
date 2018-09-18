package cn.nukkit.entity.data;

import cn.nukkit.nbt.stream.FastByteArrayOutputStream;
import sun.misc.IOUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class Skin {
    private static final int PIXEL_SIZE = 4;

    public static final int SINGLE_SKIN_SIZE = 64 * 32 * PIXEL_SIZE;
    public static final int DOUBLE_SKIN_SIZE = 64 * 64 * PIXEL_SIZE;
    public static final int SKIN_128_64_SIZE = 128 * 64 * PIXEL_SIZE;
    public static final int SKIN_128_128_SIZE = 128 * 128 * PIXEL_SIZE;

    public static final String GEOMETRY_CUSTOM = "geometry.humanoid.custom";
    public static final String GEOMETRY_CUSTOM_SLIM = "geometry.humanoid.customSlim";

    private String skinId = "Steve";
    private byte[] skinData = new byte[SINGLE_SKIN_SIZE];
    private byte[] capeData = new byte[0];
    private String geometryName = GEOMETRY_CUSTOM;
    private String geometryData = null;

    public boolean isValid() {
        return isValidSkin(skinData.length);
    }

    public byte[] getSkinData() {
        return skinData;
    }

    public String getGeometryName() {
        return geometryName;
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

    public void setSkinData(BufferedImage image) {
        setSkinData(parseBufferedImage(image));
    }

    public void setSkinData(byte[] data) {
        if (data == null || !isValidSkin(data.length)) {
            throw new IllegalArgumentException("Invalid skin");
        }
        this.skinData = data;
    }

    public void setGeometryName(String model) {
        if (model == null || model.trim().isEmpty()) {
            model = GEOMETRY_CUSTOM;
        }

        this.geometryName = model;
    }

    public byte[] getCapeData() {
        return capeData;
    }

    public void setCapeData(BufferedImage image) {
        setCapeData(parseBufferedImage(image));
    }

    public void setCapeData(byte[] capeData) {
        if (capeData == null) {
            this.capeData = new byte[0];
        } else {
            this.capeData = capeData;
        }
    }

    public String getGeometryData() {
        return geometryData;
    }

    public void setGeometryData(InputStream stream) {
        try {
            setGeometryData(Base64.getEncoder().encodeToString(IOUtils.readFully(stream, -1, true)));
        } catch (IOException e) {
            // Ignore
        }
    }

    public void setGeometryData(String geometryData) {
        this.geometryData = geometryData;
    }

    private static byte[] parseBufferedImage(BufferedImage image) {
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
        return outputStream.toByteArray();
    }

    private static boolean isValidSkin(int length) {
        return length == SINGLE_SKIN_SIZE ||
                length == DOUBLE_SKIN_SIZE ||
                length == SKIN_128_64_SIZE ||
                length == SKIN_128_128_SIZE;
    }
}
