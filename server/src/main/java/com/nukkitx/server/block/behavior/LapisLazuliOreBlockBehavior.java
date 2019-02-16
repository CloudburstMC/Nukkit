package com.nukkitx.server.block.behavior;

import com.google.common.collect.ImmutableList;
import com.nukkitx.api.Player;
import com.nukkitx.api.block.Block;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.item.ItemTypes;
import com.nukkitx.api.item.TierTypes;
import com.nukkitx.api.item.ToolTypes;
import com.nukkitx.api.metadata.Dyed;
import com.nukkitx.api.metadata.data.DyeColor;
import com.nukkitx.server.item.NukkitItemInstanceBuilder;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.concurrent.ThreadLocalRandom;

public class LapisLazuliOreBlockBehavior extends SimpleBlockBehavior {
    public static final LapisLazuliOreBlockBehavior INSTANCE = new LapisLazuliOreBlockBehavior();
    private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
    private static final ItemInstance LAPIS = new NukkitItemInstanceBuilder().itemType(ItemTypes.DYE)
            .itemData(Dyed.of(DyeColor.BLUE)).amount(4 + RANDOM.nextInt(5)).build();

    @Override
    public Collection<ItemInstance> getDrops(Player player, Block block, @Nullable ItemInstance item) {
        if(isCorrectTool(item)) {
            return ImmutableList.of(LAPIS);
        }
        return ImmutableList.of();
    }

    @Override
    public boolean isCorrectTool(@Nullable ItemInstance item) {
        return item != null && item.getItemType().getToolType().isPresent() &&
                item.getItemType().getToolType().get() == ToolTypes.PICKAXE &&
                item.getItemType().getTierType().isPresent() &&
                item.getItemType().getTierType().get() != TierTypes.WOOD;
    }
}
