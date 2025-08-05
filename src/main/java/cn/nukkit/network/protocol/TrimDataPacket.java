package cn.nukkit.network.protocol;

import cn.nukkit.item.ItemTrimPattern;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Deflater;

@ToString
public class TrimDataPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.__INTERNAL__TRIM_DATA_PACKET;

    private static final BatchPacket CACHED_PACKET;

    public static final List<TrimPattern> trimPatterns = new ArrayList<>();
    public static final List<TrimMaterial> trimMaterials = new ArrayList<>();

    static {
        for (ItemTrimPattern.Type type : ItemTrimPattern.Type.values()) {
            trimPatterns.add(new TrimPattern("minecraft:" + type.getTrimPattern() + "_armor_trim_smithing_template", type.getTrimPattern()));
        }

        trimMaterials.add(new TrimMaterial("amethyst", "§u", "minecraft:amethyst_shard"));
        trimMaterials.add(new TrimMaterial("copper", "§n", "minecraft:copper_ingot"));
        trimMaterials.add(new TrimMaterial("diamond", "§s", "minecraft:diamond"));
        trimMaterials.add(new TrimMaterial("emerald", "§q", "minecraft:emerald"));
        trimMaterials.add(new TrimMaterial("gold", "§p", "minecraft:gold_ingot"));
        trimMaterials.add(new TrimMaterial("iron", "§i", "minecraft:iron_ingot"));
        trimMaterials.add(new TrimMaterial("lapis", "§t", "minecraft:lapis_lazuli"));
        trimMaterials.add(new TrimMaterial("netherite", "§j", "minecraft:netherite_ingot"));
        trimMaterials.add(new TrimMaterial("quartz", "§h", "minecraft:quartz"));
        trimMaterials.add(new TrimMaterial("redstone", "§m", "minecraft:redstone"));

        TrimDataPacket pk = new TrimDataPacket();
        pk.tryEncode();
        CACHED_PACKET = pk.compress(Deflater.BEST_COMPRESSION);
    }

    public static BatchPacket getCachedPacket() {
        return CACHED_PACKET;
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
    }

    @Override
    public void encode() {
        this.reset();

        this.putUnsignedVarInt(trimPatterns.size());
        for (TrimPattern p : trimPatterns) {
            this.putString(p.getItemName());
            this.putString(p.getPatternId());
        }

        this.putUnsignedVarInt(trimMaterials.size());
        for (TrimMaterial m : trimMaterials) {
            this.putString(m.getMaterialId());
            this.putString(m.getColor());
            this.putString(m.getItemName());
        }
    }

    @Data
    public static class TrimPattern {
        private final String itemName;
        private final String patternId;
    }

    @Data
    public static class TrimMaterial {
        private final String materialId;
        private final String color;
        private final String itemName;
    }
}
