package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.generator.object.ObjectTallGrass;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

/**
 * @author MagicDroidX
 * Nukkit Project
 */
public class BlockGravel extends BlockFallable {

    @Override
    public int getId() {
        return GRAVEL;
    }

    @Override
    public double getHardness() {
        return 0.6;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public String getName() {
        return "Gravel";
    }

    @Override
    public Item[] getDrops(Item item) {
        if (ThreadLocalRandom.current().nextInt(9) == 0 && !item.hasEnchantment(Enchantment.ID_SILK_TOUCH)) {
            return new Item[]{
                    Item.get(Item.FLINT)
            };
        } else {
            return new Item[]{
                    toItem()
            };
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRAY_BLOCK_COLOR;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
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
