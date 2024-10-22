package cn.nukkit.level.format.leveldb.updater;

import org.cloudburstmc.blockstateupdater.BlockStateUpdater;
import org.cloudburstmc.blockstateupdater.util.tagupdater.CompoundTagUpdaterContext;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import static cn.nukkit.level.format.leveldb.LevelDBConstants.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BlockStateUpdaterChunker implements BlockStateUpdater {

    // This is updater for worlds converted using chunker.app
    public static final BlockStateUpdater INSTANCE = new BlockStateUpdaterChunker();

    @Override
    public void registerUpdaters(CompoundTagUpdaterContext ctx) {
        this.addProperty(ctx, "minecraft:anvil", "damage", "undamaged");
        this.addProperty(ctx, "minecraft:azalea_leaves", "update_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:azalea_leaves_flowered", "update_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:bamboo_sapling", "age_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:bedrock", "infiniburn_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:big_dripleaf", "big_dripleaf_tilt", "none");
        this.addProperty(ctx, "minecraft:blackstone_double_slab", "top_slot_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:cauldron", "cauldron_liquid", "water");
        this.addProperty(ctx, "minecraft:cobbled_deepslate_double_slab", "top_slot_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:coral", "dead_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:coral_block", "dead_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:coral_fan", "coral_fan_direction", (int) 0);
        this.addProperty(ctx, "minecraft:coral_fan_dead", "coral_fan_direction", (int) 0);
        this.addProperty(ctx, "minecraft:coral_fan_hang", "dead_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:coral_fan_hang2", "dead_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:coral_fan_hang3", "dead_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:crimson_double_slab", "top_slot_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:double_wooden_slab", "top_slot_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:deepslate_brick_double_slab", "top_slot_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:deepslate_tile_double_slab", "top_slot_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:dirt", "dirt_type", "normal");
        this.addProperty(ctx, "minecraft:double_cut_copper_slab", "top_slot_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:double_wooden_slab", "wood_type", "oak");
        this.addProperty(ctx, "minecraft:double_stone_block_slab", "top_slot_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:double_stone_block_slab2", "top_slot_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:double_stone_block_slab3", "top_slot_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:double_stone_block_slab4", "top_slot_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:exposed_double_cut_copper_slab", "top_slot_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:flower_pot", "update_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:frame", "item_frame_photo_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:frame", "item_frame_map_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:kelp", "kelp_age", (int) 0);
        this.addProperty(ctx, "minecraft:lava_cauldron", "cauldron_liquid", "lava");
        this.addProperty(ctx, "minecraft:leaves", "update_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:leaves2", "update_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:oxidized_double_cut_copper_slab", "top_slot_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:polished_blackstone_brick_double_slab", "top_slot_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:polished_blackstone_double_slab", "top_slot_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:polished_deepslate_double_slab", "top_slot_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:prismarine", "prismarine_block_type", "default");
        this.addProperty(ctx, "minecraft:purpur_block", "chisel_type", "default");
        this.addProperty(ctx, "minecraft:red_sandstone", "sand_stone_type", "default");
        this.addProperty(ctx, "minecraft:sand", "sand_type", "normal");
        this.addProperty(ctx, "minecraft:sandstone", "sand_stone_type", "default");
        this.addProperty(ctx, "minecraft:sea_pickle", "dead_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:soul_fire", "age", (int) 0);
        this.addProperty(ctx, "minecraft:sponge", "sponge_type", "dry");
        this.addProperty(ctx, "minecraft:structure_void", "structure_void_type", "void");
        this.addProperty(ctx, "minecraft:tnt", "allow_underwater_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:trip_wire", "suspended_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:warped_double_slab", "top_slot_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:water", "liquid_depth", (int) 0);
        this.addProperty(ctx, "minecraft:waxed_double_cut_copper_slab", "top_slot_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:waxed_exposed_double_cut_copper_slab", "top_slot_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:waxed_oxidized_double_cut_copper_slab", "top_slot_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:waxed_weathered_double_cut_copper_slab", "top_slot_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:weathered_double_cut_copper_slab", "top_slot_bit", (byte) 0);
        this.addProperty(ctx, "minecraft:wooden_slab", "wood_type", "oak");
        this.addProperty(ctx, "minecraft:quartz_block", "chisel_type", "default");
        this.addProperty(ctx, "minecraft:quartz_block", "pillar_axis", "y");
    }

    private void addProperty(CompoundTagUpdaterContext ctx, String identifier, String propertyName, Object value) {
        ctx.addUpdater(STATE_MAYOR_VERSION, STATE_MINOR_VERSION, STATE_PATCH_VERSION, true)
                .match("name", identifier)
                .visit("states")
                .tryAdd(propertyName, value);
    }
}