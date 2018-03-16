package cn.nukkit.network.protocol;

import cn.nukkit.Player;

public class BookEditPacket extends DataPacket {

    @Override
    public byte pid() {
        return ProtocolInfo.BOOK_EDIT_PACKET;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        //TODO
    }

    @Override
    protected void handle(Player player) {

    }
}
