package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemKelp;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.MathHelper;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;

public class BlockKelp extends BlockFlowable {
    public BlockKelp() {
        this(0);
    }
    
    public BlockKelp(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return BLOCK_KELP;
    }
    
    @Override
    public String getName() {
        return "Kelp";
    }
    
    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        Block down = down();
        Block layer1Block = block.getLevelBlockAtLayer(1);
        int waterDamage;
        if ((down.getId() == BLOCK_KELP || down.isSolid()) && down.getId() != MAGMA && down.getId() != ICE && down.getId() != SOUL_SAND &&
                (layer1Block instanceof BlockWater && ((waterDamage = (block.getDamage())) == 0 || waterDamage == 8))
        ) {
            if (waterDamage == 8) {
                this.getLevel().setBlock(this, 1, new BlockWater(), true, false);
            }
            
            if (down.getId() == BLOCK_KELP && down.getDamage() != 24) {
                down.setDamage(24);
                this.getLevel().setBlock(down, down, true, true);
            }
            
            //Placing it by hand gives it a random age value between 0 and 24.
            setDamage(ThreadLocalRandom.current().nextInt(25));
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
            int waterDamage = 0;
            if (!(blockLayer1 instanceof BlockIceFrosted) &&
                    (!(blockLayer1 instanceof BlockWater) || ((waterDamage = blockLayer1.getDamage()) != 0 && waterDamage != 8))) {
                this.getLevel().useBreakOn(this);
                return type;
            }
        
            Block down = down();
            if ((!down.isSolid() && down.getId() != BLOCK_KELP) || down.getId() == MAGMA || down.getId() == ICE || down.getId() == SOUL_SAND) {
                this.getLevel().useBreakOn(this);
                return type;
            }
        
            if (waterDamage == 8) {
                this.getLevel().setBlock(this, 1, new BlockWater(), true, false);
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
    
    public boolean grow() {
        int age = MathHelper.clamp(getDamage(), 0, 25);
        if (age < 25) {
            Block up = up();
            if (up instanceof BlockWater && (up.getDamage() == 0 || up.getDamage() == 8)) {
                BlockKelp grown = new BlockKelp(age + 1);
                BlockGrowEvent ev = new BlockGrowEvent(this, grown);
                Server.getInstance().getPluginManager().callEvent(ev);
                if (!ev.isCancelled()) {
                    this.setDamage(25);
                    this.getLevel().setBlock(this, 0, this, true, true);
                    this.getLevel().setBlock(up, 1, new BlockWater(), true, false);
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
            this.getLevel().setBlock(down, new BlockKelp(ThreadLocalRandom.current().nextInt(25)), true, true);
        }
        this.getLevel().setBlock(this, new BlockAir(), true, true);
        return true;
    }
    
    @Override
    public boolean onActivate(@Nonnull Item item, Player player) {
        //Bone meal
        if (item.getId() == Item.DYE && item.getDamage() == 0x0f) {
            int x = (int) this.x;
            int z = (int) this.z;
            for (int y = (int) this.y + 1; y < 255; y++) {
                int blockIdAbove = getLevel().getBlockIdAt(x, y, z);
                if (blockIdAbove != BLOCK_KELP) {
                    if (blockIdAbove == WATER || blockIdAbove == STILL_WATER) {
                        int waterData = getLevel().getBlockDataAt(x, y, z);
                        if (waterData == 0 || waterData == 8) {
                            BlockKelp highestKelp = (BlockKelp) getLevel().getBlock(x, y - 1, z);
                            if (highestKelp.grow()) {
                                this.level.addParticle(new BoneMealParticle(this));
    
                                if (player != null && (player.gamemode & 0x01) == 0) {
                                    item.count--;
                                }
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
