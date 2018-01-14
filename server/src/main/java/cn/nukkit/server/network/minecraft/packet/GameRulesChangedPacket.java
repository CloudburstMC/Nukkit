package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.api.level.gamerule.GameRule;
import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeGameRules;

@Data
public class GameRulesChangedPacket implements MinecraftPacket {
    private final List<GameRule> rules = new ArrayList<>();

    @Override
    public void encode(ByteBuf buffer) {
        writeGameRules(buffer, rules);
    }

    @Override
    public void decode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void handle(NetworkPacketHandler handler) {

    }
}
