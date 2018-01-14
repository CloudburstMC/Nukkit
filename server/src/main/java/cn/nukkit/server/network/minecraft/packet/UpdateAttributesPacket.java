package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.entity.PlayerAttribute;
import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

import static cn.nukkit.server.network.minecraft.MinecraftUtil.writePlayerAttributes;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.writeRuntimeEntityId;

@Data
public class UpdateAttributesPacket implements MinecraftPacket {
    private long runtimeEntityId;
    private List<PlayerAttribute> attributes = new ArrayList<>();

    @Override
    public void encode(ByteBuf buffer) {
        writeRuntimeEntityId(buffer, runtimeEntityId);
        writePlayerAttributes(buffer, attributes);
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
