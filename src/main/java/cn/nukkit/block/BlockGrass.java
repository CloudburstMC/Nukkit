package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.block.BlockSpreadEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.level.generator.object.ObjectTallGrass;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Utils;

/**
 * @author Angelic47
 * Nukkit Project
 */
public class BlockGrass extends BlockDirt {

    public BlockGrass() {
        this(0);
    }

    public BlockGrass(int meta) {
        super(0);
    }

    @Override
    public int getId() {
        return GRASS;
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
    public String getName() {
        return "Grass Block";
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.getId() == Item.DYE && item.getDamage() == ItemDye.BONE_MEAL) {
            ObjectTallGrass.growGrass(this.getLevel(), this);
            this.level.addParticle(new BoneMealParticle(this));
            if (player != null) {
                if (!player.isCreative()) {
                    item.count--;
                }
            }
            return true;
        } else if (item.isHoe()) {
            Block up = this.up();
            if (up instanceof BlockAir || up instanceof BlockFlowable) {
                item.useOn(this);
                this.getLevel().setBlock(this, Block.get(FARMLAND));
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
        }

        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            Block up = this.up();
            if (up instanceof BlockUnknown) {
                return 0;
            }

            if ((up.isSolid() && !up.isTransparent()) || up instanceof BlockLiquid) {
                BlockSpreadEvent ev = new BlockSpreadEvent(this, this, Block.get(BlockID.DIRT));
                Server.getInstance().getPluginManager().callEvent(ev);
                if (!ev.isCancelled()) {
                    this.getLevel().setBlock(this, ev.getNewState());
                }
                return 0;
            }

            int xx = Utils.rand((int) x - 1, (int) x + 1);
            int yy = Utils.rand((int) y - 2, (int) y + 2);
            int zz = Utils.rand((int) z - 1, (int) z + 1);
            Block block = this.getLevel().getBlock(xx, yy, zz);
            if (block.getId() == Block.DIRT && block.getDamage() == 0) {
                up = block.up();
                if (!up.isSolid() && up.isTransparent() && !(up instanceof BlockLiquid)) {
                    BlockSpreadEvent ev = new BlockSpreadEvent(block, this, Block.get(BlockID.GRASS));
                    Server.getInstance().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        this.getLevel().setBlock(block, ev.getNewState());
                    }
                }
            }
        }
        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.GRASS_BLOCK_COLOR;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public int getFullId() {
        return this.getId() << Block.DATA_BITS;
    }

    @Override
    public void setDamage(int meta) {
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.hasEnchantment(Enchantment.ID_SILK_TOUCH)) {
            return new Item[]{this.toItem()};
        }
        return new Item[]{new ItemBlock(Block.get(BlockID.DIRT))};
    }
}