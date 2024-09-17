package cn.nukkit.block.custom.comparator;

import org.cloudburstmc.nbt.NbtMap;

import java.util.Comparator;

public class AlphabetPaletteComparator implements Comparator<NbtMap> {
    public static final AlphabetPaletteComparator INSTANCE = new AlphabetPaletteComparator();

    @Override
    public int compare(NbtMap o1, NbtMap o2) {
        return getIdentifier(o1).compareToIgnoreCase(getIdentifier(o2));
    }

    private String getIdentifier(NbtMap state) {
        return state.getString("name");
    }
}
