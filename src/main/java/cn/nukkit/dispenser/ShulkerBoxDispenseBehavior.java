package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.block.BlockIds;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityTypes;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.registry.BlockEntityRegistry;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;

public class ShulkerBoxDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(Vector3i position, BlockDispenser block, BlockFace face, Item item) {
        Block shulkerBox = Block.get(BlockIds.SHULKER_BOX);
        Block target = block.getSide(face);

        this.success = block.getLevel().getCollidingEntities(shulkerBox.getBoundingBox()).isEmpty();

        if (this.success) {
            BlockFace shulkerBoxFace = target.down().getId() == BlockIds.AIR ? face : BlockFace.UP;

            CompoundTagBuilder nbt = CompoundTagBuilder.builder();
            nbt.byteTag("facing", (byte) shulkerBoxFace.getIndex());

            if (item.hasCustomName()) {
                nbt.stringTag("CustomName", item.getCustomName());
            }

            CompoundTag tag = item.getTag();

            if (tag != null) {
                if (tag.contains("Items")) {
                    nbt.listTag("Items", CompoundTag.class, tag.getList("Items", CompoundTag.class));
                }
            }

            BlockEntity blockEntity = BlockEntityRegistry.get().newEntity(BlockEntityTypes.SHULKER_BOX, target.getChunk(), target.getPosition());
            blockEntity.loadAdditionalData(nbt.buildRootTag());

            block.getLevel().updateComparatorOutputLevel(target.getPosition());
        }

        return null;
    }
}
