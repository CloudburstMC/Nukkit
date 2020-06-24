package cn.nukkit.network.protocol;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

import java.util.EnumSet;
import java.util.Set;

@PowerNukkitOnly
@Since("1.2.2.0-PN")
public class PlayerArmorDamagePacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.PLAYER_ARMOR_DAMAGE_PACKET;
    private static final PlayerArmorType[] FLAGS = PlayerArmorType.values();

    public final Set<PlayerArmorType> flags = EnumSet.noneOf(PlayerArmorType.class);
    public final int[] damage = new int[4];

    @Override
    public void encode() {
        int flagsVal = 0;
        for (PlayerArmorType flag : flags) {
            flagsVal |= 1 << flag.ordinal();
        }
        putByte((byte) flagsVal);

        for (PlayerArmorType flag : flags) {
            putVarInt(damage[flag.ordinal()]);
        }
    }

    @Override
    public void decode() {
        int flagsVal = getByte();
        for (int i = 0; i < 4; i++) {
            if ((flagsVal & (1 << i)) != 0) {
                flags.add(FLAGS[i]);
                damage[i] = getVarInt();
            }
        }
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @PowerNukkitOnly
    @Since("1.2.2.0-PN")
    public enum PlayerArmorType {
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS
    }
}
