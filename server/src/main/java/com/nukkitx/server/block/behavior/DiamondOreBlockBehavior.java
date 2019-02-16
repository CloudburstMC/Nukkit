package com.nukkitx.server.block.behavior;

import com.google.common.collect.ImmutableList;
import com.nukkitx.api.Player;
import com.nukkitx.api.block.Block;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.item.ItemTypes;
import com.nukkitx.api.item.TierTypes;
import com.nukkitx.api.item.ToolTypes;
import com.nukkitx.server.item.NukkitItemInstanceBuilder;

import javax.annotation.Nullable;
import java.util.Collection;

public class DiamondOreBlockBehavior extends SimpleBlockBehavior {
    public static final DiamondOreBlockBehavior INSTANCE = new DiamondOreBlockBehavior();
    private static final ItemInstance DIAMOND = new NukkitItemInstanceBuilder().itemType(ItemTypes.DIAMOND).amount(1).build();

    @Override
    public Collection<ItemInstance> getDrops(Player player, Block block, @Nullable ItemInstance item) {
        if(isCorrectTool(item)) {
            return ImmutableList.of(DIAMOND);
        }
        return ImmutableList.of();
    }

    @Override
    public boolean isCorrectTool(@Nullable ItemInstance item) {
        return item != null && item.getItemType().getToolType().isPresent() &&
                item.getItemType().getToolType().get() == ToolTypes.PICKAXE &&
                item.getItemType().getTierType().isPresent() &&
                item.getItemType().getTierType().get().getLevel() >= TierTypes.IRON.getLevel();
    }
}
