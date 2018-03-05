package cn.nukkit.api.item;

import cn.nukkit.api.enchantment.EnchantmentInstance;
import cn.nukkit.api.metadata.Metadata;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Nonnull
@Immutable
public interface ItemInstance {

    ItemType getItemType();

    int getAmount();

    Optional<Metadata> getItemData();

    Optional<String> getName();

    List<String> getLore();

    Collection<EnchantmentInstance> getEnchantments();

    ItemInstanceBuilder toBuilder();

    boolean isSimilar(@Nonnull ItemInstance itemInstance);

    boolean isMergeable(@Nonnull ItemInstance itemInstance);

    default boolean isFull() {
        return getAmount() >= getItemType().getMaximumStackSize();
    }
}
