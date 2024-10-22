package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.event.block.BlockHarvestEvent;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.MathHelper;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

import java.util.concurrent.ThreadLocalRandom;

public class BlockSweetBerryBush extends BlockFlowable {

    public BlockSweetBerryBush() {
        this(0);
    }

    public BlockSweetBerryBush(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Sweet Berry Bush";
    }

    @Override
    public int getId() {
        return SWEET_BERRY_BUSH;
    }

    @Override
    public boolean canBeActivated() {
        return true;
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
    public Item toItem() {
        return Item.get(ItemID.SWEET_BERRIES);
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (this.getDamage() > 0 && !(entity instanceof EntityItem)) {
            entity.resetFallDistance();
            if (!entity.isSneaking() && ThreadLocalRandom.current().nextInt(20) == 0) {
                if (entity.attack(new EntityDamageByBlockEvent(this, entity, EntityDamageEvent.DamageCause.CONTACT, 1))) {
                    this.level.addLevelSoundEvent(entity, LevelSoundEventPacket.SOUND_BLOCK_SWEET_BERRY_BUSH_HURT);
                }
            }
        }
    }

    @Override
    public boolean hasEntityCollision() {
        return this.getDamage() > 0;
    }

    protected AxisAlignedBB recalculateBoundingBox() {
        if (this.getDamage() > 0) {
            return this;
        }
        return null;
    }

    @Override
    public Item[] getDrops(Item item) {
        int age = MathHelper.clamp(getDamage(), 0, 3);

        int amount = 1;
        if (age > 1) {
            amount = 1 + ThreadLocalRandom.current().nextInt(2);
            if (age == 3) {
                amount++;
            }
        }

        return new Item[]{Item.get(ItemID.SWEET_BERRIES, 0, amount)};
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        int age = MathHelper.clamp(this.getDamage(), 0, 3);

        if (age < 3 && item.getId() == ItemID.DYE && item.getDamage() == DyeColor.WHITE.getDyeData()) {
            BlockSweetBerryBush block = (BlockSweetBerryBush) this.clone();
            block.setDamage(block.getDamage() + 1);
            if (block.getDamage() > 3) {
                block.setDamage(3);
            }

            BlockGrowEvent ev = new BlockGrowEvent(this, block);
            this.getLevel().getServer().getPluginManager().callEvent(ev);
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

        if (age < 2) {
            return true;
        }

        int amount = 1 + ThreadLocalRandom.current().nextInt(2);
        if (age == 3) {
            amount++;
        }

        BlockHarvestEvent event = new BlockHarvestEvent(this, Block.get(SWEET_BERRY_BUSH, 1), new Item[]{Item.get(ItemID.SWEET_BERRIES, 0, amount)});
        this.getLevel().getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            this.getLevel().setBlock(this, event.getNewState(), true, true);
            Item[] drops = event.getDrops();
            if (drops != null) {
                Position dropPos = add(0.5, 0.5, 0.5);
                for (Item drop : drops) {
                    if (drop != null) {
                        this.getLevel().dropItem(dropPos, drop);
                    }
                }
            }
            this.level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_BLOCK_SWEET_BERRY_BUSH_PICK);
        }
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isSupportValid(this.down())) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (this.getDamage() < 3 && ThreadLocalRandom.current().nextInt(5) == 0) {
                BlockGrowEvent event = new BlockGrowEvent(this, Block.get(this.getId(), this.getDamage() + 1));
                if (!event.isCancelled()) {
                    this.getLevel().setBlock(this, event.getNewState(), true, true);
                }
            }
            return type;
        }
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (target.getId() == SWEET_BERRY_BUSH || block.getId() != AIR) {
            return false;
        }
        if (isSupportValid(this.down())) {
            this.getLevel().setBlock(block, this, true);
            return true;
        }
        return false;
    }


    public static boolean isSupportValid(Block block) {
        switch (block.getId()) {
            case GRASS:
            case DIRT:
            case PODZOL:
            case MYCELIUM:
            case FARMLAND:
                return true;
            default:
                return false;
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }
}
