package com.nukkitx.server.network.bedrock.packet;

import com.flowpowered.math.vector.Vector2f;
import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.BedrockPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.server.network.bedrock.BedrockUtil.readVector2f;

@Data
public class PlayerInputPacket implements BedrockPacket {
    private Vector2f inputMotion;
    private boolean unknown0;
    private boolean unknown1;

    @Override
    public void encode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void decode(ByteBuf buffer) {
        inputMotion = readVector2f(buffer);
        unknown0 = buffer.readBoolean();
        unknown1 = buffer.readBoolean();
    }

    @Override
    public void handle(BedrockPacketHandler handler) {
        handler.handle(this);
    }
}
