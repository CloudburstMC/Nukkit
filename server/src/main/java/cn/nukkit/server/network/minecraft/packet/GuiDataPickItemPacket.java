package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeString;

@Data
public class GuiDataPickItemPacket implements MinecraftPacket {
    private String locale;
    private String popupMessage;
    private int hotbarSlot;

    @Override
    public void encode(ByteBuf buffer) {
        writeString(buffer, locale);
        writeString(buffer, popupMessage);
        buffer.writeIntLE(hotbarSlot);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {

    }
}
