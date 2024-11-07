package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemDye;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.BoneMealParticle;

public class BlockDirtRooted extends BlockDirt {

    public BlockDirtRooted() {
        this(0);
    }

    public BlockDirtRooted(int meta) {
        super(0); // no different states
    }

    @Override
    public int getId() {
        return ROOTED_DIRT;
    }

    @Override
    public String getName() {
        return "Rooted Dirt";
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        Block up = this.up();
        if (item.isShovel() && (up instanceof BlockAir || up instanceof BlockFlowable)) {
            item.useOn(this);
            this.getLevel().setBlock(this, Block.get(GRASS_PATH));
            if (player != null) {
                player.getLevel().addSound(player, Sound.STEP_GRASS);
            }
            return true;
        }

        if (item.isHoe() && (up instanceof BlockAir || up instanceof BlockFlowable)) {
            item.useOn(this);
            this.getLevel().setBlock(this, Block.get(DIRT));
            if (player != null) {
                player.getLevel().addSound(player, Sound.STEP_GRASS);
            }
            this.getLevel().dropItem(this.add(0.5, 0.8, 0.5), new ItemBlock(Block.get(HANGING_ROOTS), 0));
            return true;
        }

        if (item.getId() != Item.DYE || item.getDamage() != ItemDye.BONE_MEAL) {
            return false;
        }

        Block down = this.down();
        BlockGrowEvent event = new BlockGrowEvent(down, Block.get(BlockID.HANGING_ROOTS, 0, down));
        this.getLevel().getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        if (down.getId() == AIR || down.canBeReplaced()) {
            this.getLevel().setBlock(down, Block.get(HANGING_ROOTS));
            this.level.addParticle(new BoneMealParticle(down));
            return true;
        }
        return false;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId()), 0, 1);
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{ this.toItem() };
    }
}
