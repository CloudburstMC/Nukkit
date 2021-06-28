package cn.nukkit.network.protocol;

import lombok.ToString;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
@ToString
public class ContainerSetDataPacket extends DataPacket {

    public static final int PROPERTY_FURNACE_TICK_COUNT = 0;
    public static final int PROPERTY_FURNACE_LIT_TIME = 1;
    public static final int PROPERTY_FURNACE_LIT_DURATION = 2;
    public static final int PROPERTY_FURNACE_STORED_XP = 3;
    public static final int PROPERTY_FURNACE_FUEL_AUX = 4;

    public static final int PROPERTY_BREWING_STAND_BREW_TIME = 0;
    public static final int PROPERTY_BREWING_STAND_FUEL_AMOUNT = 1;
    public static final int PROPERTY_BREWING_STAND_FUEL_TOTAL = 2;

    public byte windowId;
    public int property;
    public int value;

    @Override
    public byte pid() {
        return ProtocolInfo.CONTAINER_SET_DATA_PACKET;
    }

    @Override
    public void decode() {
        this.windowId = this.getByte();
        this.property = this.getVarInt();
        this.value = this.getVarInt();
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte(this.windowId);
        this.putVarInt(this.property);
        this.putVarInt(this.value);
    }
}
