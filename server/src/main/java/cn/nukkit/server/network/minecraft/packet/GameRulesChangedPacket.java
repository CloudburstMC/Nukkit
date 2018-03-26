package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.api.level.GameRules;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import cn.nukkit.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeGameRules;

@Data
public class GameRulesChangedPacket implements MinecraftPacket {
    private GameRules gameRules;

    @Override
    public void encode(ByteBuf buffer) {
        writeGameRules(buffer, gameRules);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        // Client bound only.
    }
}
