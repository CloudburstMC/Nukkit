package cn.nukkit.item;

import cn.nukkit.Player;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockWater;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityBoat;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

import java.util.OptionalInt;

/**
 * @author yescallop
 * @since 2016/2/13
 */
public class ItemBoat extends Item {

    @Deprecated
    @DeprecationDetails(
            since = "1.3.2.0-PN",
            reason = "Boat items now have they own ids, and their implementation extends ItemBoat, " +
                    "so you may get 0 as meta result even though you have a boat of material different to oak wood.",
            replaceWith = "new ItemBoatOak()"
    )
    public ItemBoat() {
        this(0, 1);
    }

    @Deprecated
    @DeprecationDetails(
            since = "1.3.2.0-PN",
            reason = "Boat items now have they own ids, and their implementation extends ItemBoat, " +
                    "so you may get 0 as meta result even though you have a boat of material different to oak wood.",
            replaceWith = "An item class specific for the item you want. Eg: ItemBoatOak, ItemBoatSpruce, etc"
    )
    public ItemBoat(Integer meta) {
        this(meta, 1);
    }

    @Deprecated
    @DeprecationDetails(
            since = "1.3.2.0-PN",
            reason = "Boat items now have they own ids, and their implementation extends ItemBoat, " +
                    "so you may get 0 as meta result even though you have a boat of material different to oak wood.",
            replaceWith = "An item class specific for the item you want. Eg: ItemBoatOak, ItemBoatSpruce, etc"
    )
    public ItemBoat(Integer meta, int count) {
        super(BOAT, meta, count, "Boat");
    }

    @PowerNukkitOnly
    @Since("1.3.2.0-PN")
    protected ItemBoat(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Deprecated
    @DeprecationDetails(
            since = "1.3.2.0-PN",
            reason = "Boat items now have they own ids, and their implementation extends ItemBoat, " +
                    "so you may get 0 as meta result even though you have a boat of material different to oak wood.",
            replaceWith = "getLegacyBoatDamage()"
    )
    @Override
    public int getDamage() {
        return super.getDamage();
    }
    
    @PowerNukkitOnly
    @Since("1.3.2.0-PN")
    public OptionalInt getLegacyBoatDamage() {
        if (getId() == BOAT) {
            return OptionalInt.of(super.getDamage());
        } else {
            return OptionalInt.empty();
        }
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, BlockFace face, double fx, double fy, double fz) {
        if (face != BlockFace.UP) return false;
        EntityBoat boat = (EntityBoat) Entity.createEntity("Boat",
                level.getChunk(block.getFloorX() >> 4, block.getFloorZ() >> 4), new CompoundTag("")
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", block.getX() + 0.5))
                        .add(new DoubleTag("", block.getY() - (target instanceof BlockWater ? 0.0625 : 0)))
                        .add(new DoubleTag("", block.getZ() + 0.5)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0)))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", (float) ((player.yaw + 90f) % 360)))
                        .add(new FloatTag("", 0)))
                .putInt("Variant", getLegacyBoatDamage().orElse(0))
        );

        if (boat == null) {
            return false;
        }

        if (player.isSurvival() || player.isAdventure()) {
            Item item = player.getInventory().getItemInHand();
            item.setCount(item.getCount() - 1);
            player.getInventory().setItemInHand(item);
        }

        boat.spawnToAll();
        return true;
    }

    @Since("1.3.2.0-PN")
    @PowerNukkitOnly
    @Override
    public Item selfUpgrade() {
        if (getId() != BOAT) {
            return this;
        }
        int newId;
        switch (super.getDamage()) {
            case 0: newId = OAK_BOAT; break; 
            case 1: newId = SPRUCE_BOAT; break; 
            case 2: newId = BIRCH_BOAT; break; 
            case 3: newId = JUNGLE_BOAT; break; 
            case 4: newId = ACACIA_BOAT; break; 
            case 5: newId = DARK_OAK_BOAT; break;
            default: return this;
        }
        return Item.get(newId, 0, getCount(), getCompoundTag());
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }
}
