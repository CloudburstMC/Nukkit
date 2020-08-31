package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nonnull;

/**
 * @author CreeperFace
 */
public class BlockBone extends BlockSolidMeta implements Faceable {

    private static final int[] FACES = {
            0,
            0,
            0b1000,
            0b1000,
            0b0100,
            0b0100
    };

    public BlockBone() {
        this(0);
    }

    public BlockBone(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BONE_BLOCK;
    }

    @Override
    public String getName() {
        return "Bone Block";
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x7);
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        this.setDamage(((this.getDamage() & 0x3) | FACES[face.getIndex()]));
        this.getLevel().setBlock(block, this, true);
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SAND_BLOCK_COLOR;
    }
}
