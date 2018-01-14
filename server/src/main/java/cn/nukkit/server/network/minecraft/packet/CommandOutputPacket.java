package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.data.CommandOriginData;
import cn.nukkit.server.network.minecraft.data.CommandOutputMessage;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeCommandOriginData;

@Data
public class CommandOutputPacket implements MinecraftPacket {
    private final List<CommandOutputMessage> outputMessages = new ArrayList<>();
    private CommandOriginData commandOriginData;
    private byte outputType;
    private int successCount;

    @Override
    public void encode(ByteBuf buffer) {
        writeCommandOriginData(buffer, commandOriginData);
        buffer.writeByte(outputType);

    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // This packet isn't handled
    }
}
