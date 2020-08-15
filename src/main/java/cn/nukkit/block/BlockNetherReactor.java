package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityNetherReactor;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDiamond;
import cn.nukkit.item.ItemIngotIron;
import cn.nukkit.item.ItemTool;
import cn.nukkit.utils.BlockColor;

/**
 * Created by good777LUCKY
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockNetherReactor extends BlockSolid {

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
        return 30;
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
                new ItemDiamond(0, 3),
                new ItemIngotIron(0, 6)
            };
        } else {
            return new Item[0];
        }
    }
    
    @Override
    public BlockColor getColor() {
        return BlockColor.IRON_BLOCK_COLOR;
    }
    
    private BlockEntityNetherReactor getOrInitBlockEntity() {
        BlockEntity blockEntity = getLevel().getBlockEntity(this);
        if (blockEntity instanceof BlockEntityNetherReactor) {
            return (BlockEntityNetherReactor) blockEntity;
        }
        if (blockEntity != null) {
            throw new IllegalStateException("The block entity space at "+getLocation()+" is already occupied with "+blockEntity);
        }
        blockEntity = BlockEntity.createBlockEntity(BlockEntity.NETHER_REACTOR, this);
        if (blockEntity instanceof BlockEntityNetherReactor) {
            return (BlockEntityNetherReactor) blockEntity;
        }
        String message = "Failed to create the NetherReactor block entity at " + getLocation() + ", received:" + blockEntity;
        blockEntity.close();
        throw new IllegalStateException(message);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public NetherReactorProgress getProgress() {
        return getOrInitBlockEntity().getProgress();
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setProgress(NetherReactorProgress progress) {
        getOrInitBlockEntity().setProgress(progress);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public enum NetherReactorProgress {
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        READY,

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        INITIALIZED,

        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        FINISHED;
        
        private static final NetherReactorProgress[] values = values();
        
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        public static NetherReactorProgress getFromData(int data) {
            return values[data];
        }
    }
}
