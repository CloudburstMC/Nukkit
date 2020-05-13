package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.block.LeavesDecayEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.BlockColor;

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
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public int getBurnAbility() {
        return 60;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        this.setPersistent(true);
        this.getLevel().setBlock(this, this, true);
        return true;
    }

    @Override
    public Item toItem() {
        return Item.get(BlockID.LEAVES, this.getDamage() & 0x3);
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
        if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (isCheckDecay()) {
                if (isPersistent() || findLog(this, 7)) {
                    setCheckDecay(false);
                    getLevel().setBlock(this, this, false, false);
                } else {
                    LeavesDecayEvent ev = new LeavesDecayEvent(this);
                    Server.getInstance().getPluginManager().callEvent(ev);
                    if (!ev.isCancelled()) {
                        getLevel().useBreakOn(this);
                    }
                }
                return type;
            }
        } else if (type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (!isCheckDecay()) {
                setCheckDecay(true);
                getLevel().setBlock(this, this, false, false);
            }
            
            // Slowly propagates the need to update instead of peaking down the TPS for huge trees
            for (BlockFace side : BlockFace.values()) {
                Block other = getSide(side);
                if (other instanceof BlockLeaves) {
                    BlockLeaves otherLeave = (BlockLeaves) other;
                    if (!otherLeave.isCheckDecay()) {
                        getLevel().scheduleUpdate(otherLeave, 2);
                    }
                }
            }
            return type;
        }
        return type;
    }

    private Boolean findLog(Block pos, Integer distance) {
        Block[] woodBlocks = this.getLevel().getCollisionBlocks(
                new SimpleAxisAlignedBB(
                        pos.getX() - distance, pos.getY() - distance, pos.getZ() - distance,
                        pos.getX() + distance, pos.getY() + distance, pos.getZ() + distance
                ), true, true,
                block -> block.getId() == WOOD || block.getId() == WOOD2
        );
        
        return woodBlocks.length > 0;
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
        return Item.get(BlockID.SAPLING, this.getDamage() & 0x03);
    }

    @Override
    public boolean diffusesSkyLight() {
        return true;
    }


    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }
}
