package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.block.LeavesDecayEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Utils;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Angelic47
 * Nukkit Project
 */
public class BlockLeaves extends BlockTransparentMeta {

    public static final int OAK = 0;
    public static final int SPRUCE = 1;
    public static final int BIRCH = 2;
    public static final int JUNGLE = 3;

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
        return ItemTool.TYPE_HOE;
    }

    private static final String[] NAMES = {
            "Oak Leaves",
            "Spruce Leaves",
            "Birch Leaves",
            "Jungle Leaves"
    };

    @Override
    public String getName() {
        return NAMES[this.getDamage() & 0x03];
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
        setPersistent(true);
        this.getLevel().setBlock(this, this, true, true);
        return true;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, this.getDamage() & 0x3, 1);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isShears()) {
            return new Item[]{
                    toItem()
            };
        } else {
            if (item.hasEnchantment(Enchantment.ID_SILK_TOUCH)) {
                return new Item[]{this.toItem()};
            }
            if (this.canDropApple() && ThreadLocalRandom.current().nextInt(200) == 0) {
                return new Item[]{
                        Item.get(Item.APPLE)
                };
            }
            if (ThreadLocalRandom.current().nextInt(20) == 0) {
                if (Utils.rand()) {
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
        if (type == Level.BLOCK_UPDATE_NORMAL && !isPersistent() && !isCheckDecay()) {
            if (this.level.getBlockIdAt((int) this.x, (int) this.y, (int) this.z) != this.getId()) {
                return 0;
            }

            setCheckDecay(true);
            getLevel().setBlock((int) this.x, (int) this.y, (int) this.z, BlockLayer.NORMAL, this, false, false, false); // No need to send this to client

            return Level.BLOCK_UPDATE_NORMAL;
        } else if (type == Level.BLOCK_UPDATE_RANDOM && isCheckDecay() && !isPersistent()) {
            LeavesDecayEvent ev = new LeavesDecayEvent(this);
            Server.getInstance().getPluginManager().callEvent(ev);

            if (ev.isCancelled() || findLog()) {
                setCheckDecay(false);
                getLevel().setBlock((int) this.x, (int) this.y, (int) this.z, BlockLayer.NORMAL, this, false, false, false); // No need to send this to client
            } else {
                getLevel().useBreakOn(this);
            }

            return Level.BLOCK_UPDATE_RANDOM;
        }
        return 0;
    }

    public boolean isCheckDecay() {
        return (this.getDamage() & 0x08) != 0;
    }

    public void setCheckDecay(boolean checkDecay) {
        if (checkDecay) {
            this.setDamage(this.getDamage() | 0x08);
        } else {
            this.setDamage(this.getDamage() & -9);
        }
    }

    public boolean isPersistent() {
        return (this.getDamage() & 0x04) != 0;
    }

    public void setPersistent(boolean persistent) {
        if (persistent) {
            this.setDamage(this.getDamage() | 0x04);
        } else {
            this.setDamage(this.getDamage() & -5);
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
        return Item.get(BlockID.SAPLING, this.getDamage() & 0x03);
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.WHEN_PLACED_IN_WATER;
    }

    @Override
    public boolean breakWhenPushed() {
        return true;
    }

    private boolean findLog() {
        Set<Block> visited = new HashSet<>();
        Queue<Block> queue = new LinkedList<>();
        Map<Block, Integer> distance = new HashMap<>();

        queue.offer(this);
        visited.add(this);
        distance.put(this, 0);

        while (!queue.isEmpty()) {
            Block currentBlock = queue.poll();
            int currentDistance = distance.get(currentBlock);

            if (currentDistance > 4) {
                return false;
            }

            for (BlockFace face : BlockFace.values()) {
                Block nextBlock = currentBlock.getSideIfLoadedOrNull(face); // If side chunk not loaded, do not load or decay
                if (nextBlock == null || nextBlock instanceof BlockWood) {
                    return true;
                }

                if (nextBlock instanceof BlockLeaves && !visited.contains(nextBlock)) {
                    queue.offer(nextBlock);
                    visited.add(nextBlock);
                    distance.put(nextBlock, currentDistance + 1);
                }
            }
        }

        return false;
    }
}
