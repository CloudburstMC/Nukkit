package cn.nukkit.test.matcher;

import org.mockito.ArgumentMatcher;
import org.mockito.internal.progress.ThreadSafeMockingProgress;

/**
 * @author joserobjr
 */
public class ExtraArgumentMatchers {
    public static byte inRange(byte fromInclusive, byte toInclusive) {
        reportMatcher(new InRange<>(fromInclusive, toInclusive));
        return 0;
    }
    
    public static short inRange(short fromInclusive, short toInclusive) {
        reportMatcher(new InRange<>(fromInclusive, toInclusive));
        return 0;
    }
    
    public static char inRange(char fromInclusive, char toInclusive) {
        reportMatcher(new InRange<>(fromInclusive, toInclusive));
        return 0;
    }
    
    public static int inRange(int fromInclusive, int toInclusive) {
        reportMatcher(new InRange<>(fromInclusive, toInclusive));
        return 0;
    }
    
    public static long inRange(long fromInclusive, long toInclusive) {
        reportMatcher(new InRange<>(fromInclusive, toInclusive));
        return 0;
    }
    
    public static float inRange(float fromInclusive, float toInclusive) {
        reportMatcher(new InRange<>(fromInclusive, toInclusive));
        return 0;
    }
    
    public static double inRange(double fromInclusive, double toInclusive) {
        reportMatcher(new InRange<>(fromInclusive, toInclusive));
        return 0;
    }
    
    public static <C extends Comparable<C>> C inRange(C fromInclusive, C toInclusive) {
        reportMatcher(new InRange<>(fromInclusive, toInclusive));
        return fromInclusive;
    }

    private static void reportMatcher(ArgumentMatcher<?> matcher) {
        ThreadSafeMockingProgress.mockingProgress().getArgumentMatcherStorage().reportMatcher(matcher);
    }
}
