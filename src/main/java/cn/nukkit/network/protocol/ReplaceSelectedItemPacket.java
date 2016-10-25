package cn.nukkit.network.protocol;

/**
 * Created by Pub4Game on 29.04.2016.
 */
public class ReplaceSelectedItemPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.REPLACE_SELECTED_ITEM_PACKET;

    @Override
    public void decode() {

    }

    @Override
    public void encode() {

    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

}