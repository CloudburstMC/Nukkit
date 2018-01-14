package cn.nukkit.server.inventory.transaction.record;

import cn.nukkit.api.block.BlockTypes;
import cn.nukkit.api.item.ItemStack;
import cn.nukkit.server.network.minecraft.data.ContainerIds;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Optional;

import static cn.nukkit.server.nbt.util.VarInt.readSignedInt;
import static cn.nukkit.server.nbt.util.VarInt.writeSignedInt;

@EqualsAndHashCode(callSuper = true)
@Data
public class ContainerTransactionRecord extends TransactionRecord {
    private static final Type type = Type.CONTAINER;
    private int inventoryId;

    @Override
    public void write(ByteBuf buffer){
        writeSignedInt(buffer, inventoryId);
        super.write(buffer);
    }

    @Override
    public void read(ByteBuf buffer){
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
                Optional<ItemStack> actualItem = session.getInventory().getItem(getSlot());

                if ((actualItem.isPresent() && !actualItem.get().equals(getOldItem())) ||
                        !actualItem.isPresent() && getOldItem().getItemType() != BlockTypes.AIR) {
                    // Not actually the same item.
                    session.sendPlayerInventory();
                    return;
                }

                session.getInventory().setItem(getSlot(), getNewItem());
                break;
            case ContainerIds.CURSOR:
                Optional<ItemStack> cursorItem = session.getInventory().getCursorItem();

                if (cursorItem.isPresent() && !cursorItem.get().equals(getOldItem()) ||
                        !cursorItem.isPresent() && getOldItem().getItemType() != BlockTypes.AIR) {
                    // Not actually the same item.
                    session.sendPlayerInventory();
                    return;
                }

                session.setCursorItem(getNewItem(), false);
                break;
        }
    }
}
