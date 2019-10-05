package cn.nukkit.network.protocol;

import cn.nukkit.player.Player;
import cn.nukkit.utils.Binary;
import io.netty.buffer.ByteBuf;
import lombok.ToString;

/**
 * @author Nukkit Project Team
 */
@ToString
public class AdventureSettingsPacket extends DataPacket {

    public static final short NETWORK_ID = ProtocolInfo.ADVENTURE_SETTINGS_PACKET;

    public static final int PERMISSION_NORMAL = 0;
    public static final int PERMISSION_OPERATOR = 1;
    public static final int PERMISSION_HOST = 2;
    public static final int PERMISSION_AUTOMATION = 3;
    public static final int PERMISSION_ADMIN = 4;
    //TODO: check level 3
    /**
     * This constant is used to identify flags that should be set on the second field. In a sensible world, these
     * flags would all be set on the same packet field, but as of MCPE 1.2, the new abilities flags have for some
     * reason been assigned a separate field.
     */
    public static final int BITFLAG_SECOND_SET = 1 << 16;

    public static final int WORLD_IMMUTABLE = 0x01;
    public static final int NO_PVP = 0x02;
    public static final int AUTO_JUMP = 0x20;
    public static final int ALLOW_FLIGHT = 0x40;
    public static final int NO_CLIP = 0x80;
    public static final int WORLD_BUILDER = 0x100;
    public static final int FLYING = 0x200;
    public static final int MUTED = 0x400;
    public static final int BUILD_AND_MINE = 0x01 | BITFLAG_SECOND_SET;
    public static final int DOORS_AND_SWITCHES = 0x02 | BITFLAG_SECOND_SET;
    public static final int OPEN_CONTAINERS = 0x04 | BITFLAG_SECOND_SET;
    public static final int ATTACK_PLAYERS = 0x08 | BITFLAG_SECOND_SET;
    public static final int ATTACK_MOBS = 0x10 | BITFLAG_SECOND_SET;
    public static final int OPERATOR = 0x20 | BITFLAG_SECOND_SET;
    public static final int TELEPORT = 0x80 | BITFLAG_SECOND_SET;

    public long flags = 0;

    public long commandPermission = PERMISSION_NORMAL;

    public long flags2 = -1;

    public long playerPermission = Player.PERMISSION_MEMBER;

    public long customFlags; //...

    public long entityUniqueId; //This is a little-endian long, NOT a var-long. (WTF Mojang)

    protected void decode(ByteBuf buffer) {
        this.flags = Binary.readUnsignedVarInt(buffer);
        this.commandPermission = Binary.readUnsignedVarInt(buffer);
        this.flags2 = Binary.readUnsignedVarInt(buffer);
        this.playerPermission = Binary.readUnsignedVarInt(buffer);
        this.customFlags = Binary.readUnsignedVarInt(buffer);
        this.entityUniqueId = buffer.readLongLE();
    }

    protected void encode(ByteBuf buffer) {
        Binary.writeUnsignedVarInt(buffer, this.flags);
        Binary.writeUnsignedVarInt(buffer, this.commandPermission);
        Binary.writeUnsignedVarInt(buffer, this.flags2);
        Binary.writeUnsignedVarInt(buffer, this.playerPermission);
        Binary.writeUnsignedVarInt(buffer, this.customFlags);
        buffer.writeLongLE(this.entityUniqueId);
    }

    public boolean getFlag(int flag) {
        if ((flag & BITFLAG_SECOND_SET) != 0) {
            return (this.flags2 & flag) != 0;
        }
        return (this.flags & flag) != 0;
    }

    public void setFlag(int flag, boolean value) {
        boolean flags = (flag & BITFLAG_SECOND_SET) != 0;

        if (value) {
            if (flags) {
                this.flags2 |= flag;
            } else {
                this.flags |= flag;
            }
        } else {
            if (flags) {
                this.flags2 &= ~flag;
            } else {
                this.flags &= ~flag;
            }
        }
    }

    @Override
    public short pid() {
        return NETWORK_ID;
    }
}
