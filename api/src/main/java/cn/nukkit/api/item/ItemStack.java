package cn.nukkit.api.item;

import cn.nukkit.api.metadata.Metadata;

import javax.annotation.Nonnull;
import java.util.Optional;

@Nonnull
public interface ItemStack {

    ItemType getItemType();

    int getAmount();

    Optional<Metadata> getItemData();

    Optional<String> getName();

    ItemStackBuilder toBuilder();

    int getDurability();

    void setDurability(int durability);


}
