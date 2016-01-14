package cn.nukkit.utils;

/**
 * Created by Snake1999 on 2016/1/10.
 * Package cn.nukkit.utils in project nukkit
 */
public class Color extends java.awt.Color {

    public static final Color transparentColor = new Color(0x00, 0x00, 0x00, 0x00);
    public static final Color voidColor = new Color(0x00, 0x00, 0x00, 0x00);

    public static final Color airColor = new Color(0x00, 0x00, 0x00);
    public static final Color grassColor = new Color(0x7f, 0xb2, 0x38);
    public static final Color sandColor = new Color(0xf1, 0xe9, 0xa3);
    public static final Color clothColor = new Color(0xa7, 0xa7, 0xa7);
    public static final Color tntColor = new Color(0xff, 0x00, 0x00);
    public static final Color iceColor = new Color(0xa0, 0xa0, 0xff);
    public static final Color ironColor = new Color(0xa7, 0xa7, 0xa7);
    public static final Color foliageColor = new Color(0x00, 0x7c, 0x00);
    public static final Color snowColor = new Color(0xff, 0xff, 0xff);
    public static final Color clayColor = new Color(0xa4, 0xa8, 0xb8);
    public static final Color dirtColor = new Color(0xb7, 0x6a, 0x2f);
    public static final Color stoneColor = new Color(0x70, 0x70, 0x70);
    public static final Color waterColor = new Color(0x40, 0x40, 0xff);
    public static final Color lavaColor = tntColor;
    public static final Color woodColor = new Color(0x68, 0x53, 0x32);
    public static final Color quartzColor = new Color(0xff, 0xfc, 0xf5);
    public static final Color adobeColor = new Color(0xd8, 0x7f, 0x33);

    public static final Color whiteColor = snowColor;
    public static final Color orangeColor = adobeColor;
    public static final Color magentaColor = new Color(0xb2, 0x4c, 0xd8);
    public static final Color lightBlueColor = new Color(0x66, 0x99, 0xd8);
    public static final Color yellowColor = new Color(0xe5, 0xe5, 0x33);
    public static final Color limeColor = new Color(0x7f, 0xcc, 0x19);
    public static final Color pinkColor = new Color(0xf2, 0x7f, 0xa5);
    public static final Color grayColor = new Color(0x4c, 0x4c, 0x4c);
    public static final Color silverColor = new Color(0x99, 0x99, 0x99);
    public static final Color cyanColor = new Color(0x4c, 0x7f, 0x99);
    public static final Color purpleColor = new Color(0x7f, 0x3f, 0xb2);
    public static final Color blueColor = new Color(0x33, 0x4c, 0xb2);
    public static final Color brownColor = new Color(0x66, 0x4c, 0x33);
    public static final Color greenColor = new Color(0x66, 0x7f, 0x33);
    public static final Color redColor = new Color(0x99, 0x33, 0x33);
    public static final Color blackColor = new Color(0x19, 0x19, 0x19);

    public static final Color goldColor = new Color(0xfa, 0xee, 0x4d);
    public static final Color diamondColor = new Color(0x5c, 0xdb, 0xd5);
    public static final Color lapisColor = new Color(0x4a, 0x80, 0xff);
    public static final Color emeraldColor = new Color(0x00, 0xd9, 0x3a);
    public static final Color obsidianColor = new Color(0x15, 0x14, 0x1f);
    public static final Color netherrackColor = new Color(0x70, 0x02, 0x00);
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
            case 0:
                return whiteColor;
            case 1:
                return orangeColor;
            case 2:
                return magentaColor;
            case 3:
                return lightBlueColor;
            case 4:
                return yellowColor;
            case 5:
                return limeColor;
            case 6:
                return pinkColor;
            case 7:
                return grayColor;
            case 8:
                return silverColor;
            case 9:
                return cyanColor;
            case 10:
                return purpleColor;
            case 11:
                return blueColor;
            case 12:
                return brownColor;
            case 13:
                return greenColor;
            case 14:
                return redColor;
            case 15:
                return blackColor;

            default:
                return whiteColor;
        }
    }

}
