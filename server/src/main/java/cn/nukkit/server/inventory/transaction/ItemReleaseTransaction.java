package cn.nukkit.server.inventory.transaction;

import cn.nukkit.server.network.NetworkPacketHandler;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static cn.nukkit.server.nbt.util.VarInt.readUnsignedInt;
import static cn.nukkit.server.nbt.util.VarInt.writeUnsignedInt;

@Data
@EqualsAndHashCode(callSuper = true)
public class ItemReleaseTransaction extends ComplexTransaction {
    private static final Type type = Type.ITEM_RELEASE;
    private Action action;

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
    public void handle(NetworkPacketHandler handler) {
        handler.handle(this);
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
