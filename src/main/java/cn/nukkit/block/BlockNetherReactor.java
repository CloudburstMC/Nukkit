package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityNetherReactor;
import cn.nukkit.blockproperty.value.NetherReactorState;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by good777LUCKY
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockNetherReactor extends BlockSolid {
    private static final boolean DEBUG = false;

    public BlockNetherReactor() {
        // Does nothing
    }
    
    @Override
    public int getId() {
        return NETHER_REACTOR;
    }
    
    @Override
    public String getName() {
        return "Nether Reactor Core";
    }
    
    @Override
    public double getHardness() {
        return 10;
    }
    
    @Override
    public double getResistance() {
        return 6;
    }
    
    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
    
    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }
    
    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe()) {
            return new Item[]{
                    Item.get(ItemID.DIAMOND, 0, 3),
                    Item.get(ItemID.IRON_INGOT, 0, 6)
            };
        } else {
            return new Item[0];
        }
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (super.place(item, block, target, face, fx, fy, fz, player)) {
            createBlockEntity();
            return true;
        }
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.IRON_BLOCK_COLOR;
    }
    
    @Nullable
    @Override
    public BlockEntityNetherReactor getBlockEntity() {
        return getTypedBlockEntity(BlockEntityNetherReactor.class);
    }

    @Nonnull
    @Override
    protected BlockEntityNetherReactor createBlockEntity() {
        BlockEntityNetherReactor blockEntity = createBlockEntity(BlockEntityNetherReactor.class, BlockEntity.NETHER_REACTOR);
        blockEntity.setProgress(900);
        return blockEntity;
    }

    @Nonnull
    @Override
    public BlockEntityNetherReactor getOrCreateBlockEntity() {
        return (BlockEntityNetherReactor) super.getOrCreateBlockEntity();
    }

    @Override
    public boolean canBeActivated() {
        return DEBUG;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (!DEBUG) {
            return false;
        }
        BlockEntityNetherReactor blockEntity = getOrCreateBlockEntity();
        NetherReactorState progress = blockEntity.getReactorState();
        try {
            progress = NetherReactorState.getFromData(progress.ordinal() + 1);
        } catch (IndexOutOfBoundsException ignored) {
            progress = NetherReactorState.READY;
        }
        blockEntity.setReactorState(progress);
        blockEntity.spawnToAll();
        return true;
    }
}
