package cn.nukkit.network.protocol;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.math.NukkitMath;
import lombok.ToString;

@ToString
public class MoveEntityDeltaPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.MOVE_ENTITY_DELTA_PACKET;

    public static final int FLAG_HAS_X = 0b1;
    public static final int FLAG_HAS_Y = 0b10;
    public static final int FLAG_HAS_Z = 0b100;
    public static final int FLAG_HAS_YAW = 0b1000;
    public static final int FLAG_HAS_HEAD_YAW = 0b10000;
    public static final int FLAG_HAS_PITCH = 0b100000;

    public int flags = 0;
    @Since("1.3.2.0-PN") public float x = 0;
    @Since("1.3.2.0-PN") public float y = 0;
    @Since("1.3.2.0-PN") public float z = 0;

    @Deprecated @DeprecationDetails(since = "1.3.2.0-PN", reason = "Changed to float", replaceWith = "x")
    @PowerNukkitOnly("Re-added for backward-compatibility")
    public int xDelta = 0;
    @Deprecated @DeprecationDetails(since = "1.3.2.0-PN", reason = "Changed to float", replaceWith = "y")
    @PowerNukkitOnly("Re-added for backward-compatibility")
    public int yDelta = 0;
    @PowerNukkitOnly("Re-added for backward-compatibility")
    @Deprecated @DeprecationDetails(since = "1.3.2.0-PN", reason = "Changed to float", replaceWith = "z")
    public int zDelta = 0;
    
    private int xDecoded;
    private int yDecoded;
    private int zDecoded;
    
    public double yawDelta = 0;
    public double headYawDelta = 0;
    public double pitchDelta = 0;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        this.flags = this.getByte();
        this.x = getCoordinate(FLAG_HAS_X);
        this.y = getCoordinate(FLAG_HAS_Y);
        this.z = getCoordinate(FLAG_HAS_Z);
        this.yawDelta = getRotation(FLAG_HAS_YAW);
        this.headYawDelta = getRotation(FLAG_HAS_HEAD_YAW);
        this.pitchDelta = getRotation(FLAG_HAS_PITCH);
        
        this.xDelta = this.xDecoded = NukkitMath.floorFloat(x);
        this.yDelta = this.yDecoded = NukkitMath.floorFloat(y);
        this.zDelta = this.zDecoded = NukkitMath.floorFloat(z);
    }

    @Override
    public void encode() {
        this.putByte((byte) flags);
        float x = this.x;
        float y = this.y;
        float z = this.z;
        if (xDelta != xDecoded || yDelta != yDecoded || zDelta != zDecoded) {
            x = xDelta;
            y = yDelta;
            z = zDelta;
        }
        putCoordinate(FLAG_HAS_X, x);
        putCoordinate(FLAG_HAS_Y, y);
        putCoordinate(FLAG_HAS_Z, z);
        putRotation(FLAG_HAS_YAW, this.yawDelta);
        putRotation(FLAG_HAS_HEAD_YAW, this.headYawDelta);
        putRotation(FLAG_HAS_PITCH, this.pitchDelta);
    }

    private float getCoordinate(int flag) {
        if ((flags & flag) != 0) {
            return this.getLFloat();
        }
        return 0;
    }

    private double getRotation(int flag) {
        if ((flags & flag) != 0) {
            return this.getByte() * (360d / 256d);
        }
        return 0d;
    }

    private void putCoordinate(int flag, float value) {
        if ((flags & flag) != 0) {
            this.putLFloat(value);
        }
    }

    private void putRotation(int flag, double value) {
        if ((flags & flag) != 0) {
            this.putByte((byte) (value / (360d / 256d)));
        }
    }
}
