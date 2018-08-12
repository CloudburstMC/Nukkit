package com.nukkitx.server.math;

import com.flowpowered.math.GenericMath;
import com.nukkitx.api.metadata.data.SimpleDirection;
import lombok.experimental.UtilityClass;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * This class was made due to smartly used direction sequences in items/blocks metadata
 *
 * @author CreeperFace
 */
@UtilityClass
public class DirectionHelper {

    private static final Map<SeqType, List<SimpleDirection>> objTranslators = new EnumMap<>(SeqType.class);
    private static final Map<SeqType, Map<SimpleDirection, Short>> metaTranslators = new EnumMap<>(SeqType.class);

    static {
        register(SeqType.TYPE_1, SimpleDirection.NORTH, SimpleDirection.SOUTH, SimpleDirection.WEST, SimpleDirection.EAST);
        register(SeqType.TYPE_2, SimpleDirection.SOUTH, SimpleDirection.WEST, SimpleDirection.NORTH, SimpleDirection.EAST);
        register(SeqType.TYPE_2_REVERSED, SimpleDirection.NORTH, SimpleDirection.EAST, SimpleDirection.SOUTH, SimpleDirection.WEST);
        register(SeqType.TYPE_3, SimpleDirection.EAST, SimpleDirection.SOUTH, SimpleDirection.WEST, SimpleDirection.NORTH);
        register(SeqType.TYPE_4, SimpleDirection.EAST, SimpleDirection.WEST, SimpleDirection.SOUTH, SimpleDirection.NORTH);
    }

    private static void register(SeqType type, SimpleDirection... seq) {
        EnumMap<SimpleDirection, Short> map = new EnumMap<>(SimpleDirection.class);

        for (short i = 0; i < seq.length; i++) {
            map.put(seq[i], i);
        }

        objTranslators.put(type, new ArrayList<>(Arrays.asList(seq)));
        metaTranslators.put(type, map);
    }

    public static SimpleDirection fromMeta(int meta, SeqType type) {
        List<SimpleDirection> list = objTranslators.get(type);

        meta = GenericMath.clamp(meta, 0, list.size());

        return list.get(meta);
    }

    public static short toMeta(@Nonnull SimpleDirection direction, SeqType type) {
        return metaTranslators.get(type).get(direction);
    }

    public enum SeqType {
        TYPE_1,
        TYPE_2,
        TYPE_2_REVERSED,
        TYPE_3,
        TYPE_4
    }
}
