package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.ClientboundMapItemDataPacket;
import cn.nukkit.utils.MainLogger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by CreeperFace on 18.3.2017.
 */
public class ItemMap extends Item {

    public static int mapCount = 0;

    public ItemMap() {
        this(0, 1);
    }

    public ItemMap(Integer meta) {
        this(meta, 1);
    }

    public ItemMap(Integer meta, int count) {
        super(MAP, 0, count, "Map");

        if (!hasCompoundTag() || !getNamedTag().contains("map_uuid")) {
            CompoundTag tag = new CompoundTag();
            tag.putString("map_uuid", "" + mapCount++);
            this.setNamedTag(tag);
        }
    }

    public void setImage(File file) throws IOException {
        setImage(ImageIO.read(file));
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

            this.getNamedTag().putByteArray("Colors", baos.toByteArray());
        } catch (IOException e) {
            MainLogger.getLogger().logException(e);
        }
    }

    protected BufferedImage loadImageFromNBT() {
        try {
            byte[] data = getNamedTag().getByteArray("Colors");
            return ImageIO.read(new ByteArrayInputStream(data));
        } catch (IOException e) {
            MainLogger.getLogger().logException(e);
        }

        return null;
    }

    public long getMapId() {
        return Long.valueOf(getNamedTag().getString("map_uuid"));
    }

    public void sendImage(Player p) {
        BufferedImage image = loadImageFromNBT();

        ClientboundMapItemDataPacket pk = new ClientboundMapItemDataPacket();
        pk.mapId = getMapId();
        pk.update = 2;
        pk.scale = 0;
        pk.width = 128;
        pk.height = 128;
        pk.offsetX = 0;
        pk.offsetZ = 0;
        pk.image = image;

        p.dataPacket(pk);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, int face, double fx, double fy, double fz) {
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
