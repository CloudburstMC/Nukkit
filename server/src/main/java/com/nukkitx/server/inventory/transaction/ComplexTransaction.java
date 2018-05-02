package com.nukkitx.server.inventory.transaction;

import com.flowpowered.math.vector.Vector3f;
import com.nukkitx.api.block.Block;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.server.inventory.transaction.action.InventoryAction;
import com.nukkitx.server.level.NukkitLevel;
import com.nukkitx.server.network.minecraft.packet.UpdateBlockPacket;
import com.nukkitx.server.network.minecraft.session.PlayerSession;
import io.netty.buffer.ByteBuf;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.nukkitx.nbt.util.VarInt.readSignedInt;
import static com.nukkitx.nbt.util.VarInt.writeSignedInt;
import static com.nukkitx.server.network.minecraft.MinecraftUtil.*;

@Data
@Log4j2
@EqualsAndHashCode
public abstract class ComplexTransaction implements InventoryTransaction {
    private final long creationTime;
    private final List<InventoryAction> records = new ArrayList<>();
    private int slot;
    private ItemInstance item;
    private Vector3f fromPosition;

    public ComplexTransaction() {
        creationTime = System.currentTimeMillis();
    }

    public void read(ByteBuf buffer) {
        slot = readSignedInt(buffer);
        item = readItemInstance(buffer);
        fromPosition = readVector3f(buffer);
    }

    public void write(ByteBuf buffer) {
        writeSignedInt(buffer, slot);
        writeItemInstance(buffer, item);
        writeVector3f(buffer, fromPosition);
    }

    protected boolean isHandValid(ItemInstance withItem, PlayerSession session) {
        if (!withItem.equals(getItem())) {
            log.debug("{} interacted with an object using {} but has {} in hand {}", session.getName(), getItem(), withItem, getSlot());
            session.sendPlayerInventory();
            return false;
        }
        return true;
    }

    protected void resetBlock(Block block, PlayerSession session) {
        UpdateBlockPacket resetBlock = new UpdateBlockPacket();
        resetBlock.setRuntimeId(NukkitLevel.getPaletteManager().getOrCreateRuntimeId(block.getBlockState()));
        resetBlock.setDataLayer(UpdateBlockPacket.DataLayer.NORMAL); //TODO: Remove hardcoding
        resetBlock.setBlockPosition(block.getBlockPosition());
    }

    @Override
    public String toString() {
        return "(" +
                "type=" + getType() +
                ", records=" + Arrays.toString(getRecords().toArray()) +
                ", creationTime=" + creationTime +
                ", slot=" + slot +
                ", item=" + item +
                ", fromPosition=" + fromPosition;
    }
}
