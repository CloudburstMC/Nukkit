package cn.nukkit.level.format.leveldb.updater;

import org.cloudburstmc.blockstateupdater.BlockStateUpdater;
import org.cloudburstmc.blockstateupdater.util.tagupdater.CompoundTagUpdaterContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static cn.nukkit.level.format.leveldb.LevelDBConstants.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BlockStateUpdaterVanilla implements BlockStateUpdater {

    public static final BlockStateUpdater INSTANCE = new BlockStateUpdaterVanilla();

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext ctx) {
        ctx.addUpdater(STATE_MAYOR_VERSION, STATE_MINOR_VERSION, STATE_PATCH_VERSION, true)
                .match("name", "minecraft:water")
                .visit("states")
                .tryAdd("liquid_depth", (int) 0);

        ctx.addUpdater(STATE_MAYOR_VERSION, STATE_MINOR_VERSION, STATE_PATCH_VERSION, true)
                .match("name", "minecraft:polished_blackstone_double_slab")
                .visit("states")
                .tryAdd("top_slot_bit", (byte) 0);

        this.replaceState(ctx, "minecraft:wood", "pillar_axis", "y");
        this.removeConnections(ctx, "minecraft:cobblestone_wall");
    }

    private void replaceState(CompoundTagUpdaterContext ctx, String identifier, String propertyName, Object value) {
        ctx.addUpdater(STATE_MAYOR_VERSION, STATE_MINOR_VERSION, STATE_PATCH_VERSION, true)
                .match("name", identifier)
                .visit("states")
                .edit(propertyName, helper -> helper.replaceWith(propertyName, value));
    }

    private void removeConnections(CompoundTagUpdaterContext ctx, String identifier) {
        ctx.addUpdater(STATE_MAYOR_VERSION, STATE_MINOR_VERSION, STATE_PATCH_VERSION, true)
                .match("name", identifier)
                .visit("states")
                .edit("wall_connection_type_east", helper -> helper.replaceWith("wall_connection_type_east", "none"))
                .edit("wall_connection_type_north", helper -> helper.replaceWith("wall_connection_type_north", "none"))
                .edit("wall_connection_type_south", helper -> helper.replaceWith("wall_connection_type_south", "none"))
                .edit("wall_connection_type_west", helper -> helper.replaceWith("wall_connection_type_west", "none"))
                .edit("wall_post_bit", helper -> helper.replaceWith("wall_post_bit", (byte) 0));
    }

    private void addProperty(CompoundTagUpdaterContext ctx, String identifier, String propertyName, Object value) {
        ctx.addUpdater(STATE_MAYOR_VERSION, STATE_MINOR_VERSION, STATE_PATCH_VERSION, true)
                .match("name", identifier)
                .visit("states")
                .tryAdd(propertyName, value);
    }
}