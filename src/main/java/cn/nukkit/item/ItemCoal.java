package cn.nukkit.item;

import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

/**
 * @author MagicDroidX (Nukkit Project)
 */
public class ItemCoal extends Item {

    public ItemCoal() {
        this(0, 1);
    }

    @Deprecated
    @DeprecationDetails(since = "1.3.2.0-PN", reason = "Charcoal now have it's own id",
        replaceWith = "ItemCoal() or ItemCharcoal()")
    public ItemCoal(Integer meta) {
        this(meta, 1);
    }

    @Deprecated
    @DeprecationDetails(since = "1.3.2.0-PN", reason = "Charcoal now have it's own id",
            replaceWith = "ItemCoal() or ItemCharcoal()")
    public ItemCoal(Integer meta, int count) {
        super(COAL, meta, count, "Coal");
        if (this.meta == 1) {
            this.name = "Charcoal";
        }
    }

    @PowerNukkitOnly
    @Since("1.3.2.0-PN")
    protected ItemCoal(int id, Integer meta, int count, String name) {
        super(id, meta, count, name);
    }

    @Deprecated
    @DeprecationDetails(
            since = "1.3.2.0-PN",
            reason = "Charcoal  now it's own ids, and its implementation extends ItemCoal, " +
                    "so you may get 0 as meta result even though you have a charcoal.",
            replaceWith = "isCharcoal()"
    )
    @Override
    public int getDamage() {
        return super.getDamage();
    }
    
    @PowerNukkitOnly
    @Since("1.3.2.0-PN")
    public boolean isCharcoal() {
        return getId() == COAL && super.getDamage() == 1; 
    }

    @Since("1.3.2.0-PN")
    @PowerNukkitOnly
    @Override
    public Item selfUpgrade() {
        if (getId() == COAL && super.getDamage() == 1) {
            return Item.get(CHARCOAL, 0, getCount(), getCompoundTag());
        }
        return this;
    }
}
