package cn.nukkit.block;

import cn.nukkit.Server;
import cn.nukkit.event.block.LeavesDecayEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Hash;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongSet;

import java.util.concurrent.ThreadLocalRandom;

/**
 * author: Angelic47
 * Nukkit Project
 */
public class BlockLeaves extends BlockTransparentMeta {
    public static final int OAK = 0;
    public static final int SPRUCE = 1;
    public static final int BIRCH = 2;
    public static final int JUNGLE = 3;
    public static final int ACACIA = 4;
    public static final int DARK_OAK = 5;

    public BlockLeaves() {
        this(0);
    }

    public BlockLeaves(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return LEAVES;
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
    public String getName() {
        String[] names = new String[]{
                "Oak Leaves",
                "Spruce Leaves",
                "Birch Leaves",
                "Jungle Leaves"
        };
        return names[this.getDamage() & 0x03];
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
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        this.setDamage(this.getDamage() | 0x04);
        this.getLevel().setBlock(this, this, true);
        return true;
    }

    @Override
    public Item toItem() {
        return Item.get(BlockID.LEAVES);
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
                        Item.get(Item.APPLE)
                };
            }
            if (ThreadLocalRandom.current().nextInt(20) == 0) {
                if (ThreadLocalRandom.current().nextBoolean()) {
                    return new Item[]{
                            Item.get(Item.STICK, 0, ThreadLocalRandom.current().nextInt(1, 2))
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
        if (type == Level.BLOCK_UPDATE_RANDOM && (getDamage() & 0b00001100) == 0x00) {
            setDamage(getDamage() | 0x08);
            getLevel().setBlock(this, this, false, false);
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if ((getDamage() & 0b00001100) == 0x08) {
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
        if (pos.getId() == WOOD || pos.getId() == WOOD2) return true;
        if ((pos.getId() == LEAVES || pos.getId() == LEAVES2) && distance <= 4) {
            visited.add(index);
            int down = pos.down().getId();
            if (down == WOOD || down == WOOD2) {
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

    public boolean isChechDecay() {
        return (this.getDamage() & 0x08) > 0;
    }

    public boolean isDecayable() {
        return (this.getDamage() & 0x04) == 0;
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
        return new ItemBlock(get(SAPLING), this.getDamage() & 0x03);
    }
}
