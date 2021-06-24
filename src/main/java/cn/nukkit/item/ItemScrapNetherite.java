package cn.nukkit.item;

import cn.nukkit.api.Since;

@Since("1.4.0.0-PN")
public class ItemScrapNetherite extends Item {

    @Since("1.4.0.0-PN")
    public ItemScrapNetherite() {
        this(0, 1);
    }

    @Since("1.4.0.0-PN")
    public ItemScrapNetherite(Integer meta) {
        this(meta, 1);
    }

    @Since("1.4.0.0-PN")
    public ItemScrapNetherite(Integer meta, int count) {
        super(NETHERITE_SCRAP, 0, count, "Netherite Scrap");
    }

    @Override
    public boolean isLavaResistant() {
        return true;
    }
}
