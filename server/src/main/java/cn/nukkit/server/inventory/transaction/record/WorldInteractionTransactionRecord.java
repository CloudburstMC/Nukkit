package cn.nukkit.server.inventory.transaction.record;

import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static cn.nukkit.server.nbt.util.VarInt.readUnsignedInt;
import static cn.nukkit.server.nbt.util.VarInt.writeUnsignedInt;

@EqualsAndHashCode(callSuper = true)
@Data
public class WorldInteractionTransactionRecord extends TransactionRecord {
    private static final Type type = Type.WORLD_INTERACTION;
    private Action action;

    @Override
    public void write(ByteBuf buffer){
        writeUnsignedInt(buffer, action.ordinal());
        super.write(buffer);
    }

    @Override
    public void read(ByteBuf buffer) {
        action = Action.values()[readUnsignedInt(buffer)];
        super.read(buffer);
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void execute(PlayerSession session) {
        switch (action) {
            case DROP_ITEM:
                session.getLevel().dropItem(getNewItem(), session.getGamePosition(), session.getDirectionVector().mul(0.4f), (short) 20);
                break;
            case PICKUP_ITEM:
        }
    }

    public enum Action {
        DROP_ITEM,
        PICKUP_ITEM
    }
}
