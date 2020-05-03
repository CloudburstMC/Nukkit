package cn.nukkit.registry;

import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.biome.BiomeBuilder;
import cn.nukkit.utils.Identifier;
import com.google.common.base.Preconditions;
import com.nukkitx.nbt.tag.CompoundTag;
import com.nukkitx.nbt.tag.StringTag;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import lombok.NonNull;

import java.util.Map;
import java.util.stream.Collectors;

import static cn.nukkit.level.biome.BiomeIds.*;
import static com.google.common.base.Preconditions.*;

public class BiomeRegistry implements Registry {
    private static final BiomeRegistry INSTANCE;

    private static final Map<Identifier, Biome> VANILLA_BIOMES;

    static {
        //build initial biome map
        VANILLA_BIOMES = Biome.BIOME_DEFINITIONS.getValue().entrySet().stream().collect(Collectors.toMap(
                entry -> Identifier.fromString(entry.getKey()),
                entry -> {
                    CompoundTag tag = (CompoundTag) entry.getValue();
                    BiomeBuilder builder = BiomeBuilder.builder().setId(Identifier.fromString(entry.getKey()));
                    tag.listenForFloat("temperature", builder::setTemperature);
                    tag.listenForFloat("downfall", builder::setDownfall);
                    tag.listenForList("tags", StringTag.class, list -> builder.setTags(list.stream().map(StringTag::getValue).map(Identifier::fromString).collect(Collectors.toList())));
                    return builder.build();
                }));

        INSTANCE = new BiomeRegistry();
    }

    private final Int2ObjectMap<Biome> runtimeToBiomeMap = new Int2ObjectOpenHashMap<>();
    private final Int2ObjectMap<Identifier> runtimeToIdMap = new Int2ObjectOpenHashMap<>();
    private final Object2IntMap<Identifier> idToRuntimeMap = new Object2IntLinkedOpenHashMap<>();
    private int runtimeTypeAllocator;
    private volatile boolean closed;

    private BiomeRegistry() {
        this.registerVanillaBiomes();
    }

    public static BiomeRegistry get() {
        return INSTANCE;
    }

    public synchronized void register(@NonNull Biome biome) {
        /*Preconditions.checkState(this.runtimeTypeAllocator < 256, "Cannot register more than 256 biomes!");
        this.registerInternal(biome, biome.getId(), this.runtimeTypeAllocator++);*/
        throw new UnsupportedOperationException("Custom biomes are not currently supported!");
    }

    private void registerVanilla(@NonNull Identifier id, int runtime) {
        Biome biome = VANILLA_BIOMES.get(id);
        Preconditions.checkArgument(biome != null, "Unknown vanilla biome ID: %s", id);
        this.registerInternal(biome, runtime);

        if (runtime >= this.runtimeTypeAllocator) {
            this.runtimeTypeAllocator = runtime + 1;
        }
    }

    private synchronized void registerInternal(Biome biome, int runtime) throws RegistryException {
        this.checkClosed();
        Preconditions.checkArgument(runtime >= 0, "Runtime ID may not be negative!");
        Preconditions.checkState(!this.runtimeToIdMap.containsKey(runtime), "Runtime ID already registered: %s", runtime);
        Preconditions.checkState(!this.idToRuntimeMap.containsKey(biome.getId()), "Biome ID already registered: %s", biome.getId());

        this.runtimeToBiomeMap.put(runtime, biome);
        this.runtimeToIdMap.put(runtime, biome.getId());
        this.idToRuntimeMap.put(biome.getId(), runtime);
    }

    public int getRuntimeId(Biome biome) {
        return this.getRuntimeId(biome.getId());
    }

    public int getRuntimeId(Identifier id) {
        return this.idToRuntimeMap.getOrDefault(id, -1);
    }

    public Biome getBiome(Identifier identifier) {
        return this.getBiome(this.getRuntimeId(identifier));
    }

    public Biome getBiome(int runtimeId) {
        return this.runtimeToBiomeMap.get(runtimeId);
    }

    public Identifier getId(int runtimeId) {
        return this.runtimeToIdMap.get(runtimeId);
    }

    @Override
    public synchronized void close() throws RegistryException {
        this.checkClosed();
        this.closed = true;
    }

    private void checkClosed() {
        checkState(!this.closed, "Registration is already closed");
    }

    private void registerVanillaBiomes() {
        this.registerVanilla(OCEAN, 0);
        this.registerVanilla(PLAINS, 1);
        this.registerVanilla(DESERT, 2);
        this.registerVanilla(EXTREME_HILLS, 3);
        this.registerVanilla(FOREST, 4);
        this.registerVanilla(TAIGA, 5);
        this.registerVanilla(SWAMPLAND, 6);
        this.registerVanilla(RIVER, 7);
        this.registerVanilla(HELL, 8);
        this.registerVanilla(THE_END, 9);
        this.registerVanilla(LEGACY_FROZEN_OCEAN, 10);
        this.registerVanilla(FROZEN_RIVER, 11);
        this.registerVanilla(ICE_PLAINS, 12);
        this.registerVanilla(ICE_MOUNTAINS, 13);
        this.registerVanilla(MUSHROOM_ISLAND, 14);
        this.registerVanilla(MUSHROOM_ISLAND_SHORE, 15);
        this.registerVanilla(BEACH, 16);
        this.registerVanilla(DESERT_HILLS, 17);
        this.registerVanilla(FOREST_HILLS, 18);
        this.registerVanilla(TAIGA_HILLS, 19);
        this.registerVanilla(EXTREME_HILLS_EDGE, 20);
        this.registerVanilla(JUNGLE, 21);
        this.registerVanilla(JUNGLE_HILLS, 22);
        this.registerVanilla(JUNGLE_EDGE, 23);
        this.registerVanilla(DEEP_OCEAN, 24);
        this.registerVanilla(STONE_BEACH, 25);
        this.registerVanilla(COLD_BEACH, 26);
        this.registerVanilla(BIRCH_FOREST, 27);
        this.registerVanilla(BIRCH_FOREST_HILLS, 28);
        this.registerVanilla(ROOFED_FOREST, 29);
        this.registerVanilla(COLD_TAIGA, 30);
        this.registerVanilla(COLD_TAIGA_HILLS, 31);
        this.registerVanilla(MEGA_TAIGA, 32);
        this.registerVanilla(MEGA_TAIGA_HILLS, 33);
        this.registerVanilla(EXTREME_HILLS_PLUS_TREES, 34);
        this.registerVanilla(SAVANNA, 35);
        this.registerVanilla(SAVANNA_PLATEAU, 36);
        this.registerVanilla(MESA, 37);
        this.registerVanilla(MESA_PLATEAU_STONE, 38);
        this.registerVanilla(MESA_PLATEAU, 39);
        this.registerVanilla(WARM_OCEAN, 40);
        this.registerVanilla(DEEP_WARM_OCEAN, 41);
        this.registerVanilla(LUKEWARM_OCEAN, 42);
        this.registerVanilla(DEEP_LUKEWARM_OCEAN, 43);
        this.registerVanilla(COLD_OCEAN, 44);
        this.registerVanilla(DEEP_COLD_OCEAN, 45);
        this.registerVanilla(FROZEN_OCEAN, 46);
        this.registerVanilla(DEEP_FROZEN_OCEAN, 47);
        this.registerVanilla(BAMBOO_JUNGLE, 48);
        this.registerVanilla(BAMBOO_JUNGLE_HILLS, 49);
        this.registerVanilla(SUNFLOWER_PLAINS, 129);
        this.registerVanilla(DESERT_MUTATED, 130);
        this.registerVanilla(EXTREME_HILLS_MUTATED, 131);
        this.registerVanilla(FLOWER_FOREST, 132);
        this.registerVanilla(TAIGA_MUTATED, 133);
        this.registerVanilla(SWAMPLAND_MUTATED, 134);
        this.registerVanilla(ICE_PLAINS_SPIKES, 140);
        this.registerVanilla(JUNGLE_MUTATED, 149);
        this.registerVanilla(JUNGLE_EDGE_MUTATED, 151);
        this.registerVanilla(BIRCH_FOREST_MUTATED, 155);
        this.registerVanilla(BIRCH_FOREST_HILLS_MUTATED, 156);
        this.registerVanilla(ROOFED_FOREST_MUTATED, 157);
        this.registerVanilla(COLD_TAIGA_MUTATED, 158);
        this.registerVanilla(REDWOOD_TAIGA_MUTATED, 160);
        this.registerVanilla(REDWOOD_TAIGA_HILLS_MUTATED, 161);
        this.registerVanilla(EXTREME_HILLS_PLUS_TREES_MUTATED, 162);
        this.registerVanilla(SAVANNA_MUTATED, 163);
        this.registerVanilla(SAVANNA_PLATEAU_MUTATED, 164);
        this.registerVanilla(MESA_BRYCE, 165);
        this.registerVanilla(MESA_PLATEAU_STONE_MUTATED, 166);
        this.registerVanilla(MESA_PLATEAU_MUTATED, 167);
    }
}
