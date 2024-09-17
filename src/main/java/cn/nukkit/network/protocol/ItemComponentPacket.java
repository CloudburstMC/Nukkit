package cn.nukkit.network.protocol;

import cn.nukkit.item.custom.ItemDefinition;
import cn.nukkit.nbt.NBTIO;
import lombok.ToString;

import java.io.IOException;
import java.nio.ByteOrder;
import java.util.List;

@ToString
public class ItemComponentPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.ITEM_COMPONENT_PACKET;

    public List<ItemDefinition> itemDefinitions;

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
            this.putString(definition.getIdentifier());
            try {
                this.put(NBTIO.write(definition.getNetworkData(), ByteOrder.LITTLE_ENDIAN, true));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
