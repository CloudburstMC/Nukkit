package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.nbt.util.VarInt.writeSignedInt;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeString;

@Data
public class SetDisplayObjectivePacket implements MinecraftPacket {
    private String unknownString0;
    private String unknownString1;
    private String unknownString2;
    private String unknownString3;
    private int unknownVarInt0;

    @Override
    public void encode(ByteBuf buffer) {
        writeString(buffer, unknownString0);
        writeString(buffer, unknownString1);
        writeString(buffer, unknownString2);
        writeString(buffer, unknownString3);
        writeSignedInt(buffer, unknownVarInt0);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // Client bound only
    }
}
