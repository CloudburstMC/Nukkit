package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemSugarcane;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

/**
 * @author Pub4Game
 * @since 09.01.2016
 */
public class BlockSugarcane extends BlockFlowable {

    public BlockSugarcane() {
        this(0);
    }

    public BlockSugarcane(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Sugarcane";
    }

    @Override
    public int getId() {
        return SUGARCANE_BLOCK;
    }

    @Override
    public Item toItem() {
        return new ItemSugarcane();
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(@Nonnull Item item, Player player) {
        if (item.isFertilizer()) { //Bonemeal
            int count = 1;

            for (int i = 1; i <= 2; i++) {
                int id = this.level.getBlockIdAt(this.getFloorX(), this.getFloorY() - i, this.getFloorZ());

                if (id == SUGARCANE_BLOCK) {
                    count++;
                }
            }

            if (count < 3) {
                boolean success = false;
                int toGrow = 3 - count;

                for (int i = 1; i <= toGrow; i++) {
                    Block block = this.up(i);
                    if (block.getId() == 0) {
                        BlockGrowEvent ev = new BlockGrowEvent(block, Block.get(BlockID.SUGARCANE_BLOCK));
                        Server.getInstance().getPluginManager().callEvent(ev);

                        if (!ev.isCancelled()) {
                            this.getLevel().setBlock(block, ev.getNewState(), true);
                            success = true;
                        }
                    } else if (block.getId() != SUGARCANE_BLOCK) {
                        break;
                    }
                }

                if (success) {
                    if (player != null && (player.gamemode & 0x01) == 0) {
                        item.count--;
                    }

                    this.level.addParticle(new BoneMealParticle(this));
                }
            }

            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        Level level = getLevel();
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            level.scheduleUpdate(this, 0);
            return type;
        }
        
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (!isSupportValid()) {
                level.useBreakOn(this);
            }
            return type;
        }
        
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (!isSupportValid()) {
                level.scheduleUpdate(this, 0);
                return type;
            }
            if (getDamage() < 15) {
                setDamage(this.getDamage() + 1);
                level.setBlock(this, this, false);
                return type;
            }
            Block up = up();
            if (up.getId() != AIR) {
                return type;
            }
            
            int height = 0;
            for (Block current = this; height < 3 && current.getId() == SUGARCANE_BLOCK; height++) {
                current = current.down();
            }
            if (height >= 3) {
                return type;
            }

            BlockGrowEvent ev = new BlockGrowEvent(up, Block.get(BlockID.SUGARCANE_BLOCK));
            Server.getInstance().getPluginManager().callEvent(ev);

            if (ev.isCancelled()) {
                return type;
            }
            
            if (!level.setBlock(up, Block.get(BlockID.SUGARCANE_BLOCK), false)) {
                return type;
            }
            
            setDamage(0);
            level.setBlock(this, this, false);
            return type;
        }
        return 0;
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        if (block.getId() != AIR) {
            return false;
        }
        if (isSupportValid()) {
            this.getLevel().setBlock(block, this, true);
            return true;
        }
        return false;
    }

    /**
     * @since 1.2.0.2-PN
     */
    private boolean isSupportValid() {
        Block down = this.down();
        int downId = down.getId();
        if (downId == SUGARCANE_BLOCK) {
            return true;
        }
        if (downId != GRASS && downId != DIRT && downId != SAND || down.getId() == PODZOL) {
            return false;
        }
        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            Block possibleWater = down.getSide(face);
            if (possibleWater instanceof BlockWater
                    || possibleWater instanceof BlockIceFrosted
                    || possibleWater.getLevelBlockAtLayer(1) instanceof BlockWater) {
                return true;
            }
        }
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }
}
