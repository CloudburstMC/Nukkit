package cn.nukkit.level;

import lombok.Data;

@Data
public class DimensionData {
    private final int dimensionId;
    private final int minHeight;
    private final int maxHeight;
    private final int height;

    public DimensionData(int dimensionId, int minHeight, int maxHeight) {
        this.dimensionId = dimensionId;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;

        int height = maxHeight - minHeight;
        if (minHeight <= 0 && maxHeight > 0) {
            height += 1; // 0 y coordinate counts too
        }
        this.height = height;
    }
}
