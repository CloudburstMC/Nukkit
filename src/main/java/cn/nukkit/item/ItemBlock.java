package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.blockstate.exception.InvalidBlockStateException;
import lombok.extern.log4j.Log4j2;

/**
 * @author MagicDroidX (Nukkit Project)
 */
@Log4j2
public class ItemBlock extends Item {
    public ItemBlock(Block block) {
        this(block, 0, 1);
    }

    public ItemBlock(Block block, Integer meta) {
        this(block, meta, 1);
    }

    public ItemBlock(Block block, Integer meta, int count) {
        super(block.getItemId(), meta, count, block.getName());
        this.block = block;
    }

    public void setDamage(Integer meta) {
        int blockMeta;
        if (meta != null) {
            this.meta = meta;
            blockMeta = meta;
        } else {
            this.hasMeta = false;
            blockMeta = 0;
        }
        int blockId = block.getId();
        try {
            if (block instanceof BlockUnknown) {
                block = BlockState.of(blockId, blockMeta).getBlock();
                log.info("An invalid ItemBlock for {} was set to a valid meta {} and it is now safe again", block.getPersistenceName(), meta);
            } else {
                block.setDataStorageFromInt(blockMeta);
            }
        } catch (InvalidBlockStateException e) {
            log.warn("An ItemBlock for {} was set to have meta {}"+
                    " but this value is not valid. The item stack is now unsafe.", block.getPersistenceName(), meta, e);
            block = new BlockUnknown(blockId, blockMeta);
            return;
        }

        int expected = block.asItemBlock().getDamage();
        if (expected != blockMeta) {
            log.warn("An invalid ItemBlock for {} was set to an valid meta {} for item blocks, " +
                    "it was expected to have meta {} the stack is now unsafe.\nProperties: {}",
                    block.getPersistenceName(), meta, expected, block.getProperties());
        }
    }

    @Override
    public ItemBlock clone() {
        ItemBlock block = (ItemBlock) super.clone();
        block.block = this.block.clone();
        return block;
    }

    public Block getBlock() {
        return this.block;
    }

    @Override
    public boolean isLavaResistant() {
        return block.isLavaResistant();
    }
}
