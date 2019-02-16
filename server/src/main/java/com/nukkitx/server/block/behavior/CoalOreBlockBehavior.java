package com.nukkitx.server.block.behavior;

import com.google.common.collect.ImmutableList;
import com.nukkitx.api.Player;
import com.nukkitx.api.block.Block;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.item.ItemTypes;
import com.nukkitx.api.item.ToolTypes;
import com.nukkitx.server.item.NukkitItemInstanceBuilder;

import javax.annotation.Nullable;
import java.util.Collection;

public class CoalOreBlockBehavior extends SimpleBlockBehavior {
    public static final CoalOreBlockBehavior INSTANCE = new CoalOreBlockBehavior();
    private static final ItemInstance COAL = new NukkitItemInstanceBuilder().itemType(ItemTypes.COAL).amount(1).build();

    @Override
    public Collection<ItemInstance> getDrops(Player player, Block block, @Nullable ItemInstance item) {
        return ImmutableList.of(COAL);
    }

    @Override
    public boolean isCorrectTool(@Nullable ItemInstance item) {
        return item != null && item.getItemType().getToolType().isPresent() &&
                item.getItemType().getToolType().get() == ToolTypes.PICKAXE;
    }
}
