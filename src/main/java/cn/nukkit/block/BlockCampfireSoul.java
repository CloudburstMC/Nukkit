package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageByBlockEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemID;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockCampfireSoul extends BlockCampfire {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockCampfireSoul() {
        this(0);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockCampfireSoul(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return SOUL_CAMPFIRE_BLOCK;
    }

    @Override
    public String getName() {
        return "Soul Campfire";
    }

    @Override
    public int getLightLevel() {
        return isExtinguished()? 0 : 10;
    }

    @Override
    public Item toItem() {
        return Item.get(ItemID.SOUL_CAMPFIRE);
    }
    
    @Override
    public Item[] getDrops(Item item) {
        return new Item[] { new ItemBlock(Block.get(BlockID.SOUL_SOIL)) };
    }
    
    @Override
    public void onEntityCollide(Entity entity) {
        if (!isExtinguished()) {
            entity.attack(new EntityDamageByBlockEvent(this, entity, EntityDamageEvent.DamageCause.FIRE, 2));
        }
    }
}
