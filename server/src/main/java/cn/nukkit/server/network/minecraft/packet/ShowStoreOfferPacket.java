package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeString;

@Data
public class ShowStoreOfferPacket implements MinecraftPacket {
    private String offerId;
    private boolean shownToAll;

    @Override
    public void encode(ByteBuf buffer) {
        writeString(buffer, offerId);
        buffer.writeBoolean(shownToAll);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // Only client bound.
    }
}
