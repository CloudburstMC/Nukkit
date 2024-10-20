package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.utils.Utils;
import lombok.ToString;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by CreeperFace on 5.3.2017.
 */
@ToString
public class ClientboundMapItemDataPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.CLIENTBOUND_MAP_ITEM_DATA_PACKET;

    public long mapId;
    public int update;
    public byte scale;
    public boolean isLocked;
    public int width;
    public int height;
    public int offsetX;
    public int offsetZ;
    public byte dimensionId;
    public BlockVector3 origin = EMPTY_BlockVector3;
    public MapDecorator[] decorators = EMPTY_MapDecorator;
    public MapTrackedObject[] trackedEntities = EMPTY_MapTrackedObject;
    public long[] eids = EMPTY_long;
    public int[] colors = EMPTY_int;
    public BufferedImage image;

    private static final BlockVector3 EMPTY_BlockVector3 = new BlockVector3();
    private static final MapDecorator[] EMPTY_MapDecorator = new MapDecorator[0];
    private static final MapTrackedObject[] EMPTY_MapTrackedObject = new MapTrackedObject[0];
    private static final long[] EMPTY_long = new long[0];
    private static final int[] EMPTY_int = new int[0];

    public static final int TEXTURE_UPDATE = 0x02;
    public static final int DECORATIONS_UPDATE = 0x04;
    public static final int ENTITIES_UPDATE = 0x08;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.decodeUnsupported();
    }

    @Override
    public void encode() {
        this.reset();
        this.putEntityUniqueId(mapId);

        int update = 0;
        if (eids.length > 0) {
            update |= ENTITIES_UPDATE;
        }
        if (decorators.length > 0) {
            update |= DECORATIONS_UPDATE;
        }

        if (image != null || colors.length > 0) {
            update |= TEXTURE_UPDATE;
        }

        this.putUnsignedVarInt(update);
        this.putByte(this.dimensionId);
        this.putBoolean(this.isLocked);
        this.putSignedBlockPosition(origin);

        if ((update & ENTITIES_UPDATE) != 0) {
            this.putUnsignedVarInt(eids.length);
            for (long eid : eids) {
                this.putEntityUniqueId(eid);
            }
        }
        if ((update & (ENTITIES_UPDATE | TEXTURE_UPDATE | DECORATIONS_UPDATE)) != 0) {
            this.putByte(this.scale);
        }

        if ((update & DECORATIONS_UPDATE) != 0) {
            this.putUnsignedVarInt(trackedEntities.length);
            for (MapTrackedObject object : trackedEntities) {
                this.putLInt(object.type);
                if (object.type == MapTrackedObject.TYPE_BLOCK) {
                    this.putBlockVector3(object.x, object.y, object.z);
                } else if (object.type == MapTrackedObject.TYPE_ENTITY) {
                    this.putEntityUniqueId(object.entityUniqueId);
                } else {
                    throw new IllegalArgumentException("Unknown map object type " + object.type);
                }
            }

            this.putUnsignedVarInt(decorators.length);

            for (MapDecorator decorator : decorators) {
                this.putByte(decorator.icon);
                this.putByte(decorator.rotation);
                this.putByte(decorator.offsetX);
                this.putByte(decorator.offsetZ);
                this.putString(decorator.label);
                this.putUnsignedVarInt(decorator.color.getRGB());
            }
        }

        if ((update & TEXTURE_UPDATE) != 0) {
            this.putVarInt(width);
            this.putVarInt(height);
            this.putVarInt(offsetX);
            this.putVarInt(offsetZ);

            this.putUnsignedVarInt((long) width * height);

            if (image != null) {
                for (int y = 0; y < width; y++) {
                    for (int x = 0; x < height; x++) {
                        this.putUnsignedVarInt(Utils.toABGR(this.image.getRGB(x, y)));
                    }
                }

                image.flush();
            } else {
                for (int color : colors) {
                    this.putUnsignedVarInt(color);
                }
            }
        }
    }

    public static class MapDecorator {
        public byte rotation;
        public byte icon;
        public byte offsetX;
        public byte offsetZ;
        public String label;
        public Color color;
    }

    public static class MapTrackedObject {
        public static final int TYPE_ENTITY = 0;
        public static final int TYPE_BLOCK = 1;

        public int type;
        public long entityUniqueId;

        public int x;
        public int y;
        public int z;
    }
}
