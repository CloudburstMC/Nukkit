package cn.nukkit.network.protocol;

import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.AllArgsConstructor;
import lombok.ToString;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.List;

@ToString
public class ItemComponentPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.ITEM_COMPONENT_PACKET;

    public List<ItemDefinition> itemDefinitions;

    private static final byte[] EMPTY_COMPOUND_TAG;

    static {
        try {
            EMPTY_COMPOUND_TAG = NBTIO.writeNetwork(new CompoundTag(""));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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
        this.putUnsignedVarInt(this.itemDefinitions.size());
        for (ItemDefinition definition : this.itemDefinitions) {
            this.putString(definition.getIdentifier);
            this.putLShort(definition.getRuntimeId);
            this.putBoolean(definition.isComponentBased);
            this.putVarInt(definition.getVersion);

            if (definition.getNetworkData != null) {
                try {
                    this.put(NBTIO.write(definition.getNetworkData, ByteOrder.LITTLE_ENDIAN, true));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                this.put(EMPTY_COMPOUND_TAG);
            }
        }
    }

    @AllArgsConstructor
    public static class ItemDefinition {
        private final String getIdentifier;
        private final int getRuntimeId;
        private final boolean isComponentBased;
        private final int getVersion;
        private final CompoundTag getNetworkData;
    }
}
