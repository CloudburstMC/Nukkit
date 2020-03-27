package cn.nukkit.item;

import cn.nukkit.player.Player;
import cn.nukkit.utils.Identifier;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.protocol.bedrock.packet.ClientboundMapItemDataPacket;
import lombok.extern.log4j.Log4j2;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by CreeperFace on 18.3.2017.
 */
@Log4j2
public class ItemMap extends Item {

    public static final AtomicLong mapIdAllocator = new AtomicLong(0);

    private static final String TAG_ID = "mapId"; // Long
    private static final String TAG_PARENT_ID = "parentMapId"; // Long
    private static final String TAG_DIMENSION = "dimension"; // Byte
    private static final String TAG_X_CENTER = "xCenter"; // Int
    private static final String TAG_Z_CENTER = "zCenter"; // Int
    private static final String TAG_SCALE = "scale"; // Byte
    private static final String TAG_UNLIMITED_TRACKING = "unlimitedTracking"; // Boolean
    private static final String TAG_PREVIEW_INCOMPLETE = "previewIncomplete"; // Boolean
    private static final String TAG_WIDTH = "width"; // Short
    private static final String TAG_HEIGHT = "height"; // Short
    private static final String TAG_COLORS = "colors"; // Byte array
    private static final String TAG_FULLY_EXPLORED = "fullyExplored"; // Boolean
    private static final String TAG_DECORATIONS = "decorations"; // List<CompoundTag>
    private static final String TAG_DECORATION_DATA = "data"; // CompoundTag - MapDecoration::load
    private static final String TAG_DECORATION_KEY = "key"; // CompoundTag - MapItemTrackedActor::UniqueId::load
    private static final String TAG_LOCKED = "mapLocked"; // Boolean

    private long mapId;
    private long parentMapId;
    private byte dimension;
    private int xCenter;
    private int zCenter;
    private int scale;
    private boolean unlimitedTracking;
    private boolean previewIncomplete;
    private short width;
    private short height;
    private int[] image = new int[0];
    private boolean fullyExplored;
    // private List<MapDecoration> decorations
    private boolean mapLocked;

    public ItemMap(Identifier id) {
        super(id);
        this.mapId = mapIdAllocator.getAndIncrement();
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        tag.listenForLong(TAG_ID, v -> this.mapId = v);
        tag.listenForLong(TAG_PARENT_ID, this::setParentMapId);
        tag.listenForByteArray(TAG_COLORS, this::setColors);
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        tag.longTag(TAG_ID, this.mapId);
        tag.byteArrayTag(TAG_COLORS, this.getColors());
    }

    public void setImage(File file) throws IOException {
        setImage(ImageIO.read(file));
    }

    protected BufferedImage getBufferedImage() {
        try {
            byte[] data = getColors();
            return ImageIO.read(new ByteArrayInputStream(data));
        } catch (IOException e) {
            log.error("Unable to load image from NBT", e);
        }

        return null;
    }

    public long getMapId() {
        return this.mapId;
    }

    public long getParentMapId() {
        return parentMapId;
    }

    public void setParentMapId(long parentMapId) {
        this.parentMapId = parentMapId;
    }

    public byte getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = (byte) dimension;
    }

    public int getXCenter() {
        return xCenter;
    }

    public void setXCenter(int xCenter) {
        this.xCenter = xCenter;
    }

    public int getZCenter() {
        return zCenter;
    }

    public void setZCenter(int zCenter) {
        this.zCenter = zCenter;
    }

    public int getScale() {
        return scale;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }

    public boolean isUnlimitedTracking() {
        return unlimitedTracking;
    }

    public void setUnlimitedTracking(boolean unlimitedTracking) {
        this.unlimitedTracking = unlimitedTracking;
    }

    public boolean isPreviewIncomplete() {
        return previewIncomplete;
    }

    public void setPreviewIncomplete(boolean previewIncomplete) {
        this.previewIncomplete = previewIncomplete;
    }

    public short getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = (short) width;
    }

    public short getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = (short) height;
    }

    public int[] getImage() {
        return image;
    }

    public void setImage(BufferedImage img) {
        try {
            BufferedImage image = img;

            if (img.getHeight() != 128 || img.getWidth() != 128) { //resize
                image = new BufferedImage(128, 128, img.getType());
                Graphics2D g = image.createGraphics();
                g.drawImage(img, 0, 0, 128, 128, null);
                g.dispose();
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);

            this.setColors(baos.toByteArray());
        } catch (IOException e) {
            log.error("Unable to set map image", e);
        }
    }

    public void setImage(int[] image) {
        this.image = Arrays.copyOf(image, image.length);
    }

    public byte[] getColors() {
        byte[] image = new byte[this.image.length * 4];
        for (int i = 0; i < this.image.length; i += 4) {
            int rgba = this.image[i];
            image[i] = (byte) ((rgba >>> 24) & 0xff);
            image[i + 1] = (byte) ((rgba >>> 16) & 0xff);
            image[i + 2] = (byte) ((rgba >>> 8) & 0xff);
            image[i + 3] = (byte) (rgba & 0xff);
        }
        return image;
    }

    public void setColors(byte[] colors) {
        checkNotNull(colors, "colors");
        int[] image = new int[colors.length / 4];
        for (int i = 0; i < image.length; i += 4) {
            image[i] = (colors[i] << 24) | (colors[i + 1] << 16) | (colors[i + 2] << 8) | colors[i + 3];
        }
        this.image = image;
    }

    public boolean isFullyExplored() {
        return fullyExplored;
    }

    public void setFullyExplored(boolean fullyExplored) {
        this.fullyExplored = fullyExplored;
    }

    public boolean isMapLocked() {
        return mapLocked;
    }

    public void setMapLocked(boolean mapLocked) {
        this.mapLocked = mapLocked;
    }

    public void sendImage(Player p) {
        ClientboundMapItemDataPacket packet = new ClientboundMapItemDataPacket();
        packet.setUniqueMapId(this.mapId);
        packet.setDimensionId(this.dimension);
        packet.setLocked(this.mapLocked);
        packet.setXOffset(this.xCenter);
        packet.setYOffset(this.zCenter);
        packet.setWidth(this.width);
        packet.setHeight(this.height);
        packet.setScale(this.scale);
        packet.setColors(this.image);

        p.sendPacket(packet);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
