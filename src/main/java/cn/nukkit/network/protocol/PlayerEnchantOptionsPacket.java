package cn.nukkit.network.protocol;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.ToString;
import lombok.Value;

import java.util.ArrayList;
import java.util.List;

@Since("1.3.0.0-PN")
@ToString
public class PlayerEnchantOptionsPacket extends DataPacket {
    public static final byte NETWORK_ID = ProtocolInfo.PLAYER_ENCHANT_OPTIONS_PACKET;

    @Since("1.3.0.0-PN") public final List<EnchantOptionData> options = new ArrayList<>();

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        int size = (int) this.getUnsignedVarInt();
        for (int i = 0; i < size; i++) {
            int minLevel = this.getVarInt();
            int slot = this.getInt();

            int eSize = (int) this.getUnsignedVarInt();
            List<EnchantData> list1 = new ObjectArrayList<>();
            for (int j = 0; j < eSize; j++) {
                EnchantData data = new EnchantData(this.getByte(), this.getByte());
                list1.add(data);
            }

            eSize = (int) this.getUnsignedVarInt();
            List<EnchantData> list2 = new ObjectArrayList<>();
            for (int j = 0; j < eSize; j++) {
                EnchantData data = new EnchantData(this.getByte(), this.getByte());
                list2.add(data);
            }

            eSize = (int) this.getUnsignedVarInt();
            List<EnchantData> list3 = new ObjectArrayList<>();
            for (int j = 0; j < eSize; j++) {
                EnchantData data = new EnchantData(this.getByte(), this.getByte());
                list3.add(data);
            }
            String enchantName = this.getString();
            int eNetId = (int) this.getUnsignedVarInt();
            this.options.add(new EnchantOptionData(minLevel, slot, list1, list2, list3, enchantName, eNetId));
        }

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

    @Since("1.3.0.0-PN")
    @Value
    public class EnchantOptionData {
        @Since("1.3.1.0-PN") private final int minLevel;
        private final int primarySlot;
        private final List<EnchantData> enchants0;
        private final List<EnchantData> enchants1;
        private final List<EnchantData> enchants2;
        private final String enchantName;
        private final int enchantNetId;
    }

    @Since("1.3.0.0-PN")
    @Value
    public class EnchantData {
        private final int type;
        private final int level;
    }
}
