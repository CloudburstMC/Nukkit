package com.nukkitx.api.item;

import lombok.RequiredArgsConstructor;

public class TierTypes {
    public static final TierType WOOD = new IntTier(100, 2);
    public static final TierType STONE = new IntTier(200, 4);
    public static final TierType IRON = new IntTier(300, 6);
    public static final TierType DIAMOND = new IntTier(400, 8);
    public static final TierType GOLD = new IntTier(500, 12);

    @RequiredArgsConstructor
    private static class IntTier implements TierType {
        private final int level;
        private final float miningEfficiency;

        @Override
        public int getLevel() {
            return level;
        }

        @Override
        public float getMiningEfficiency() {
            return miningEfficiency;
        }
    }
}
