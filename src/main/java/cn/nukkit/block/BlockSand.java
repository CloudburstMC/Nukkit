package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.generator.object.ObjectTallGrass;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.utils.BlockColor;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class BlockSand extends BlockFallableMeta {

    public static final int DEFAULT = 0;
    public static final int RED = 1;

    public BlockSand() {
        this(0);
    }

    public BlockSand(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SAND;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public String getName() {
        if (this.getDamage() == 0x01) {
            return "Red Sand";
        }

        return "Sand";
    }

    @Override
    public BlockColor getColor() {
        if (this.getDamage() == 0x01) {
            return BlockColor.ORANGE_BLOCK_COLOR;
        }

        return BlockColor.SAND_BLOCK_COLOR;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null && item.getId() == Item.DYE && item.getDamage() == ItemDye.BONE_MEAL) {
            Block up = this.up();
            if (up instanceof BlockWater) {
                if (!player.isCreative()) {
                    item.count--;
                }
                this.level.addParticle(new BoneMealParticle(this));
                if (up.getDamage() == 0 && up.up() instanceof BlockWater) {
                    ObjectTallGrass.growSeagrass(this.getLevel(), this);
                }
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }
}
