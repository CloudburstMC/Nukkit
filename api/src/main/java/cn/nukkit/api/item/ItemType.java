package cn.nukkit.api.item;

import cn.nukkit.api.item.component.ItemComponent;
import cn.nukkit.api.metadata.Metadata;
import com.google.common.base.VerifyException;

import java.util.Optional;
import java.util.Set;

public interface ItemType {
    int getId();

    String getName();

    boolean isBlock();

    Class<? extends Metadata> getMetadataClass();

    int getMaximumStackSize();

    default boolean isStackable() {
        return getMaximumStackSize() > 1;
    }

    Set<Class<? extends ItemComponent>> providedComponents();

    <C extends ItemComponent> boolean provides(Class<C> clazz);

    <C extends ItemComponent> Optional<C> get(Class<C> clazz);

    default <C extends ItemComponent> C ensureAndGet(Class<C> clazz) {
        Optional<C> component = get(clazz);
        if (!component.isPresent()) {
            throw new VerifyException("Component class " + clazz.getName() + " isn't provided by this item.");
        }
        return component.get();
    }

    default boolean hasMetadataClass() {
        return getMetadataClass() != null;
    }
}
