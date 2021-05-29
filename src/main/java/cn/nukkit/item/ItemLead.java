package cn.nukkit.item;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;

@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class ItemLead extends Item {

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ItemLead() {
        this(0, 1);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ItemLead(Integer meta) {
        this(meta, 1);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public ItemLead(Integer meta, int count) {
        super(LEAD, meta, count, "Lead");
    }
    
    // TODO: Add Functionality
}
