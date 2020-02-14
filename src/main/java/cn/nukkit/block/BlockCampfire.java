package cn.nukkit.block;

import cn.nukkit.blockentity.Campfire;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.impl.EntityLiving;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemEdible;
import cn.nukkit.item.ItemIds;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.registry.BlockEntityRegistry;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

import static cn.nukkit.blockentity.BlockEntityTypes.CAMPFIRE;

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
    public float getHardness() {
        return 2.0f;
    }

    @Override
    public float getResistance() {
        return 10.0f;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getMeta() & CAMPFIRE_FACING_MASK);
    }

    public boolean isLit() {
        return (this.getMeta() & CAMPFIRE_LIT_MASK) == 0;
    }

    public void toggleFire() {
        this.setDamage(this.getMeta() ^ CAMPFIRE_LIT_MASK);
        getLevel().setBlockDataAt(this.getX(), this.getY(), this.getZ(), this.getMeta());
        getLevel().getBlockEntity(this.getPosition()).scheduleUpdate();
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        if (!block.canBeReplaced()) return false;
        this.setDamage(player.getHorizontalFacing().getOpposite().getHorizontalIndex() & CAMPFIRE_FACING_MASK);
        if (getLevel().setBlock(block.getPosition(), this, true, true)) {
            BlockEntityRegistry.get().newEntity(CAMPFIRE, this.getChunk(), this.getPosition());
            return true;
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
            if (getLevel().getServer().getCraftingManager().matchFurnaceRecipe(item) != null) {
                Campfire fire = (Campfire) getLevel().getBlockEntity(this.getPosition());
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
}
