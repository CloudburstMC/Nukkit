package cn.nukkit.level.generator.standard.misc;

import com.google.common.base.Preconditions;
import lombok.NonNull;
import net.daporkchop.lib.common.ref.Ref;
import net.daporkchop.lib.common.ref.ThreadRef;
import net.daporkchop.lib.common.util.PArrays;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.math.primitive.BinMath;

import java.lang.ref.SoftReference;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.WeakHashMap;

/**
 * A simple pooling allocator for {@code int[]}s.
 * <p>
 * Not thread-safe!
 *
 * @author DaPorkchop_
 */
public class IntArrayAllocator {
    public static final Ref<IntArrayAllocator> DEFAULT = ThreadRef.soft(() -> new IntArrayAllocator(8));

    //keys are weak, won't inhibit operation of SoftReference
    protected final Map<int[], SoftReference<int[]>> referenceCache = new WeakHashMap<>();

    protected final Queue<SoftReference<int[]>>[] arenas;
    protected final int                           maxArenaSize;

    public IntArrayAllocator(int maxArenaSize) {
        this.maxArenaSize = PValidation.ensurePositive(maxArenaSize);
        this.arenas = PorkUtil.uncheckedCast(PArrays.filled(32, Queue[]::new, () -> new ArrayDeque(maxArenaSize)));
    }

    public int[] get(int minSize) {
        int rounded = BinMath.roundToNearestPowerOf2(PValidation.ensurePositive(minSize));
        int bits = BinMath.getNumBitsNeededFor(rounded);
        Queue<SoftReference<int[]>> arena = this.arenas[bits];

        SoftReference<int[]> ref;
        int[] arr = null;
        while ((ref = arena.poll()) != null && (arr = ref.get()) == null);

        if (arr == null)    {
            arr = new int[rounded];
            this.referenceCache.put(arr, new SoftReference<>(arr));
        }
        return arr;
    }

    public void release(@NonNull int[] arr) {
        SoftReference<int[]> ref = this.referenceCache.get(arr);
        Preconditions.checkArgument(ref != null, "array does not belong to pool!");

        int bits = BinMath.getNumBitsNeededFor(arr.length);
        Queue<SoftReference<int[]>> arena = this.arenas[bits];
        if (arena.size() < this.maxArenaSize)   {
            //return to arena
            arena.add(ref);
        } else {
            //arena is already full, simply discard array
            this.referenceCache.remove(arr);
        }
    }
}
