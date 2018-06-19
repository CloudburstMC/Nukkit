package com.nukkitx.server.inventory.transaction.action;

import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.server.block.BlockUtil;
import com.nukkitx.server.network.bedrock.session.PlayerSession;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;

import static com.nukkitx.server.network.util.VarInts.readUnsignedInt;
import static com.nukkitx.server.network.util.VarInts.writeUnsignedInt;

@Data
@Log4j2
@EqualsAndHashCode(callSuper = true)
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
        ItemInstance serverItem = session.getInventory().getItemInHand().orElse(BlockUtil.AIR);
        if (!serverItem.isMergeable(getNewItem())) {
            if (log.isDebugEnabled()) {
                log.debug("Item thrown by client didn't match");
            }
            session.sendPlayerInventory();
            return;
        }
        switch (action) {
            case DROP_ITEM:
                int amountLeft = serverItem.getAmount() - getNewItem().getAmount();
                if (amountLeft < 0) {
                    session.sendPlayerInventory();
                    return;
                }
                session.getLevel().dropItem(getNewItem(), session.getGamePosition()).setMotion(session.getDirectionVector().mul(0.4f));
                if (amountLeft == 0) {
                    serverItem = null;
                } else {
                    serverItem = serverItem.toBuilder().amount(amountLeft).build();
                }
                session.getInventory().setItem(session.getInventory().getHeldHotbarSlot(), serverItem, session);
                break;
            case PICKUP_ITEM:
        }
    }

    public enum Action {
        DROP_ITEM,
        PICKUP_ITEM
    }
}
