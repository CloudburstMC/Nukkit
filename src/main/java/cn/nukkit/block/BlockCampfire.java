package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityCampfire;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.impl.EntityLiving;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemEdible;
import cn.nukkit.item.ItemIds;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;

/**
 * @author Sleepybear
 */
public class BlockCampfire extends BlockSolid implements Faceable {

    private static final int CAMPFIRE_LIT_MASK = 0x04; // Bit is 1 when fire is extinguished
    private static final int CAMPFIRE_FACING_MASK = 0x03;

    public BlockCampfire(Identifier id) {
        super(id);
    }

    @Override
    public double getHardness() {
        return 2.0;
    }

    @Override
    public double getResistance() {
        return 10.0;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & CAMPFIRE_FACING_MASK);
    }

    public boolean isLit() {
        return (this.getDamage() & CAMPFIRE_LIT_MASK) == 0;
    }

    public void toggleFire() {
        if (!this.isLit() && isWaterlogged()) return;
        this.setDamage(this.getDamage() ^ CAMPFIRE_LIT_MASK);
        getLevel().setBlockDataAt(this.x, this.y, this.z, this.getDamage());
        BlockEntityCampfire cf = (BlockEntityCampfire) getLevel().getBlockEntity(this);
        if (cf != null) getLevel().getBlockEntity(this).scheduleUpdate();
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        if (!block.canBeReplaced()) return false;
        if (block.down().getId() == BlockIds.CAMPFIRE) {
            return false;
        }
        this.setDamage(player.getHorizontalFacing().getOpposite().getHorizontalIndex() & CAMPFIRE_FACING_MASK);
        if ((block.getId() == BlockIds.WATER || block.getId() == BlockIds.FLOWING_WATER)
                && block.getDamage() == 0) {
            this.setDamage(this.getDamage() + CAMPFIRE_LIT_MASK);
            getLevel().setBlock(block.getX(), block.getY(), block.getZ(), 1, block.clone(), true, false);
        }

        if (getLevel().setBlock(block, this, true, true)) {
            CompoundTag tag = new CompoundTag()
                    .putString("id", BlockEntity.CAMPFIRE)
                    .putInt("x", block.x)
                    .putInt("y", block.y)
                    .putInt("z", block.z);
            BlockEntity campfire = BlockEntity.createBlockEntity(BlockEntity.CAMPFIRE, this.getChunk(), tag);
            return campfire != null;
        }
        return false;
    }

    @Override
    public int getLightLevel() {
        return isLit() ? 15 : 0;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public boolean canWaterlogSource() {
        return true;
    }

    @Override
    public boolean canBeFlooded() {
        return false;
    }

    @Override
    public boolean onActivate(Item item) {
        return this.onActivate(item, null);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.getEnchantment(Enchantment.ID_SILK_TOUCH) != null) {
            return super.getDrops(item);
        } else {
            return new Item[0];
        }
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.getId() == ItemIds.FLINT_AND_STEEL
                || item.getEnchantment(Enchantment.ID_FIRE_ASPECT) != null) {
            if (!(this.isLit())) {
                this.toggleFire();
            }
            return true;
        } else if (item.isShovel()) {
            if (this.isLit()) {
                this.toggleFire();
            }
            return true;
        } else if (item instanceof ItemEdible) {
            BlockEntityCampfire fire = (BlockEntityCampfire) getLevel().getBlockEntity(this);
            if (fire.putItemInFire(item)) {
                if (player != null && player.isSurvival()) {
                    item.decrementCount();
                    if (item.getCount() <= 0) {
                        item = Item.get(BlockIds.AIR);
                    }
                    player.getInventory().setItemInHand(item);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (this.isLit() && entity instanceof EntityLiving) {
            entity.attack(new EntityDamageByBlockEvent(this, entity, EntityDamageEvent.DamageCause.FIRE_TICK, 1.0f));
        } else if (!this.isLit() && entity.isOnFire()) {
            this.toggleFire();
        }
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.isLit()) {
                if (this.isWaterlogged()) {
                    this.toggleFire();
                    return type;
                }

                Block up = this.up();
                if (up.getId() == BlockIds.WATER || up.getId() == BlockIds.FLOWING_WATER) {
                    this.toggleFire();
                    return type;
                }
            }
            return type;
        }

        return 0;
    }
}
