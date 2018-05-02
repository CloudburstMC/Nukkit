package com.nukkitx.server.inventory.transaction;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.server.network.minecraft.session.PlayerSession;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.nukkitx.nbt.util.VarInt.readUnsignedInt;
import static com.nukkitx.nbt.util.VarInt.writeUnsignedInt;
import static com.nukkitx.server.network.minecraft.MinecraftUtil.*;

@Data
@EqualsAndHashCode(callSuper = true)
public class ItemUseOnEntityTransaction extends ComplexTransaction {
    private static final Type type = Type.ITEM_USE_ON_ENTITY;
    private long runtimeEntityId;
    private Action action;
    private Vector3f clickPosition;

    @Override
    public void execute(PlayerSession session) {

    }

    @Override
    public void read(ByteBuf buffer){
        runtimeEntityId = readRuntimeEntityId(buffer);
        action = Action.values()[readUnsignedInt(buffer)];
        super.read(buffer);
        clickPosition = readVector3f(buffer);
    }

    @Override
    public void write(ByteBuf buffer){
        writeRuntimeEntityId(buffer, runtimeEntityId);
        writeUnsignedInt(buffer, action.ordinal());
        super.write(buffer);
        writeVector3f(buffer, clickPosition);
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void handle(PlayerSession.PlayerNetworkPacketHandler handler) {
        handler.handle(this);
    }

    @Override
    public String toString() {
        return "ItemUseOnEntityTransaction" + super.toString() +
                ", runtimeEntityId=" + runtimeEntityId +
                ", action=" + action +
                ", clickPosition=" + clickPosition +
                ')';
    }

    public enum Action {
        INTERACT,
        ATTACK,
        ITEM_INTERACT
    }
}
