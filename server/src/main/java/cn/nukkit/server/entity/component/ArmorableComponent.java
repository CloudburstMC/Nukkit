package cn.nukkit.server.entity.component;

import cn.nukkit.api.entity.component.Armorable;
import cn.nukkit.api.item.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class ArmorableComponent implements Armorable {
    private ItemStack helmet;
    private ItemStack chestplate;
    private ItemStack leggings;
    private ItemStack boots;

    @Nonnull
    @Override
    public Optional<ItemStack> getHelmet() {
        return Optional.ofNullable(helmet);
    }

    @Override
    public void setHelmet(@Nullable ItemStack helmet) {
        this.helmet = helmet;
    }

    @Nonnull
    @Override
    public Optional<ItemStack> getChestplate() {
        return Optional.ofNullable(chestplate);
    }

    @Override
    public void setChestplate(@Nullable ItemStack chestplate) {
         this.chestplate = chestplate;
    }

    @Nonnull
    @Override
    public Optional<ItemStack> getLeggings() {
        return Optional.ofNullable(leggings);
    }

    @Override
    public void setLeggings(@Nullable ItemStack leggings) {
        this.leggings = leggings;
    }

    @Nonnull
    @Override
    public Optional<ItemStack> getBoots() {
        return Optional.ofNullable(boots);
    }

    @Override
    public void setBoots(@Nullable ItemStack boots) {
        this.boots = boots;
    }
}
