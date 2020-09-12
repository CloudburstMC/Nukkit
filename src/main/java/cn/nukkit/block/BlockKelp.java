package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.IntBlockProperty;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemKelp;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;

@PowerNukkitOnly
public class BlockKelp extends BlockFlowable {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final IntBlockProperty KELP_AGE = new IntBlockProperty("kelp_age", false, 25);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(KELP_AGE);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockKelp() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockKelp(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return BLOCK_KELP;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Kelp";
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public int getAge() {
        return getIntValue(KELP_AGE);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setAge(int age) {
        setIntValue(KELP_AGE, age);
    }
    
    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        Block down = down();
        Block layer1Block = block.getLevelBlockAtLayer(1);
        if ((down.getId() == BLOCK_KELP || down.isSolid()) && down.getId() != MAGMA && down.getId() != ICE && down.getId() != SOUL_SAND &&
                (layer1Block instanceof BlockWater && ((BlockWater) layer1Block).isSourceOrFlowingDown())
        ) {
            if (((BlockWater) layer1Block).isFlowingDown()) {
                this.getLevel().setBlock(this, 1, get(WATER), true, false);
            }

            int maxAge = KELP_AGE.getMaxValue();
            if (down.getId() == BLOCK_KELP && down.getIntValue(KELP_AGE) != maxAge - 1) {
                down.setIntValue(KELP_AGE, maxAge - 1);
                this.getLevel().setBlock(down, down, true, true);
            }

            //Placing it by hand gives it a random age value between 0 and 24.
            setAge(ThreadLocalRandom.current().nextInt(maxAge));
            this.getLevel().setBlock(this, this, true, true);
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block blockLayer1 = getLevelBlockAtLayer(1);
            if (!(blockLayer1 instanceof BlockIceFrosted) &&
                    (!(blockLayer1 instanceof BlockWater) || !((BlockWater)blockLayer1).isSourceOrFlowingDown())) {
                this.getLevel().useBreakOn(this);
                return type;
            }
        
            Block down = down();
            if ((!down.isSolid() && down.getId() != BLOCK_KELP) || down.getId() == MAGMA || down.getId() == ICE || down.getId() == SOUL_SAND) {
                this.getLevel().useBreakOn(this);
                return type;
            }
        
            if (blockLayer1 instanceof BlockWater && ((BlockWater)blockLayer1).isFlowingDown()) {
                this.getLevel().setBlock(this, 1, get(WATER), true, false);
            }
            return type;
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (ThreadLocalRandom.current().nextInt(100) <= 14) {
                grow();
            }
            return type;
        }
        return super.onUpdate(type);
    }
    
    @PowerNukkitOnly
    public boolean grow() {
        int age = getAge();
        int maxValue = KELP_AGE.getMaxValue();
        if (age < maxValue) {
            Block up = up();
            if (up instanceof BlockWater && ((BlockWater)up).isSourceOrFlowingDown()) {
                Block grown = BlockState.of(BLOCK_KELP, age + 1).getBlock();
                BlockGrowEvent ev = new BlockGrowEvent(this, grown);
                Server.getInstance().getPluginManager().callEvent(ev);
                if (!ev.isCancelled()) {
                    this.setAge(maxValue);
                    this.getLevel().setBlock(this, 0, this, true, true);
                    this.getLevel().setBlock(up, 1, get(WATER), true, false);
                    this.getLevel().setBlock(up, 0, ev.getNewState(), true, true);
                    return true;
                }
            }
        }
        return false;
    }
    
    @Override
    public boolean onBreak(Item item) {
        Block down = down();
        if (down.getId() == BLOCK_KELP) {
            this.getLevel().setBlock(down, BlockState.of(BLOCK_KELP, ThreadLocalRandom.current().nextInt(KELP_AGE.getMaxValue())).getBlock(), true, true);
        }
        this.getLevel().setBlock(this, get(AIR), true, true);
        return true;
    }
    
    @Override
    public boolean onActivate(@Nonnull Item item, Player player) {
        //Bone meal
        if (item.getId() == Item.DYE && item.getDamage() == 0x0f) {
            int x = (int) this.x;
            int z = (int) this.z;
            for (int y = (int) this.y + 1; y < 255; y++) {
                BlockState blockStateAbove = getLevel().getBlockStateAt(x, y, z);
                int blockIdAbove = blockStateAbove.getBlockId();
                if (blockIdAbove != BLOCK_KELP) {
                    if (blockIdAbove == WATER || blockIdAbove == STILL_WATER) {
                        if (((BlockWater)blockStateAbove.getBlock()).isSourceOrFlowingDown()) {
                            BlockKelp highestKelp = (BlockKelp) getLevel().getBlock(x, y - 1, z);
                            if (highestKelp.grow()) {
                                this.level.addParticle(new BoneMealParticle(this));
    
                                if (player != null && (player.gamemode & 0x01) == 0) {
                                    item.count--;
                                }
                                
                                return true;
                            }
                        }
                    }
                    return false;
                }
            }
            
            return true;
        }
        
        return false;
    }
    
    @Override
    public Item toItem() {
        return new ItemKelp();
    }
    
    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 2;
    }
    
    @Override
    public boolean canBeActivated() {
        return true;
    }
}
