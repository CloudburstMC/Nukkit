package com.nukkitx.server.network.minecraft.packet;

import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import com.nukkitx.server.network.minecraft.data.CommandOriginData;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.minecraft.MinecraftUtil.*;

@Data
public class CommandRequestPacket implements MinecraftPacket {
    private String command;
    private CommandOriginData commandOriginData;
    private boolean internal;

    @Override
    public void encode(ByteBuf buffer) {
        writeString(buffer, command);
        writeCommandOriginData(buffer, commandOriginData);
        buffer.writeBoolean(internal);
    }

    @Override
    public void decode(ByteBuf buffer) {
        command = readString(buffer);
        commandOriginData = readCommandOriginData(buffer);
        internal = buffer.readBoolean();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }
}
