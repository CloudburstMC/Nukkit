package com.nukkitx.server.network.minecraft.packet;

import com.flowpowered.math.vector.Vector3i;
import com.nukkitx.api.util.data.BlockFace;
import com.nukkitx.server.network.minecraft.MinecraftPacket;
import com.nukkitx.server.network.minecraft.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import static com.nukkitx.nbt.util.VarInt.readSignedInt;
import static com.nukkitx.server.network.minecraft.MinecraftUtil.readRuntimeEntityId;
import static com.nukkitx.server.network.minecraft.MinecraftUtil.readVector3i;

@Data
public class PlayerActionPacket implements MinecraftPacket {
    private long runtimeEntityId;
    private Action action;
    private Vector3i blockPosition;
    private BlockFace face;

    @Override
    public void encode(ByteBuf buffer) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void decode(ByteBuf buffer) {
        runtimeEntityId = readRuntimeEntityId(buffer);
        action = Action.values()[readSignedInt(buffer)];
        blockPosition = readVector3i(buffer);
        face = BlockFace.values()[readSignedInt(buffer)];
    }

    @Override
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
    }

    public enum Action {
        START_BREAK,
        ABORT_BREAK,
        STOP_BREAK,
        GET_UPDATED_BLOCK,
        DROP_ITEM,
        START_SLEEP,
        STOP_SLEEP,
        RESPAWN,
        JUMP,
        START_SPRINT,
        STOP_SPRINT,
        START_SNEAK,
        STOP_SNEAK,
        DIMENSION_CHANGE_REQUEST,
        DIMENSION_CHANGE_SUCCESS,
        START_GLIDE,
        STOP_GLIDE,
        BUILD_DENIED,
        CONTINUE_BREAK,
        CHANGE_SKIN,
        SET_ENCHANTMENT_SEED,
        START_SWIMMING,
        STOP_SWIMMING,
        START_SPIN_ATTACK,
        STOP_SPIN_ATTACK
    }
}
