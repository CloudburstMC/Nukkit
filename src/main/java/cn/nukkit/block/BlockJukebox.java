package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityJukebox;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemRecord;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by CreeperFace on 7.8.2017.
 */
public class BlockJukebox extends BlockSolid implements BlockEntityHolder<BlockEntityJukebox> {

    public BlockJukebox() {
        // Does nothing
    }

    @Override
    public String getName() {
        return "Jukebox";
    }

    @Override
    public int getId() {
        return JUKEBOX;
    }

    @Nonnull
    @Override
    public Class<? extends BlockEntityJukebox> getBlockEntityClass() {
        return BlockEntityJukebox.class;
    }

    @Nonnull
    @Override
    public String getBlockEntityType() {
        return BlockEntity.JUKEBOX;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    public boolean onActivate(@Nonnull Item item, @Nullable Player player) {
        BlockEntityJukebox jukebox = getOrCreateBlockEntity();
        if (jukebox.getRecordItem().getId() != 0) {
            jukebox.dropItem();
            return true;
        } 
        
        if (!item.isNull() && item instanceof ItemRecord) {
            Item record = item.clone();
            record.count = 1;
            item.count--;
            jukebox.setRecordItem(record);
            jukebox.play();
            return true;
        }

        return false;
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        return BlockEntityHolder.setBlockAndCreateEntity(this) != null;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }
}
