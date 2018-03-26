package cn.nukkit.server.entity.component;

import cn.nukkit.api.entity.component.Equippable;
import cn.nukkit.api.item.ItemInstance;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class EquippableComponent implements Equippable {
    private ItemInstance helmet;
    private ItemInstance chestplate;
    private ItemInstance leggings;
    private ItemInstance boots;

    @Nonnull
    @Override
    public Optional<ItemInstance> getHelmet() {
        return Optional.ofNullable(helmet);
    }

    @Override
    public void setHelmet(@Nullable ItemInstance helmet) {
        this.helmet = helmet;
    }

    @Nonnull
    @Override
    public Optional<ItemInstance> getChestplate() {
        return Optional.ofNullable(chestplate);
    }

    @Override
    public void setChestplate(@Nullable ItemInstance chestplate) {
        this.chestplate = chestplate;
    }

    @Nonnull
    @Override
    public Optional<ItemInstance> getLeggings() {
        return Optional.ofNullable(leggings);
    }

    @Override
    public void setLeggings(@Nullable ItemInstance leggings) {
        this.leggings = leggings;
    }

    @Nonnull
    @Override
    public Optional<ItemInstance> getBoots() {
        return Optional.ofNullable(boots);
    }

    @Override
    public void setBoots(@Nullable ItemInstance boots) {
        this.boots = boots;
    }
}
