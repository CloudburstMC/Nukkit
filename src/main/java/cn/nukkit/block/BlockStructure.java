package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockProperty;
import cn.nukkit.blockproperty.value.StructureBlockType;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

/**
 * @author good777LUCKY
 */
@Since("1.4.0.0-PN")
@PowerNukkitOnly
public class BlockStructure extends BlockSolidMeta {
    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public static final BlockProperty<StructureBlockType> STRUCTURE_BLOCK_TYPE = new ArrayBlockProperty<>(
            "structure_block_type", true, StructureBlockType.class
    );

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public static final BlockProperties PROPERTIES = new BlockProperties(STRUCTURE_BLOCK_TYPE);

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public BlockStructure() {
        this(0);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public BlockStructure(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return STRUCTURE_BLOCK;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public StructureBlockType getStructureBlockType() {
        return getPropertyValue(STRUCTURE_BLOCK_TYPE);
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setStructureBlockType(StructureBlockType type) {
        setPropertyValue(STRUCTURE_BLOCK_TYPE, type);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }
    
    @Override
    public boolean onActivate(@Nonnull Item item, Player player) {
        if (player != null) {
            if (player.isCreative() && player.isOp()) {
                // TODO: Add UI
            }
        }
        return true;
    }
    
    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        if (player != null && (!player.isCreative() || !player.isOp())) {
            return false;
        }
        this.getLevel().setBlock(block, this, true);
        // TODO: Add Block Entity
        return true;
    }
    
    @Override
    public String getName() {
        return getStructureBlockType().getEnglishName();
    }
    
    @Override
    public double getHardness() {
        return -1;
    }
    
    @Override
    public double getResistance() {
        return 18000000;
    }
    
    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
    
    @Override
    public boolean isBreakable(Item item) {
        return false;
    }
    
    @Override
    public boolean canBePushed() {
        return false;
    }
    
    @Override
    public boolean canBePulled() {
        return false;
    }
    
    @Override
    public BlockColor getColor() {
        return BlockColor.LIGHT_GRAY_BLOCK_COLOR;
    }


}
