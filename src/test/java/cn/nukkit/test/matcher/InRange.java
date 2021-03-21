package cn.nukkit.test.matcher;

import lombok.RequiredArgsConstructor;
import org.mockito.ArgumentMatcher;

import java.io.Serializable;

/**
 * @author joserobjr
 */
@RequiredArgsConstructor
public class InRange<C extends Comparable<C>> implements ArgumentMatcher<C>, Serializable {
    private final C from;
    private final C to;
    
    @Override
    public boolean matches(C comparable) {
        return from.compareTo(comparable) <= 0 && to.compareTo(comparable) >= 1;
    }
}
