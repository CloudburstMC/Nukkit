package cn.nukkit.block.properties;

import lombok.Getter;

@Getter
public enum DripleafTilt {
    NONE(true, -1),
    UNSTABLE(false, 10),
    PARTIAL_TILT(true, 10),
    FULL_TILT(false, 100);

    private final boolean stable;
    private final int netxStateDelay;

    DripleafTilt(boolean stable, int netxStateDelay) {
        this.stable = stable;
        this.netxStateDelay = netxStateDelay;
    }
}
