package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.Server;
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

    public static long mapCount = 0;

    private BufferedImage image;

    public ItemMap() {
        this(0, 1);
    }

    public ItemMap(Integer meta) {
        this(meta, 1);
    }

    public ItemMap(Integer meta, int count) {
        super(MAP, meta, count, "Map");
    }

    public void setImage(File file) throws IOException {
        setImage(ImageIO.read(file));
    }

    public void setImage(BufferedImage image) {
        try {
            if (this.getMapId() == 0) {
                Server.getInstance().getLogger().debug("Uninitialized map", new Throwable(""));
                this.initItem();
            }

            if (image.getHeight() != 128 || image.getWidth() != 128) {
                this.image = new BufferedImage(128, 128, image.getType());
                Graphics2D g = this.image.createGraphics();
                g.drawImage(image, 0, 0, 128, 128, null);
                g.dispose();
            } else {
                this.image = image;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(this.image, "png", baos);

            this.setNamedTag(this.getNamedTag().putByteArray("Colors", baos.toByteArray()));
            baos.close();
        } catch (IOException e) {
            MainLogger.getLogger().logException(e);
        }
    }

    protected BufferedImage loadImageFromNBT() {
        try {
            byte[] data = getNamedTag().getByteArray("Colors");
            image = ImageIO.read(new ByteArrayInputStream(data));
            return image;
        } catch (IOException e) {
            MainLogger.getLogger().logException(e);
        }

        return null;
    }

    public long getMapId() {
        CompoundTag tag = this.getNamedTag();
        if (tag == null) return 0;
        return tag.getLong("map_uuid");
    }

    public void sendImage(Player p) {
        // Don't load the image from NBT if it has been done before
        BufferedImage image = this.image != null ? this.image : loadImageFromNBT();

        ClientboundMapItemDataPacket pk = new ClientboundMapItemDataPacket();
        pk.mapId = getMapId();
        pk.update = ClientboundMapItemDataPacket.TEXTURE_UPDATE;
        pk.scale = 0;
        pk.width = 128;
        pk.height = 128;
        pk.offsetX = 0;
        pk.offsetZ = 0;
        pk.image = image;
        pk.eids = new long[]{pk.mapId};

        p.dataPacket(pk);
    }

    public boolean trySendImage(Player p) {
        // Don't load the image from NBT if it has been done before
        BufferedImage image = this.image != null ? this.image : loadImageFromNBT();
        if (image == null) {
            return false;
        }

        ClientboundMapItemDataPacket pk = new ClientboundMapItemDataPacket();
        pk.mapId = getMapId();
        pk.update = ClientboundMapItemDataPacket.TEXTURE_UPDATE;
        pk.scale = 0;
        pk.width = 128;
        pk.height = 128;
        pk.offsetX = 0;
        pk.offsetZ = 0;
        pk.image = image;
        pk.eids = new long[]{pk.mapId};

        p.dataPacket(pk);
        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 1; // TODO: 64 when map copying is implemented
    }

    @Override
    public Item initItem() {
        CompoundTag compoundTag = this.getNamedTag();
        if (compoundTag == null || !compoundTag.contains("map_uuid")) {
            CompoundTag tag = new CompoundTag();
            mapCount++;
            tag.putLong("map_uuid", mapCount);
            tag.putInt("map_name_index", (int) mapCount);
            this.setNamedTag(tag);
        } else {
            long id;
            if ((id = getMapId()) > mapCount) {
                mapCount = id;
            }
            if (!(compoundTag = this.getNamedTag()).contains("map_name_index")) {
                compoundTag.putInt("map_name_index", (int) id);
                this.setNamedTag(compoundTag);
            }
        }
        return super.initItem();
    }

    @Override
    public boolean allowOffhand() {
        return true;
    }
}
