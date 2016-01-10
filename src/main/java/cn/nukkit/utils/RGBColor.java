package cn.nukkit.utils;

/**
 * Created by Snake1999 on 2016/1/10.
 * Package cn.nukkit.utils in project nukkit
 */
public final class RGBColor {

    public static final RGBColor voidColor = RGBColor.of(0x00, 0x00, 0x00);

    public static final RGBColor airColor = RGBColor.of(0x00, 0x00, 0x00);
    public static final RGBColor grassColor = RGBColor.of(0x7f, 0xb2, 0x38);
    public static final RGBColor sandColor = RGBColor.of(0xf1, 0xe9, 0xa3);
    public static final RGBColor clothColor = RGBColor.of(0xa7, 0xa7, 0xa7);
    public static final RGBColor tntColor = RGBColor.of(0xff, 0x00, 0x00);
    public static final RGBColor iceColor = RGBColor.of(0xa0, 0xa0, 0xff);
    public static final RGBColor ironColor = RGBColor.of(0xa7, 0xa7, 0xa7);
    public static final RGBColor foliageColor = RGBColor.of(0x00, 0x7c, 0x00);
    public static final RGBColor snowColor = RGBColor.of(0xff, 0xff, 0xff);
    public static final RGBColor clayColor = RGBColor.of(0xa4, 0xa8, 0xb8);
    public static final RGBColor dirtColor = RGBColor.of(0xb7, 0x6a, 0x2f);
    public static final RGBColor stoneColor = RGBColor.of(0x70, 0x70, 0x70);
    public static final RGBColor waterColor = RGBColor.of(0x40, 0x40, 0xff);
    public static final RGBColor lavaColor = tntColor;
    public static final RGBColor woodColor = RGBColor.of(0x68, 0x53, 0x32);
    public static final RGBColor quartzColor = RGBColor.of(0xff, 0xfc, 0xf5);
    public static final RGBColor adobeColor = RGBColor.of(0xd8, 0x7f, 0x33);

    public static final RGBColor whiteColor = snowColor;
    public static final RGBColor orangeColor = adobeColor;
    public static final RGBColor magentaColor = RGBColor.of(0xb2, 0x4c, 0xd8);
    public static final RGBColor lightBlueColor = RGBColor.of(0x66, 0x99, 0xd8);
    public static final RGBColor yellowColor = RGBColor.of(0xe5, 0xe5, 0x33);
    public static final RGBColor limeColor = RGBColor.of(0x7f, 0xcc, 0x19);
    public static final RGBColor pinkColor = RGBColor.of(0xf2, 0x7f, 0xa5);
    public static final RGBColor grayColor = RGBColor.of(0x4c, 0x4c, 0x4c);
    public static final RGBColor silverColor = RGBColor.of(0x99, 0x99, 0x99);
    public static final RGBColor cyanColor = RGBColor.of(0x4c, 0x7f, 0x99);
    public static final RGBColor purpleColor = RGBColor.of(0x7f, 0x3f, 0xb2);
    public static final RGBColor blueColor = RGBColor.of(0x33, 0x4c, 0xb2);
    public static final RGBColor brownColor = RGBColor.of(0x66, 0x4c, 0x33);
    public static final RGBColor greenColor = RGBColor.of(0x66, 0x7f, 0x33);
    public static final RGBColor redColor = RGBColor.of(0x99, 0x33, 0x33);
    public static final RGBColor blackColor = RGBColor.of(0x19, 0x19, 0x19);

    public static final RGBColor goldColor = RGBColor.of(0xfa, 0xee, 0x4d);
    public static final RGBColor diamondColor = RGBColor.of(0x5c, 0xdb, 0xd5);
    public static final RGBColor lapisColor = RGBColor.of(0x4a, 0x80, 0xff);
    public static final RGBColor emeraldColor = RGBColor.of(0x00, 0xd9, 0x3a);
    public static final RGBColor obsidianColor = RGBColor.of(0x15, 0x14, 0x1f);
    public static final RGBColor netherrackColor = RGBColor.of(0x70, 0x02, 0x00);
    public static final RGBColor redstoneColor = tntColor;

    public final int colorValue;

    private RGBColor(int colorValue) {
        this.colorValue = colorValue;
    }

    private RGBColor(int r, int g, int b) {
        this(toColorValue(r, g, b));
    }

    public static RGBColor of(int r, int g, int b) {
        return new RGBColor(r, g, b);
    }

    public static RGBColor getDyeColor(int dyeColorMeta) {
        switch (dyeColorMeta & 0x0f) {
            case 0: return whiteColor;
            case 1: return orangeColor;
            case 2: return magentaColor;
            case 3: return lightBlueColor;
            case 4: return yellowColor;
            case 5: return limeColor;
            case 6: return pinkColor;
            case 7: return grayColor;
            case 8: return silverColor;
            case 9: return cyanColor;
            case 10: return purpleColor;
            case 11: return blueColor;
            case 12: return brownColor;
            case 13: return greenColor;
            case 14: return redColor;
            case 15: return blackColor;

            default: return whiteColor;
        }
    }

    public static int toColorValue(int r, int g, int b) {
        return ((r & 0xff) << 16) | ((g & 0xff) << 8) | (b & 0xff);
    }

}
