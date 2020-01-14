package cn.nukkit.block;

import cn.nukkit.Server;
import cn.nukkit.event.block.LeavesDecayEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3f;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Hash;
import cn.nukkit.utils.Identifier;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongSet;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.BlockIds.*;
import static cn.nukkit.item.ItemIds.APPLE;
import static cn.nukkit.item.ItemIds.STICK;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockLeaves extends BlockTransparent {
    public static final int OAK = 0;
    public static final int SPRUCE = 1;
    public static final int BIRCH = 2;
    public static final int JUNGLE = 3;

    public BlockLeaves(Identifier id) {
        super(id);
    }

    @Override
    public double getHardness() {
        return 0.2;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHEARS;
    }

    @Override
    public int getBurnChance() {
        return 30;
    }

    @Override
    public int getBurnAbility() {
        return 60;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        this.setPersistent(true);
        this.getLevel().setBlock(this, this, true);
        return true;
    }

    @Override
    public Item toItem() {
        return Item.get(id, this.getDamage() & 0x3, 1);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isShears()) {
            return new Item[]{
                    toItem()
            };
        } else {
            if (this.canDropApple() && ThreadLocalRandom.current().nextInt(200) == 0) {
                return new Item[]{
                        Item.get(APPLE)
                };
            }
            if (ThreadLocalRandom.current().nextInt(20) == 0) {
                if (ThreadLocalRandom.current().nextBoolean()) {
                    return new Item[]{
                            Item.get(STICK, 0, ThreadLocalRandom.current().nextInt(1, 2))
                    };
                } else if ((this.getDamage() & 0x03) != JUNGLE || ThreadLocalRandom.current().nextInt(20) == 0) {
                    return new Item[]{
                            this.getSapling()
                    };
                }
            }
        }
        return new Item[0];
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM && !isPersistent() && !isCheckDecay()) {
            setCheckDecay(true);
            getLevel().setBlock(this, this, false, false);
        } else if (type == Level.BLOCK_UPDATE_RANDOM && isCheckDecay() && !isPersistent()) {
            setDamage(getDamage() & 0x03);
            int check = 0;

            LeavesDecayEvent ev = new LeavesDecayEvent(this);

            Server.getInstance().getPluginManager().callEvent(ev);
            if (ev.isCancelled() || findLog(this, new LongArraySet(), 0, check)) {
                getLevel().setBlock(this, this, false, false);
            } else {
                getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    private Boolean findLog(Block pos, LongSet visited, Integer distance, Integer check) {
        return findLog(pos, visited, distance, check, null);
    }

    private Boolean findLog(Block pos, LongSet visited, Integer distance, Integer check, BlockFace fromSide) {
        ++check;
        long index = Hash.hashBlock((int) pos.x, (int) pos.y, (int) pos.z);
        if (visited.contains(index)) return false;
        if (pos.getId() == LOG || pos.getId() == LOG2) return true;
        if ((pos.getId() == LEAVES || pos.getId() == LEAVES2) && distance <= 4) {
            visited.add(index);
            Identifier down = pos.down().getId();
            if (down == LOG || down == LOG2) {
                return true;
            }
            if (fromSide == null) {
                //North, East, South, West
                for (int side = 2; side <= 5; ++side) {
                    if (this.findLog(pos.getSide(BlockFace.fromIndex(side)), visited, distance + 1, check, BlockFace.fromIndex(side)))
                        return true;
                }
            } else { //No more loops
                switch (fromSide) {
                    case NORTH:
                        if (this.findLog(pos.getSide(BlockFace.NORTH), visited, distance + 1, check, fromSide))
                            return true;
                        if (this.findLog(pos.getSide(BlockFace.WEST), visited, distance + 1, check, fromSide))
                            return true;
                        if (this.findLog(pos.getSide(BlockFace.EAST), visited, distance + 1, check, fromSide))
                            return true;
                        break;
                    case SOUTH:
                        if (this.findLog(pos.getSide(BlockFace.SOUTH), visited, distance + 1, check, fromSide))
                            return true;
                        if (this.findLog(pos.getSide(BlockFace.WEST), visited, distance + 1, check, fromSide))
                            return true;
                        if (this.findLog(pos.getSide(BlockFace.EAST), visited, distance + 1, check, fromSide))
                            return true;
                        break;
                    case WEST:
                        if (this.findLog(pos.getSide(BlockFace.NORTH), visited, distance + 1, check, fromSide))
                            return true;
                        if (this.findLog(pos.getSide(BlockFace.SOUTH), visited, distance + 1, check, fromSide))
                            return true;
                        if (this.findLog(pos.getSide(BlockFace.WEST), visited, distance + 1, check, fromSide))
                            return true;
                    case EAST:
                        if (this.findLog(pos.getSide(BlockFace.NORTH), visited, distance + 1, check, fromSide))
                            return true;
                        if (this.findLog(pos.getSide(BlockFace.SOUTH), visited, distance + 1, check, fromSide))
                            return true;
                        if (this.findLog(pos.getSide(BlockFace.EAST), visited, distance + 1, check, fromSide))
                            return true;
                        break;
                }
            }
        }
        return false;
    }

    public boolean isCheckDecay() {
        return (this.getDamage() & 0x08) != 0;
    }

    public void setCheckDecay(boolean checkDecay) {
        if (checkDecay) {
            this.setDamage(this.getDamage() | 0x08);
        } else {
            this.setDamage(this.getDamage() & ~0x08);
        }
    }

    public boolean isPersistent() {
        return (this.getDamage() & 0x04) != 0;
    }

    public void setPersistent(boolean persistent) {
        if (persistent) {
            this.setDamage(this.getDamage() | 0x04);
        } else {
            this.setDamage(this.getDamage() & ~0x04);
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    protected boolean canDropApple() {
        return (this.getDamage() & 0x03) == OAK;
    }

    protected Item getSapling() {
        return Item.get(SAPLING, this.getDamage() & 0x03);
    }
}
