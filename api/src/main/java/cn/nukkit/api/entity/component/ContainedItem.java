package cn.nukkit.api.entity.component;

import cn.nukkit.api.item.ItemInstance;

public interface ContainedItem extends EntityComponent {

    ItemInstance getItemInstance();
}
