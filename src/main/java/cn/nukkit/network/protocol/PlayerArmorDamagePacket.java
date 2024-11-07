package cn.nukkit.network.protocol;

import lombok.ToString;

import java.util.EnumSet;
import java.util.Set;

@ToString
public class PlayerArmorDamagePacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.PLAYER_ARMOR_DAMAGE_PACKET;

    public final Set<PlayerArmorDamageFlag> flags = EnumSet.noneOf(PlayerArmorDamageFlag.class);
    public final int[] damage = new int[4];

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
        int outflags = 0;
        for (PlayerArmorDamageFlag flag : this.flags) {
            outflags |= 1 << flag.ordinal();
        }
        this.putByte((byte) outflags);

        for (PlayerArmorDamageFlag flag : this.flags) {
            this.putVarInt(this.damage[flag.ordinal()]);
        }
    }

    public enum PlayerArmorDamageFlag {
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS,
        BODY
    }
}
