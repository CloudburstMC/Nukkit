package cn.nukkit.network.protocol;

import cn.nukkit.network.protocol.types.DimensionDefinition;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.ToString;

import java.util.List;

@ToString
public class DimensionDataPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.DIMENSION_DATA_PACKET;

    private static final List<DimensionDefinition> DEFAULT_DEFINITIONS = new ObjectArrayList<DimensionDefinition>() {
        {
            add(new DimensionDefinition("minecraft:overworld", 319, -64, 1));
        }
    };

    public List<DimensionDefinition> definitions = DEFAULT_DEFINITIONS;

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
        this.putUnsignedVarInt(definitions.size());
        for (DimensionDefinition definition : definitions) {
            this.putString(definition.getId());
            this.putVarInt(definition.getMaximumHeight());
            this.putVarInt(definition.getMinimumHeight());
            this.putVarInt(definition.getGeneratorType());
        }
    }
}
