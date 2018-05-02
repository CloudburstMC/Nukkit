package com.nukkitx.server.network.minecraft.packet;

import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.nukkitx.server.network.minecraft.MinecraftUtil.readString;

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

        int packIdsCount = buffer.readShortLE();
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
