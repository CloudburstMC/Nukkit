package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nonnull;

import static cn.nukkit.blockproperty.CommonBlockProperties.DEPRECATED;

/**
 * @author CreeperFace
 */
public class BlockBone extends BlockSolidMeta implements Faceable {
    private static final ArrayBlockProperty<String> SPECIAL_PILLAR_AXIS = new ArrayBlockProperty<>("pillar_axis", false,
        new String[] {
                "y",
                "unused1",
                "unused2",
                "unused3",
                "x",
                "unused5",
                "unused6",
                "unused7",
                "z",
        }
    );

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(SPECIAL_PILLAR_AXIS, DEPRECATED);

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

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
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
