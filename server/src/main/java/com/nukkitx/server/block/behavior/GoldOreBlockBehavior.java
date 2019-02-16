package com.nukkitx.server.block.behavior;

import com.google.common.collect.ImmutableList;
import com.nukkitx.api.Player;
import com.nukkitx.api.block.Block;
import com.nukkitx.api.block.BlockTypes;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.item.TierTypes;
import com.nukkitx.api.item.ToolTypes;
import com.nukkitx.server.item.NukkitItemInstanceBuilder;

import javax.annotation.Nullable;
import java.util.Collection;

public class GoldOreBlockBehavior extends SimpleBlockBehavior {
    public static final GoldOreBlockBehavior INSTANCE = new GoldOreBlockBehavior();
    private static final ItemInstance GOLD_ORE = new NukkitItemInstanceBuilder().itemType(BlockTypes.GOLD_ORE).amount(1).build();

    @Override
    public Collection<ItemInstance> getDrops(Player player, Block block, @Nullable ItemInstance item) {
        return ImmutableList.of(GOLD_ORE);
    }

    @Override
    public boolean isCorrectTool(@Nullable ItemInstance item) {
        return item != null && item.getItemType().getToolType().isPresent() &&
                item.getItemType().getToolType().get() == ToolTypes.PICKAXE &&
                item.getItemType().getTierType().isPresent() &&
                item.getItemType().getTierType().get().getLevel() >= TierTypes.IRON.getLevel();
    }
}
