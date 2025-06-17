package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.*;
import cn.nukkit.level.Sound;
import cn.nukkit.level.generator.object.ObjectTallGrass;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.utils.BlockColor;

/**
 * @author MagicDroidX
 * AMAZING COARSE DIRT added by kvetinac97
 * Nukkit Project
 */
public class BlockDirt extends BlockSolidMeta {

    public BlockDirt() {
        this(0);
    }

    public BlockDirt(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DIRT;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public String getName() {
        return this.getDamage() == 0 ? "Dirt" : "Coarse Dirt";
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.isHoe()) {
            Block up = this.up();
            if (up instanceof BlockAir || up instanceof BlockFlowable) {
                item.useOn(this);
                this.getLevel().setBlock(this, this.getDamage() == 0 ? get(FARMLAND) : get(DIRT), true);
                if (player != null) {
                    player.getLevel().addSound(player, Sound.STEP_GRASS);
                }
                return true;
            }
        } else if (item.isShovel()) {
            Block up = this.up();
            if (up instanceof BlockAir || up instanceof BlockFlowable) {
                item.useOn(this);
                this.getLevel().setBlock(this, Block.get(GRASS_PATH));
                if (player != null) {
                    player.getLevel().addSound(player, Sound.STEP_GRASS);
                }
                return true;
            }
        } else if (item.getId() == Item.DYE && item.getDamage() == ItemDye.BONE_MEAL) {
            Block up = this.up();
            if (up instanceof BlockWater) {
                if (player != null && !player.isCreative()) {
                    item.count--;
                }
                this.level.addParticle(new BoneMealParticle(this));
                if (up.up() instanceof BlockWater) {
                    ObjectTallGrass.growSeagrass(this.getLevel(), this);
                }
                return true;
            }
        } else if (item.getId() == Item.POTION && item.getDamage() == ItemPotion.NO_EFFECTS) {
            if (player != null) {
                if (!player.isCreative()) {
                    item.count--;
                }
                Item emptyBottle = Item.get(Item.GLASS_BOTTLE);
                if (player.getInventory().canAddItem(emptyBottle)) {
                    player.getInventory().addItem(emptyBottle);
                } else {
                    player.getLevel().dropItem(player.add(0, 1.3, 0), emptyBottle, player.getDirectionVector().multiply(0.4));
                }
            }
            this.getLevel().setBlock(this, get(MUD), true);
            return true;
        }

        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        int damage = this.getDamage() & 0x01;
        return new Item[]{new ItemBlock(Block.get(BlockID.DIRT, damage), damage)};
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }
}
