package cn.nukkit.block;

import cn.nukkit.Server;
import cn.nukkit.event.block.LeavesDecayEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemApple;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Hash;
import it.unimi.dsi.fastutil.longs.LongArraySet;
import it.unimi.dsi.fastutil.longs.LongSet;

/**
 * Created on 2015/12/1 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockLeaves2 extends BlockLeaves {

    public BlockLeaves2() {
        this(0);
    }

    public BlockLeaves2(int meta) {
        super(meta);
    }

    public String getName() {
        String[] names = new String[]{
                "Acacia Leaves",
                "Dark Oak Leaves"
        };
        return names[this.getDamage() & 0x01];
    }

    @Override
    public int getId() {
        return LEAVES2;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isShears()) {
            return new Item[]{
                    toItem()
            };
        } else {
            if ((int) ((Math.random()) * 200) == 0 && this.getDamage() == DARK_OAK) {
                return new Item[]{
                        new ItemApple()
                };
            }
            if ((int) ((Math.random()) * 20) == 0) {
                return new Item[]{
                        new ItemBlock(new BlockSapling(), this.getDamage() & 0x05, 1)
                };
            }
        }
        return new Item[0];
    }
private Boolean findLog(Block pos, LongSet visited, Integer distance, Integer check) {
        return findLog(pos, visited, distance, check, null);
    }
    
    private Boolean findLog(Block pos, LongSet visited, Integer distance, Integer check, BlockFace fromSide) {
        ++check;
        long index = Hash.hashBlock((int) pos.x, (int) pos.y, (int) pos.z);
        if (visited.contains(index)) return false;
        if (pos.getId() == Block.WOOD2) return true;
        if (pos.getId() == Block.LEAVES2 && distance < 4) {
            visited.add(index);
            Integer down = pos.down().getId();
            if (down == Item.WOOD2) {
                return true;
            }
            if (fromSide == null) {
                //North, East, South, West
                for (Integer side = 2; side <= 5; ++side) {
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
    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_RANDOM && (getDamage() & 0b00001100) == 0x00) {
            setDamage(getDamage() | 0x08);
            getLevel().setBlock(this, this, false, false);
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if ((getDamage() & 0b00001100) == 0x04) {
                setDamage(getDamage() & 0x05);
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
}
