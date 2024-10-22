package cn.nukkit.dispenser;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockDispenser;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;

public class UndyedShulkerBoxDispenseBehavior extends DefaultDispenseBehavior {

    @Override
    public Item dispense(BlockDispenser block, BlockFace face, Item item) {
        Block target = block.getSide(face);

        if (target.getId() == Block.AIR) {
            CompoundTag nbt = BlockEntity.getDefaultCompound(target, BlockEntity.SHULKER_BOX);
            nbt.putByte("facing", BlockFace.UP.getIndex());

            if (item.hasCustomName()) {
                nbt.putString("CustomName", item.getCustomName());
            }

            CompoundTag tag = item.getNamedTag();

            if (tag != null) {
                if (tag.contains("Items")) {
                    nbt.putList(tag.getList("Items"));
                }
            }

            block.level.setBlock(target, Block.get(BlockID.UNDYED_SHULKER_BOX, 0), true);
            BlockEntity.createBlockEntity(BlockEntity.SHULKER_BOX, block.level.getChunk(target.getChunkX(), target.getChunkZ()), nbt);
            return null;
        }

        return item;
    }
}
