package com.nukkitx.server.inventory.transaction.action;

import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.server.block.BlockUtil;
import com.nukkitx.server.network.minecraft.session.PlayerSession;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.nukkitx.nbt.util.VarInt.readUnsignedInt;
import static com.nukkitx.nbt.util.VarInt.writeUnsignedInt;

@EqualsAndHashCode(callSuper = true)
@Data
public class WorldInteractionInventoryAction extends InventoryAction {
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
                ItemInstance serverItem = session.getInventory().getItemInHand().orElse(BlockUtil.AIR);
                session.getLevel().dropItem(getNewItem(), session.getGamePosition()).setMotion(session.getDirectionVector().mul(0.4f));
                break;
            case PICKUP_ITEM:
        }
    }

    public enum Action {
        DROP_ITEM,
        PICKUP_ITEM
    }
}
