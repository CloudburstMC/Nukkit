package cn.nukkit.api.entity.component;

import cn.nukkit.api.item.ItemInstance;

import javax.annotation.Nonnull;

public interface ContainedItem extends EntityComponent {

    ItemInstance getItem();

    void setItem(@Nonnull ItemInstance item);
}
