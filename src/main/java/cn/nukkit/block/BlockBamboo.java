package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.*;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.MathHelper;
import cn.nukkit.network.protocol.AnimatePacket;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

public class BlockBamboo extends BlockTransparentMeta {

    public static final int LEAF_SIZE_NONE = 0;
    public static final int LEAF_SIZE_SMALL = 1;
    public static final int LEAF_SIZE_LARGE = 2;

    public BlockBamboo() {
        this(0);
    }

    public BlockBamboo(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BAMBOO;
    }

    @Override
    public String getName() {
        return "Bamboo";
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.isSupportInvalid()) {
                this.level.scheduleUpdate(this, 0);
            }
            return type;
        } else if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            this.level.useBreakOn(this, null, null, true);
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            Block up;
            int time = level.getTime() % Level.TIME_FULL;
            boolean canGrow = time < 13184 || time > 22800;
            if (this.getAge() == 0 && (up = this.up()).getId() == AIR && canGrow/*this.level.getFullLight(up) >= BlockCrops.MINIMUM_LIGHT_LEVEL*/ && ThreadLocalRandom.current().nextInt(3) == 0) {
                this.grow(up);
            }
            return type;
        }
        return 0;
    }

    public boolean grow(Block up) {
        BlockBamboo newState = (BlockBamboo) Block.get(Block.BAMBOO);
        if (this.isThick()) {
            newState.setThick(true);
            newState.setLeafSize(LEAF_SIZE_LARGE);
        } else {
            newState.setLeafSize(LEAF_SIZE_SMALL);
        }
        BlockGrowEvent blockGrowEvent = new BlockGrowEvent(up, newState);
        level.getServer().getPluginManager().callEvent(blockGrowEvent);
        if (!blockGrowEvent.isCancelled()) {
            Block newState1 = blockGrowEvent.getNewState();
            newState1.x = x;
            newState1.y = up.y;
            newState1.z = z;
            newState1.level = level;
            newState1.place(this.toItem(), up, this, BlockFace.DOWN, 0.5, 0.5, 0.5, null);
            return true;
        }
        return false;
    }

    private int countHeight() {
        int count = 0;
        Block opt;
        Block down = this;
        while ((opt = down.down()).getId() == BAMBOO) {
            down = opt;
            if (++count >= 16) {
                break;
            }
        }
        return count;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        Block down = this.down();
        int downId = down.getId();
        if (downId != BAMBOO && downId != BAMBOO_SAPLING) {
            BlockBambooSapling sampling = (BlockBambooSapling) Block.get(Block.BAMBOO_SAPLING);
            sampling.x = x;
            sampling.y = y;
            sampling.z = z;
            sampling.level = level;
            return sampling.place(item, block, target, face, fx, fy, fz, player);
        }

        boolean canGrow = true;

        if (downId == BAMBOO_SAPLING) {
            if (player != null) {
                AnimatePacket animatePacket = new AnimatePacket();
                animatePacket.action = AnimatePacket.Action.SWING_ARM;
                animatePacket.eid = player.getId();
                this.getLevel().addChunkPacket(player.getChunkX(), player.getChunkZ(), animatePacket);
            }
            this.setLeafSize(LEAF_SIZE_SMALL);
        } if (down instanceof BlockBamboo) {
            BlockBamboo bambooDown = (BlockBamboo) down;
            canGrow = bambooDown.getAge() == 0;
            boolean thick = bambooDown.isThick();
            if (!thick) {
                boolean setThick = true;
                for (int i = 2; i <= 3; i++) {
                    if (this.getSide(BlockFace.DOWN, i).getId() != BAMBOO) {
                        setThick = false;
                    }
                }
                if (setThick) {
                    this.setThick(true);
                    this.setLeafSize(LEAF_SIZE_LARGE);
                    bambooDown.setLeafSize(LEAF_SIZE_SMALL);
                    bambooDown.setThick(true);
                    bambooDown.setAge(1);
                    this.level.setBlock(bambooDown, bambooDown, false, true);
                    while ((down = down.down()) instanceof BlockBamboo) {
                        bambooDown = (BlockBamboo) down;
                        bambooDown.setThick(true);
                        bambooDown.setLeafSize(LEAF_SIZE_NONE);
                        bambooDown.setAge(1);
                        this.level.setBlock(bambooDown, bambooDown, false, true);
                    }
                } else {
                    this.setLeafSize(LEAF_SIZE_SMALL);
                    bambooDown.setAge(1);
                    this.level.setBlock(bambooDown, bambooDown, false, true);
                }
            } else {
                this.setThick(true);
                this.setLeafSize(LEAF_SIZE_LARGE);
                this.setAge(0);
                bambooDown.setLeafSize(LEAF_SIZE_LARGE);
                bambooDown.setAge(1);
                this.level.setBlock(bambooDown, bambooDown, false, true);
                down = bambooDown.down();
                if (down instanceof BlockBamboo) {
                    bambooDown = (BlockBamboo) down;
                    bambooDown.setLeafSize(LEAF_SIZE_SMALL);
                    bambooDown.setAge(1);
                    this.level.setBlock(bambooDown, bambooDown, false, true);
                    down = bambooDown.down();
                    if (down instanceof BlockBamboo) {
                        bambooDown = (BlockBamboo) down;
                        bambooDown.setLeafSize(LEAF_SIZE_NONE);
                        bambooDown.setAge(1);
                        this.level.setBlock(bambooDown, bambooDown, false, true);
                    }
                }
            }
        } else if (this.isSupportInvalid()) {
            return false;
        }

        int height = canGrow? this.countHeight() : 0;
        if (!canGrow || height >= 15 || height >= 11 && ThreadLocalRandom.current().nextFloat() < 0.25F) {
            this.setAge(1);
        }

        this.level.setBlock(this, this, false, true);
        return true;
    }

    @Override
    public boolean onBreak(Item item) {
        Block down = this.down();
        if (down instanceof BlockBamboo) {
            BlockBamboo bambooDown = (BlockBamboo) down;
            int height = bambooDown.countHeight();
            if (height < 15 && (height < 11 || !(ThreadLocalRandom.current().nextFloat() < 0.25F))) {
                bambooDown.setAge(0);
                this.level.setBlock(bambooDown, bambooDown, false, true);
            }
        }
        return super.onBreak(item);
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    private boolean isSupportInvalid() {
        int downId = this.down().getId();
        return downId != BAMBOO && downId != DIRT && downId != GRASS && downId != SAND && downId != GRAVEL && downId != PODZOL && downId != BAMBOO_SAPLING;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }

    @Override
    public double getHardness() {
        return 1;
    }

    @Override
    public double getResistance() {
        return 2;
    }

    public boolean isThick() {
        return (this.getDamage() & 0x1) == 0x1;
    }

    public void setThick(boolean thick) {
        this.setDamage(this.getDamage() & (15 ^ 0x1) | (thick? 0x1 : 0x0));
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    public int getLeafSize() {
        return (this.getDamage() >> 1) & 0x3;
    }

    public void setLeafSize(int leafSize) {
        leafSize = MathHelper.clamp(leafSize, LEAF_SIZE_NONE, LEAF_SIZE_LARGE) & 0b11;
        this.setDamage(this.getDamage() & (15 ^ 0b110) | (leafSize << 1));
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.getId() == ItemID.DYE && item.getDamage() == ItemDye.BONE_MEAL) {
            int top = (int) y;
            int count = 1;

            for (int i = 1; i <= 16; i++) {
                int id = this.level.getBlockIdAt(this.getFloorX(), this.getFloorY() - i, this.getFloorZ());
                if (id == BAMBOO) {
                    count++;
                } else {
                    break;
                }
            }

            for (int i = 1; i <= 16; i++) {
                int id = this.level.getBlockIdAt(this.getFloorX(), this.getFloorY() + i, this.getFloorZ());
                if (id == BAMBOO) {
                    top++;
                    count++;
                } else {
                    break;
                }
            }

            if (count >= 15) {
                return false;
            }

            boolean success = false;

            Block block = this.up(top - (int)y + 1);
            if (block.getId() == BlockID.AIR) {
                success = this.grow(block);
            }

            if (success) {
                if (player != null && !player.isCreative()) {
                    item.count--;
                }
                level.addParticle(new BoneMealParticle(this));
            }

            return true;
        }
        return false;
    }

    public int getAge() {
        return (this.getDamage() & 0x8) >> 3;
    }

    public void setAge(int age) {
        age = MathHelper.clamp(age, 0, 1) << 3;
        this.setDamage(this.getDamage() & (15 ^ 0b1000) | age);
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public boolean breakWhenPushed() {
        return true;
    }
}
