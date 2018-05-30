package com.nukkitx.server.inventory.transaction;

import com.nukkitx.server.network.bedrock.session.PlayerSession;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.nukkitx.server.network.util.VarInts.readUnsignedInt;
import static com.nukkitx.server.network.util.VarInts.writeUnsignedInt;

@Data
@EqualsAndHashCode(callSuper = true)
public class ItemReleaseTransaction extends ComplexTransaction {
    private static final Type type = Type.ITEM_RELEASE;
    private Action action;

    @Override
    public void execute(PlayerSession session) {

    }

    @Override
    public void read(ByteBuf buffer){
        action = Action.values()[readUnsignedInt(buffer)];
        super.read(buffer);
    }

    @Override
    public void write(ByteBuf buffer){
        writeUnsignedInt(buffer, action.ordinal());
        super.write(buffer);
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "ItemReleaseTransaction" + super.toString() +
                ", action=" + action +
                ')';
    }

    public enum Action {
        RELEASE,
        USE
    }
}
