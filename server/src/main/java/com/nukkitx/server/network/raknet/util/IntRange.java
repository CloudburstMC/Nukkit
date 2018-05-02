package com.nukkitx.server.network.raknet.util;

import com.google.common.base.Preconditions;
import lombok.Value;

@Value
public final class IntRange {
    private final int start;
    private final int end;

    public IntRange(int num) {
        this(num, num);
    }

    public IntRange(int start, int end) {
        Preconditions.checkArgument(start <= end, "start is less than end");
        this.start = start;
        this.end = end;
    }
}