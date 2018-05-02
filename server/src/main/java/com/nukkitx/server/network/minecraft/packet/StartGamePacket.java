package com.nukkitx.server.network.minecraft.packet;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.level.LevelSettings;
import com.nukkitx.api.util.GameMode;
import com.nukkitx.api.util.Rotation;
import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.nbt.util.VarInt.writeSignedInt;
import static com.nukkitx.server.network.minecraft.MinecraftUtil.*;

@Data
public class StartGamePacket implements MinecraftPacket {
    private long uniqueEntityId;
    private long runtimeEntityId;
    private GameMode gamemode;
    private Vector3f playerPosition;
    private Rotation rotation;
    private LevelSettings levelSettings;
    private String levelId;
    private String worldName;
    private String premiumWorldTemplateId;
    private boolean trial;
    private long currentTick;
    private int enchantmentSeed;

    @Override
    public void encode(ByteBuf buffer) {
        writeUniqueEntityId(buffer, uniqueEntityId);
        writeRuntimeEntityId(buffer, runtimeEntityId);
        writeSignedInt(buffer, gamemode.ordinal());
        writeVector3f(buffer, playerPosition);
        writeVector2f(buffer, rotation.getBodyRotation());
        writeLevelSettings(buffer, levelSettings);
        writeString(buffer, levelId);
        writeString(buffer, worldName);
        writeString(buffer, premiumWorldTemplateId);
        buffer.writeBoolean(trial);
        buffer.writeLongLE(currentTick);
        writeSignedInt(buffer, enchantmentSeed);
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
