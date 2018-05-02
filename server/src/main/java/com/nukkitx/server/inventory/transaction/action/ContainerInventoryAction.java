package com.nukkitx.server.inventory.transaction.action;

import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.server.block.BlockUtil;
import com.nukkitx.server.network.minecraft.data.ContainerIds;
import com.nukkitx.server.network.minecraft.session.PlayerSession;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

import static com.nukkitx.nbt.util.VarInt.readSignedInt;
import static com.nukkitx.nbt.util.VarInt.writeSignedInt;

@Data
@EqualsAndHashCode(callSuper = true)
public class ContainerInventoryAction extends InventoryAction {
    private static final Type type = Type.CONTAINER;
    private int inventoryId;

    @Override
    public void write(ByteBuf buffer) {
        writeSignedInt(buffer, inventoryId);
        super.write(buffer);
    }

    @Override
    public void read(ByteBuf buffer) {
        inventoryId = readSignedInt(buffer);
        super.read(buffer);
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public void execute(PlayerSession session) {
        switch (inventoryId) {
            case ContainerIds.INVENTORY:
                ItemInstance serverItem = session.getInventory().getItem(getSlot()).orElse(BlockUtil.AIR);
                if (!testOldItem(serverItem)) {
                    session.sendPlayerInventory();
                    return;
                }

                session.getInventory().setItem(getSlot(), getNewItem());
                break;
            case ContainerIds.CURSOR:
                ItemInstance cursorItem = session.getInventory().getCursorItem().orElse(BlockUtil.AIR);

                if (!testOldItem(cursorItem)) {
                    session.sendPlayerInventory();
                    return;
                }

                session.getInventory().setCursorItem(getNewItem());
                break;
        }
    }
}
