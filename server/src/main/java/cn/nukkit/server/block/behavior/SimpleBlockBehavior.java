package cn.nukkit.server.block.behavior;

import cn.nukkit.api.Player;
import cn.nukkit.api.block.Block;
import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.api.item.ItemInstanceBuilder;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;
import java.util.Collection;

public class SimpleBlockBehavior implements BlockBehavior {
    public static final SimpleBlockBehavior INSTANCE = new SimpleBlockBehavior();

    @Override
    public Collection<ItemInstance> getDrops(Player player, Block block, @Nullable ItemInstance item) {
        if (!isToolCompatible(block, item)) {
            return ImmutableList.of();
        }
        // TODO: Fortune drops
        ItemInstanceBuilder builder = player.getServer().itemInstanceBuilder()
                .itemType(block.getBlockState().getBlockType())
                .amount(1);
        if (block.getBlockState().getBlockData() != null) {
            builder.itemData(block.getBlockState().getBlockData());
        }

        return ImmutableList.of(builder.build());
    }

    @Override
    public float getBreakTime(Player player, Block block, @Nullable ItemInstance item) {
        float breakTime = block.getBlockState().getBlockType().hardness();
        breakTime *= isToolCompatible(block, item) ? 1.5 : 5;

        float efficiency = getMiningEfficiency(block, item);
        if (efficiency <= 0) {
            throw new IllegalStateException("Invalid item efficiency was given");
        }
        breakTime /= efficiency;

        return breakTime;
    }

    @Override
    public boolean isToolCompatible(Block block, @Nullable ItemInstance item) {
        return block.getBlockState().getBlockType().isDiggable();
    }
}
