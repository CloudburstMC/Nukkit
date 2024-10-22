package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemDye;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.MathHelper;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

public class BlockBambooSapling extends BlockFlowable {

    public BlockBambooSapling() {
        this(0);
    }

    public BlockBambooSapling(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return BAMBOO_SAPLING;
    }

    @Override
    public String getName() {
        return "Bamboo Sapling";
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (isSupportInvalid()) {
                level.useBreakOn(this, null, null, true);
            } else {
                Block up = up();
                if (up.getId() == BAMBOO) {
                    BlockBamboo upperBamboo = (BlockBamboo) up;
                    BlockBamboo newState = (BlockBamboo) Block.get(Block.BAMBOO);
                    newState.setThick(upperBamboo.isThick());
                    level.setBlock(this, newState, true, true);
                }
            }
            return type;
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            Block up;
            int time = level.getTime() % Level.TIME_FULL;
            boolean canGrow = time < 13184 || time > 22800;
            if (getAge() == 0 && (up = this.up()).getId() == AIR && canGrow/*level.getFullLight(up) >= BlockCrops.MINIMUM_LIGHT_LEVEL*/ && ThreadLocalRandom.current().nextInt(3) == 0) {
                BlockBamboo newState = (BlockBamboo) Block.get(Block.BAMBOO);
                newState.setLeafSize(BlockBamboo.LEAF_SIZE_SMALL);
                BlockGrowEvent blockGrowEvent = new BlockGrowEvent(up, newState);
                level.getServer().getPluginManager().callEvent(blockGrowEvent);
                if (!blockGrowEvent.isCancelled()) {
                    Block newState1 = blockGrowEvent.getNewState();
                    newState1.y = up.y;
                    newState1.x = x;
                    newState1.z = z;
                    newState1.level = level;
                    newState1.place(toItem(), up, this, BlockFace.DOWN, 0.5, 0.5, 0.5, null);
                }
            }
            return type;
        }
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (isSupportInvalid()) {
            return false;
        }

        if (this.getLevelBlock() instanceof BlockLiquid) {
            return false;
        }

        this.level.setBlock(this, this, true, true);
        return true;
    }


    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.getId() == ItemID.DYE && item.getDamage() == ItemDye.BONE_MEAL) {
            boolean success = false;
            Block block = this.up();
            if (block.getId() == AIR) {
                success = this.grow(block);
            }

            if (success) {
                if (player != null && (player.gamemode & 0x01) == 0) {
                    item.count--;
                }

                this.level.addParticle(new BoneMealParticle(this));
            }

            return true;
        }
        return false;
    }

    public boolean grow(Block up) {
        BlockBamboo bamboo = (BlockBamboo) Block.get(Block.BAMBOO);
        bamboo.x = x;
        bamboo.y = y;
        bamboo.z = z;
        bamboo.level = level;
        return bamboo.grow(up);
    }

    private boolean isSupportInvalid() {
        int downId = down().getId();
        return downId != DIRT && downId != GRASS && downId != SAND && downId != GRAVEL && downId != PODZOL;
    }

    public int getAge() {
        return getDamage() & 0x1;
    }

    public void setAge(int age) {
        age = MathHelper.clamp(age, 0, 1) & 0x1;
        setDamage(getDamage() & (15 ^ 0x1) | age);
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(BAMBOO), 0);
    }

    @Override
    public double getMinX() {
        return this.x + 0.125;
    }

    @Override
    public double getMinZ() {
        return this.z + 0.125;
    }

    @Override
    public double getMaxX() {
        return this.x + 0.875;
    }

    @Override
    public double getMaxY() {
        return this.y + 0.875;
    }

    @Override
    public double getMaxZ() {
        return this.z + 0.875;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }
}
