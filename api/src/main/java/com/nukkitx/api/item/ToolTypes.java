package com.nukkitx.api.item;

import lombok.RequiredArgsConstructor;

public class ToolTypes {
    public static final ToolType SWORD = new IntTool(1.5f);
    public static final ToolType SHOVEL = new IntTool();
    public static final ToolType PICKAXE = new IntTool();
    public static final ToolType AXE = new IntTool();
    public static final ToolType SHEARS = new IntTool(15f);

    @RequiredArgsConstructor
    public static class IntTool implements ToolType {
        private final float multiplier;

        public IntTool() {
            multiplier = 1f;
        }

        @Override
        public float getEfficiencyMultiplier() {
            return multiplier;
        }
    }
}
