package cn.nukkit.entity.passive;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.Utils;

public abstract class EntityFish extends EntityWaterAnimal {

    public EntityFish(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public int getKillExperience() {
        return this.isBaby() ? 0 : Utils.rand(1, 3);
    }

    @Override
    public boolean onInteract(Player player, Item item, Vector3 clickedPos) {
        if (item.getId() == Item.BUCKET && (item.getDamage() == 0 || item.getDamage() == 8) && this.isInsideOfWater()) {
            this.close();
            if (item.getCount() <= 1) {
                player.getInventory().setItemInHand(Item.get(Item.BUCKET, this.getBucketMeta(), 1));
                return false;
            } else {
                if (!player.isCreative()) {
                    player.getInventory().decreaseCount(player.getInventory().getHeldItemIndex());
                }
                player.getInventory().addItem(Item.get(Item.BUCKET, this.getBucketMeta(), 1));
                return true;
            }
        }
        return super.onInteract(player, item, clickedPos);
    }

    protected abstract int getBucketMeta();
}
