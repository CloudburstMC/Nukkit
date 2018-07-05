package com.nukkitx.server.network.bedrock.packet;

import com.nukkitx.server.network.bedrock.BedrockPacket;
import com.nukkitx.server.network.bedrock.BedrockUtil;
import com.nukkitx.server.network.util.VarInts;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class UpdateSoftEnumPacket implements BedrockPacket {
    private final List<String> values = new ArrayList<>();
    private String enumName;
    private Type type;

    @Override
    public void encode(ByteBuf buffer) {
        BedrockUtil.writeString(buffer, enumName);
        VarInts.writeUnsignedInt(buffer, values.size());
        values.forEach(s -> BedrockUtil.writeString(buffer, s));
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    public enum Type {
        ADD,
        REMOVE,
        UPDATE
    }
}
