package cn.nukkit.level.generator.standard.misc;

import com.google.common.base.Preconditions;
import lombok.NonNull;
import net.daporkchop.lib.common.ref.Ref;
import net.daporkchop.lib.common.ref.ThreadRef;
import net.daporkchop.lib.common.util.PArrays;
import net.daporkchop.lib.common.util.PValidation;
import net.daporkchop.lib.common.util.PorkUtil;
import net.daporkchop.lib.math.primitive.BinMath;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * A simple pooling allocator for {@code int[]}s.
 * <p>
 * Not thread-safe!
 *
 * @author DaPorkchop_
 */
public class IntArrayAllocator {
    public static final Ref<IntArrayAllocator> DEFAULT = ThreadRef.soft(() -> new IntArrayAllocator(8));

    protected final Deque<int[]>[] arenas;
    protected final int maxArenaSize;

    public IntArrayAllocator(int maxArenaSize) {
        this.maxArenaSize = PValidation.ensurePositive(maxArenaSize);
        this.arenas = PorkUtil.uncheckedCast(PArrays.filled(32, Deque[]::new, () -> new ArrayDeque(maxArenaSize)));
    }

    public int[] get(int minSize) {
        Preconditions.checkArgument(minSize > 0);

        int minRequiredBits = 32 - Integer.numberOfLeadingZeros(minSize - 1);
        int[] arr = this.arenas[minRequiredBits].pollLast();
        return arr != null ? arr : new int[1 << minRequiredBits];
    }

    public void release(@NonNull int[] arr) {
        int length = arr.length;
        Preconditions.checkArgument(length != 0 && BinMath.isPow2(length));

        int minRequiredBits = 32 - Integer.numberOfLeadingZeros(length - 1);
        Deque<int[]> arena = this.arenas[minRequiredBits];
        if (arena.size() < this.maxArenaSize) {
            arena.addLast(arr);
        }
    }
}
