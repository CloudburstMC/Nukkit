package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import static cn.nukkit.blockproperty.CommonBlockProperties.DIRECTION;

import javax.annotation.Nonnull;

/**
 * @author xtypr
 * @since 2015/12/8
 */
public class BlockPumpkin extends BlockSolidMeta implements Faceable {
    public static final BlockProperties PROPERTIES = new BlockProperties(
        DIRECTION
    );
    
    public BlockPumpkin() {
        this(0);
    }

    public BlockPumpkin(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Pumpkin";
    }

    @Override
    public int getId() {
        return PUMPKIN;
    }
    
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }
    
    @Override
    public double getHardness() {
        return 1;
    }

    @Override
    public double getResistance() {
        return 1;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }
    
    @Override
    public boolean canBeActivated() {
        return true;
    }
    
    @Override
    public boolean onActivate(@Nonnull Item item, Player player) {
        if (item.isShears()) {
            Block carvedPumpkin = Block.get(CARVED_PUMPKIN);
            // TODO: Use the activated block face not the player direction
            if (player == null) {
                carvedPumpkin.setBlockFace(BlockFace.SOUTH);
            } else {
                carvedPumpkin.setBlockFace(player.getDirection().getOpposite());
            }
            item.useOn(this);
            this.level.setBlock(this, carvedPumpkin, true, true);
            return true;
        }
        return false;
    }
    
    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        if (player == null) {
            setBlockFace(BlockFace.SOUTH);
        } else {
            setBlockFace(player.getDirection().getOpposite());
        }
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ORANGE_BLOCK_COLOR;
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }
    
    @Override
    public BlockFace getBlockFace() {
        return getPropertyValue(DIRECTION);
    }
    
    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(DIRECTION, face);
    }
}
