package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.event.block.BlockHarvestEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemSweetBerries;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.MathHelper;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

import javax.annotation.Nonnull;
import java.util.concurrent.ThreadLocalRandom;

@PowerNukkitOnly
public class BlockSweetBerryBush extends BlockFlowable {

    @PowerNukkitOnly
    public BlockSweetBerryBush() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockSweetBerryBush(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SWEET_BERRY_BUSH;
    }

    @Override
    public String getName() {
        return "Sweet Berry Bush";
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
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
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getHardness() {
        return getDamage() == 0? 0 : 0.25;
    }

    @Override
    public boolean onActivate(@Nonnull Item item, Player player) {

        int age = MathHelper.clamp(getDamage(), 0, 3);

        if (age < 3 && item.getId() == ItemID.DYE && item.getDamage() == DyeColor.WHITE.getDyeData()) {
            BlockSweetBerryBush block = (BlockSweetBerryBush) this.clone();
            block.setDamage(block.getDamage() + 1);
            if (block.getDamage() > 3) {
                block.setDamage(3);
            }
            BlockGrowEvent ev = new BlockGrowEvent(this, block);
            Server.getInstance().getPluginManager().callEvent(ev);

            if (ev.isCancelled()) {
                return false;
            }

            this.getLevel().setBlock(this, ev.getNewState(), false, true);
            this.level.addParticle(new BoneMealParticle(this));

            if (player != null && (player.gamemode & 0x01) == 0) {
                item.count--;
            }

            return true;
        }

        if (age < 2){
            return true;
        }

        int amount = 1 + ThreadLocalRandom.current().nextInt(2);
        if (age == 3) {
            amount++;
        }

        BlockHarvestEvent event = new BlockHarvestEvent(this,
                new BlockSweetBerryBush(1),
                new Item[]{ new ItemSweetBerries(0, amount) }
        );

        getLevel().getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            getLevel().setBlock(this, event.getNewState(), true, true);
            Item[] drops = event.getDrops();
            if (drops != null) {
                Position dropPos = add(0.5, 0.5, 0.5);
                for (Item drop : drops) {
                    if (drop != null) {
                        getLevel().dropItem(dropPos, drop);
                    }
                }
            }
        }

        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isSupportValid(down())) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (getDamage() < 3 && ThreadLocalRandom.current().nextInt(5) == 0
                    && getLevel().getFullLight(add(0, 1, 0)) >= BlockCrops.MINIMUM_LIGHT_LEVEL) {
                BlockGrowEvent event = new BlockGrowEvent(this, Block.get(getId(), getDamage() + 1));
                if (!event.isCancelled()) {
                    getLevel().setBlock(this, event.getNewState(), true, true);
                }
            }
            return type;
        }
        return 0;
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        if (target.getId() == SWEET_BERRY_BUSH || block.getId() != AIR) {
            return false;
        }
        if (isSupportValid(down())) {
            this.getLevel().setBlock(block, this, true);
            return true;
        }
        return false;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static boolean isSupportValid(Block block) {
        switch (block.getId()) {
            case GRASS:
            case DIRT:
            case PODZOL:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean hasEntityCollision() {
        return getDamage() > 0;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (getDamage() > 0) {
            if (entity.positionChanged && !entity.isSneaking() && ThreadLocalRandom.current().nextInt(20) == 0) {
                if (entity.attack(new EntityDamageByBlockEvent(this, entity, EntityDamageEvent.DamageCause.CONTACT, 1))) {
                    getLevel().addSound(entity, Sound.BLOCK_SWEET_BERRY_BUSH_HURT);
                }
            }
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox() {
        return getDamage() > 0? this : null;
    }

    @Override
    public Item[] getDrops(Item item) {
        int age = MathHelper.clamp(getDamage(), 0, 3);
        int amount = 1 + ThreadLocalRandom.current().nextInt(2);
        if (age == 3) {
            amount++;
        }

        return new Item[]{ new ItemSweetBerries(0, amount) };
    }

    @Override
    public Item toItem() {
        return new ItemSweetBerries();
    }
}
