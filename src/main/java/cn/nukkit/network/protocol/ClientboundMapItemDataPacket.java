package cn.nukkit.network.protocol;

import cn.nukkit.math.BlockVector3;
import cn.nukkit.utils.Utils;
import lombok.ToString;

import java.awt.Color;
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

        this.putByte(this.dimensionId);
        this.putBoolean(this.isLocked);
        this.putSignedBlockPosition(this.origin);

        if (this.eids.length > 0) {
            this.putBoolean(true);
            this.putUnsignedVarInt(this.eids.length);
            for (long eid : this.eids) {
                this.putEntityUniqueId(eid);
            }
        } else {
            this.putBoolean(false);
        }

        this.putBoolean(true);
        this.putByte(this.scale);

        if (this.trackedEntities.length > 0) {
            this.putBoolean(true);
            this.putUnsignedVarInt(this.trackedEntities.length);
            for (MapTrackedObject object : this.trackedEntities) {
                this.putLInt(object.type);

                if (object.type == MapTrackedObject.TYPE_ENTITY) {
                    this.putBoolean(true);
                    this.putEntityUniqueId(object.entityUniqueId);
                    this.putBoolean(false);
                } else if (object.type == MapTrackedObject.TYPE_BLOCK) {
                    this.putBoolean(false);
                    this.putBoolean(true);
                    this.putBlockVector3(object.x, object.y, object.z);
                } else {
                    throw new IllegalArgumentException("Unknown map object type " + object.type);
                }
            }
        } else {
            this.putBoolean(false);
        }

        if (this.decorators.length > 0) {
            this.putBoolean(true);
            this.putUnsignedVarInt(this.decorators.length);
            for (MapDecorator decorator : this.decorators) {
                this.putByte(decorator.icon);
                this.putByte(decorator.rotation);
                this.putByte(decorator.offsetX);
                this.putByte(decorator.offsetZ);
                this.putString(decorator.label);
                this.putLInt(decorator.color.getRGB()); //toABGR?
            }
        } else {
            this.putBoolean(false);
        }

        if (this.width != 0) {
            this.putBoolean(true);
            this.putVarInt(this.width);
        } else {
            this.putBoolean(false);
        }

        if (this.height != 0) {
            this.putBoolean(true);
            this.putVarInt(this.height);
        } else {
            this.putBoolean(false);
        }

        this.putBoolean(true);
        this.putVarInt(this.offsetX);

        this.putBoolean(true);
        this.putVarInt(this.offsetZ);

        if (this.image != null) {
            this.putBoolean(true);
            this.putUnsignedVarInt((long) this.width * this.height);

            for (int y = 0; y < this.width; y++) {
                for (int x = 0; x < this.height; x++) {
                    this.putLInt((int) Utils.toABGR(this.image.getRGB(x, y)));
                }
            }

            this.image.flush();
        } else if (this.colors.length != 0) {
            this.putBoolean(true);
            this.putUnsignedVarInt(this.colors.length);

            for (int color : this.colors) {
                this.putLInt(color);
            }
        } else {
            this.putBoolean(false);
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
