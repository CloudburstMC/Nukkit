package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockShulkerBox;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityShulkerBox;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;

public class ShulkerBoxDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block shulkerBox = new BlockShulkerBox();
        Block target = block.getSide(face);

        this.success = block.level.getCollidingEntities(shulkerBox.getBoundingBox()).length == 0;

        if (this.success) {
            BlockFace shulkerBoxFace = target.down().getId() == BlockID.AIR ? face : BlockFace.UP;

            CompoundTag nbt = BlockEntity.getDefaultCompound(target, BlockEntity.SHULKER_BOX);
            nbt.putByte("facing", shulkerBoxFace.getIndex());

            if (item.hasCustomName()) {
                nbt.putString("CustomName", item.getCustomName());
            }

            CompoundTag tag = item.getNamedTag();

            if (tag != null) {
                if (tag.contains("Items")) {
                    nbt.putList(tag.getList("Items"));
                }
            }

            new BlockEntityShulkerBox(block.level.getChunk(target.getChunkX(), target.getChunkZ()), nbt);
            block.level.updateComparatorOutputLevel(target);
        }

        return null;
    }
}
