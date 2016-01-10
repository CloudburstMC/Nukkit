package cn.nukkit.utils;
/**
 * Created by Snake1999 on 2016/1/10.
 * Package cn.nukkit.utils in project nukkit
 */
public class Color extends java.awt.Color {

    public static final Color voidColor = (Color) new java.awt.Color(0x00, 0x00, 0x00);

    public static final Color airColor = (Color) new java.awt.Color(0x00, 0x00, 0x00);
    public static final Color grassColor = (Color) new java.awt.Color(0x7f, 0xb2, 0x38);
    public static final Color sandColor = (Color) new java.awt.Color(0xf1, 0xe9, 0xa3);
    public static final Color clothColor = (Color) new java.awt.Color(0xa7, 0xa7, 0xa7);
    public static final Color tntColor = (Color) new java.awt.Color(0xff, 0x00, 0x00);
    public static final Color iceColor = (Color) new java.awt.Color(0xa0, 0xa0, 0xff);
    public static final Color ironColor = (Color) new java.awt.Color(0xa7, 0xa7, 0xa7);
    public static final Color foliageColor = (Color) new java.awt.Color(0x00, 0x7c, 0x00);
    public static final Color snowColor = (Color) new java.awt.Color(0xff, 0xff, 0xff);
    public static final Color clayColor = (Color) new java.awt.Color(0xa4, 0xa8, 0xb8);
    public static final Color dirtColor = (Color) new java.awt.Color(0xb7, 0x6a, 0x2f);
    public static final Color stoneColor = (Color) new java.awt.Color(0x70, 0x70, 0x70);
    public static final Color waterColor = (Color) new java.awt.Color(0x40, 0x40, 0xff);
    public static final Color lavaColor = tntColor;
    public static final Color woodColor = (Color) new java.awt.Color(0x68, 0x53, 0x32);
    public static final Color quartzColor = (Color) new java.awt.Color(0xff, 0xfc, 0xf5);
    public static final Color adobeColor = (Color) new java.awt.Color(0xd8, 0x7f, 0x33);

    public static final Color whiteColor = snowColor;
    public static final Color orangeColor = adobeColor;
    public static final Color magentaColor = (Color) new java.awt.Color(0xb2, 0x4c, 0xd8);
    public static final Color lightBlueColor = (Color) new java.awt.Color(0x66, 0x99, 0xd8);
    public static final Color yellowColor = (Color) new java.awt.Color(0xe5, 0xe5, 0x33);
    public static final Color limeColor = (Color) new java.awt.Color(0x7f, 0xcc, 0x19);
    public static final Color pinkColor = (Color) new java.awt.Color(0xf2, 0x7f, 0xa5);
    public static final Color grayColor = (Color) new java.awt.Color(0x4c, 0x4c, 0x4c);
    public static final Color silverColor = (Color) new java.awt.Color(0x99, 0x99, 0x99);
    public static final Color cyanColor = (Color) new java.awt.Color(0x4c, 0x7f, 0x99);
    public static final Color purpleColor = (Color) new java.awt.Color(0x7f, 0x3f, 0xb2);
    public static final Color blueColor = (Color) new java.awt.Color(0x33, 0x4c, 0xb2);
    public static final Color brownColor = (Color) new java.awt.Color(0x66, 0x4c, 0x33);
    public static final Color greenColor = (Color) new java.awt.Color(0x66, 0x7f, 0x33);
    public static final Color redColor = (Color) new java.awt.Color(0x99, 0x33, 0x33);
    public static final Color blackColor = (Color) new java.awt.Color(0x19, 0x19, 0x19);

    public static final Color goldColor = (Color) new java.awt.Color(0xfa, 0xee, 0x4d);
    public static final Color diamondColor = (Color) new java.awt.Color(0x5c, 0xdb, 0xd5);
    public static final Color lapisColor = (Color) new java.awt.Color(0x4a, 0x80, 0xff);
    public static final Color emeraldColor = (Color) new java.awt.Color(0x00, 0xd9, 0x3a);
    public static final Color obsidianColor = (Color) new java.awt.Color(0x15, 0x14, 0x1f);
    public static final Color netherrackColor = (Color) new java.awt.Color(0x70, 0x02, 0x00);
    public static final Color redstoneColor = tntColor;

    public Color(float r, float g, float b, float a) {
        super(r, g, b, a);
    }

    public Color(float r, float g, float b) {
        super(r, g, b);
    }

    public Color(int rgba, boolean hasAlpha) {
        super(rgba, hasAlpha);
    }

    public Color(int r, int g, int b, int a) {
        super(r, g, b, a);
    }

    public Color(int rgb) {
        super(rgb);
    }

    public Color(int r, int g, int b) {
        super(r, g, b);
    }

    public static Color getDyeColor(int dyeColorMeta) {
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

}
