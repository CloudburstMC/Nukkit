package cn.nukkit.network.protocol;

import cn.nukkit.utils.Binary;
import cn.nukkit.utils.Utils;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by CreeperFace on 5.3.2017.
 */
@ToString
public class ClientboundMapItemDataPacket extends DataPacket { //TODO: update to 1.2

    public int[] eids = new int[0];

    public long mapId;
    public int update;
    public byte scale;
    public boolean isLocked;
    public int width;
    public int height;
    public int offsetX;
    public int offsetZ;

    public byte dimensionId;

    public MapDecorator[] decorators = new MapDecorator[0];
    public int[] colors = new int[0];
    public BufferedImage image = null;

    //update
    public static final int TEXTURE_UPDATE = 2;
    public static final int DECORATIONS_UPDATE = 4;
    public static final int ENTITIES_UPDATE = 8;

    @Override
    public short pid() {
        return ProtocolInfo.CLIENTBOUND_MAP_ITEM_DATA_PACKET;
    }

    @Override
    protected void decode(ByteBuf buffer) {

    }

    @Override
    protected void encode(ByteBuf buffer) {
        Binary.writeEntityUniqueId(buffer, mapId);

        int update = 0;
        if (eids.length > 0) {
            update |= 0x08;
        }
        if (decorators.length > 0) {
            update |= DECORATIONS_UPDATE;
        }

        if (image != null || colors.length > 0) {
            update |= TEXTURE_UPDATE;
        }

        Binary.writeUnsignedVarInt(buffer, update);
        buffer.writeByte(this.dimensionId);
        buffer.writeBoolean(this.isLocked);

        if ((update & 0x08) != 0) { //TODO: find out what these are for
            Binary.writeUnsignedVarInt(buffer, eids.length);
            for (int eid : eids) {
                Binary.writeEntityUniqueId(buffer, eid);
            }
        }
        if ((update & (TEXTURE_UPDATE | DECORATIONS_UPDATE)) != 0) {
            buffer.writeByte(this.scale);
        }

        if ((update & DECORATIONS_UPDATE) != 0) {
            Binary.writeUnsignedVarInt(buffer, decorators.length);

            for (MapDecorator decorator : decorators) {
                buffer.writeByte(decorator.rotation);
                buffer.writeByte(decorator.icon);
                buffer.writeByte(decorator.offsetX);
                buffer.writeByte(decorator.offsetZ);
                Binary.writeString(buffer, decorator.label);
                Binary.writeVarInt(buffer, decorator.color.getRGB());
            }
        }

        if ((update & TEXTURE_UPDATE) != 0) {
            Binary.writeVarInt(buffer, width);
            Binary.writeVarInt(buffer, height);
            Binary.writeVarInt(buffer, offsetX);
            Binary.writeVarInt(buffer, offsetZ);

            Binary.writeUnsignedVarInt(buffer, width * height);

            if (image != null) {
                for (int y = 0; y < width; y++) {
                    for (int x = 0; x < height; x++) {
                        Color color = new Color(image.getRGB(x, y), true);
                        byte red = (byte) color.getRed();
                        byte green = (byte) color.getGreen();
                        byte blue = (byte) color.getBlue();

                        Binary.writeUnsignedVarInt(buffer, Utils.toRGB(red, green, blue, (byte) 0xff));
                    }
                }

                image.flush();
            } else if (colors.length > 0) {
                for (int color : colors) {
                    Binary.writeUnsignedVarInt(buffer, color);
                }
            }
        }
    }

    public class MapDecorator {
        public byte rotation;
        public byte icon;
        public byte offsetX;
        public byte offsetZ;
        public String label;
        public Color color;
    }
}
