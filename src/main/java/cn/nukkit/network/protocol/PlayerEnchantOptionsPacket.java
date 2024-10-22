package cn.nukkit.network.protocol;

import lombok.ToString;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@ToString
public class PlayerEnchantOptionsPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.PLAYER_ENCHANT_OPTIONS_PACKET;

    public final List<EnchantOptionData> options = new ArrayList<>();

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
        this.putUnsignedVarInt(this.options.size());
        for (EnchantOptionData option : this.options) {
            this.putVarInt(option.getMinLevel());
            this.putInt(option.getPrimarySlot());
            this.putUnsignedVarInt(option.getEnchants0().size());
            for (EnchantData data : option.getEnchants0()) {
                this.putByte((byte) data.getType());
                this.putByte((byte) data.getLevel());
            }
            this.putUnsignedVarInt(option.getEnchants1().size());
            for (EnchantData data : option.getEnchants1()) {
                this.putByte((byte) data.getType());
                this.putByte((byte) data.getLevel());
            }
            this.putUnsignedVarInt(option.getEnchants2().size());
            for (EnchantData data : option.getEnchants2()) {
                this.putByte((byte) data.getType());
                this.putByte((byte) data.getLevel());
            }
            this.putString(option.getEnchantName());
            this.putUnsignedVarInt(option.getEnchantNetId());
        }

    }

    @Value
    public class EnchantOptionData {
        private final int minLevel;
        private final int primarySlot;
        private final List<EnchantData> enchants0;
        private final List<EnchantData> enchants1;
        private final List<EnchantData> enchants2;
        private final String enchantName;
        private final int enchantNetId;
    }

    @Value
    public class EnchantData {
        private final int type;
        private final int level;
    }
}
