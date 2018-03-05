package cn.nukkit.api.entity.component;

import cn.nukkit.api.item.ItemInstance;

import javax.annotation.Nonnull;

public interface ContainedItem extends EntityComponent {

    ItemInstance getItemInstance();

    void setItemInstance(@Nonnull ItemInstance itemInstance);
}
