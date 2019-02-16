package com.nukkitx.api.item;

import lombok.RequiredArgsConstructor;

public class TierTypes {
    public static final TierType WOOD = new IntTier(100, 2);
    public static final TierType GOLD = new IntTier(200, 12);
    public static final TierType STONE = new IntTier(300, 4);
    public static final TierType IRON = new IntTier(400, 6);
    public static final TierType DIAMOND = new IntTier(500, 8);

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
