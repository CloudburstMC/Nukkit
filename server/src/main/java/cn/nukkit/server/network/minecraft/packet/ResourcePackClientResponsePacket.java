package cn.nukkit.server.network.minecraft.packet;

import cn.nukkit.server.network.NetworkPacketHandler;
import cn.nukkit.server.network.minecraft.MinecraftPacket;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static cn.nukkit.server.nbt.util.VarInt.readUnsignedInt;
import static cn.nukkit.server.network.minecraft.MinecraftUtil.readString;

@Data
public class ResourcePackClientResponsePacket implements MinecraftPacket {
    private final List<UUID> packIds = new ArrayList<>();
    private Status status;

    @Override
    public void encode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void decode(ByteBuf buffer) {
        status = Status.values()[buffer.readByte()];

        int packIdsCount = readUnsignedInt(buffer);
        for (int i = 0; i < packIdsCount; i++) {
            packIds.add(UUID.fromString(readString(buffer)));
        }
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }

    public enum Status {
        NONE,
        REFUSED,
        SEND_PACKS,
        HAVE_ALL_PACKS,
        COMPLETED
    }
}
