package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.block.BlockSpreadEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

public class BlockBuddingAmethyst extends BlockSolid {

    public BlockBuddingAmethyst() {
    }

    @Override
    public int getId() {
        return BUDDING_AMETHYST;
    }

    @Override
    public double getResistance() {
        return 1.5;
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public String getName() {
        return "Budding Amethyst";
    }

    @Override
    public int onUpdate(int type) {
        if (type != Level.BLOCK_UPDATE_RANDOM || ThreadLocalRandom.current().nextInt(4) != 0) {
            return type;
        }

        BlockFace face = BlockFace.values()[ThreadLocalRandom.current().nextInt(BlockFace.values().length)];
        Block block = this.getSide(face);

        BlockAmethystBud targetBlock = null;
        if (block.getId() == AIR || (Block.isWater(block.getId()) && block.getDamage() == 8)) {
            targetBlock = (BlockAmethystBud) Block.get(SMALL_AMETHYST_BUD);
        } else if (block.getId() == SMALL_AMETHYST_BUD && ((BlockAmethystBud) block).getBlockFace() == face) {
            targetBlock = (BlockAmethystBud) Block.get(MEDIUM_AMETHYST_BUD);
        } else if (block.getId() == MEDIUM_AMETHYST_BUD && ((BlockAmethystBud) block).getBlockFace() == face) {
            targetBlock = (BlockAmethystBud) Block.get(LARGE_AMETHYST_BUD);
        } else if (block.getId() == LARGE_AMETHYST_BUD && ((BlockAmethystBud) block).getBlockFace() == face) {
            targetBlock = (BlockAmethystBud) Block.get(AMETHYST_CLUSTER);
        }

        if (targetBlock != null) {
            targetBlock.setBlockFace(face);

            BlockSpreadEvent event = new BlockSpreadEvent(block, this, targetBlock);
            this.getLevel().getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                this.getLevel().setBlock(block, event.getNewState(), false, true);
            }
        }
        return type;
    }

    @Override
    public boolean onBreak(Item item, Player player) {
        for (BlockFace face : BlockFace.values()) {
            Block side = this.getSide(face);
            if (side instanceof BlockAmethystBud && ((BlockAmethystBud) side).getBlockFace() == face) {
                this.getLevel().setBlock(side, Block.get(BlockID.AIR), true, true);
                this.getLevel().addParticle(new DestroyBlockParticle(side.add(0.5), side));
            }
        }
        return super.onBreak(item, player);
    }

    @Override
    public boolean breakWhenPushed() {
        return true;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.PURPLE_BLOCK_COLOR;
    }
}
