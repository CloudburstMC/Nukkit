package cn.nukkit.blockentity;

import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.DyeColor;

public class BlockEntityBanner extends BlockEntitySpawnable {

    public static final String PATTERN_BOTTOM_STRIPE = "bs";
    public static final String PATTERN_TOP_STRIPE = "ts";
    public static final String PATTERN_LEFT_STRIPE = "ls";
    public static final String PATTERN_RIGHT_STRIPE = "rs";
    public static final String PATTERN_CENTER_STRIPE = "cs";
    public static final String PATTERN_MIDDLE_STRIPE = "ms";
    public static final String PATTERN_DOWN_RIGHT_STRIPE = "drs";
    public static final String PATTERN_DOWN_LEFT_STRIPE = "dls";
    public static final String PATTERN_SMALL_STRIPES = "ss";
    public static final String PATTERN_DIAGONAL_CROSS = "cr";
    public static final String PATTERN_SQUARE_CROSS = "sc";
    public static final String PATTERN_LEFT_OF_DIAGONAL = "ld";
    public static final String PATTERN_RIGHT_OF_UPSIDE_DOWN_DIAGONAL = "rud";
    public static final String PATTERN_LEFT_OF_UPSIDE_DOWN_DIAGONAL = "lud";
    public static final String PATTERN_RIGHT_OF_DIAGONAL = "rd";
    public static final String PATTERN_VERTICAL_HALF_LEFT = "vh";
    public static final String PATTERN_VERTICAL_HALF_RIGHT = "vhr";
    public static final String PATTERN_HORIZONTAL_HALF_TOP = "hh";
    public static final String PATTERN_HORIZONTAL_HALF_BOTTOM = "hhb";
    public static final String PATTERN_BOTTOM_LEFT_CORNER = "bl";
    public static final String PATTERN_BOTTOM_RIGHT_CORNER = "br";
    public static final String PATTERN_TOP_LEFT_CORNER = "tl";
    public static final String PATTERN_TOP_RIGHT_CORNER = "tr";
    public static final String PATTERN_BOTTOM_TRIANGLE = "bt";
    public static final String PATTERN_TOP_TRIANGLE = "tt";
    public static final String PATTERN_BOTTOM_TRIANGLE_SAWTOOTH = "bts";
    public static final String PATTERN_TOP_TRIANGLE_SAWTOOTH = "tts";
    public static final String PATTERN_MIDDLE_CIRCLE = "mc";
    public static final String PATTERN_MIDDLE_RHOMBUS = "mr";
    public static final String PATTERN_BORDER = "bo";
    public static final String PATTERN_CURLY_BORDER = "cbo";
    public static final String PATTERN_BRICK = "bri";
    public static final String PATTERN_GRADIENT = "gra";
    public static final String PATTERN_GRADIENT_UPSIDE_DOWN = "gru";
    public static final String PATTERN_CREEPER = "cre";
    public static final String PATTERN_SKULL = "sku";
    public static final String PATTERN_FLOWER = "flo";
    public static final String PATTERN_MOJANG = "moj";

    public BlockEntityBanner(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean isBlockEntityValid() {
        return this.getBlock().getId() == Block.WALL_BANNER || this.getBlock().getId() == Block.STANDING_BANNER;
    }

    @Override
    public String getName() {
        return "Banner";
    }

    public int getBaseColor() {
        return this.namedTag.getInt("Base");
    }

    public void setBaseColor(DyeColor color) {
        this.namedTag.putInt("Base", color.getDyeData() & 0x0f);
    }

    public int getType() {
        return this.namedTag.getInt("Type");
    }

    public void setType(int type) {
        this.namedTag.putInt("Type", type);
    }

    public void addPattern(String pattern, DyeColor color) {
        ListTag<CompoundTag> patterns = this.namedTag.getList("Patterns", CompoundTag.class);
        patterns.add(new CompoundTag("").
                putInt("Color", color.getDyeData() & 0x0f).
                putString("Pattern", pattern));
        this.namedTag.putList(patterns);
    }

    public CompoundTag getPattern(int index) {
        return this.namedTag.getList("Patterns").size() > index && index >= 0 ? this.namedTag.getList("Patterns", CompoundTag.class).get(index) : new CompoundTag();
    }

    public void removePattern(int index) {
        ListTag<CompoundTag> patterns = this.namedTag.getList("Patterns", CompoundTag.class);
        if(patterns.size() > index && index >= 0) {
            patterns.remove(index);
        }
    }

    public void setPatterns(ListTag<CompoundTag> patterns) {
        this.namedTag.put("Patterns", patterns);
    }

    public ListTag<CompoundTag> getPatterns() {
        return this.namedTag.getList("Patterns", CompoundTag.class);
    }

    @Override
    public CompoundTag getSpawnCompound() {
        return getDefaultCompound(this, BANNER)
                .putInt("Base", getBaseColor())
                .putList(this.namedTag.getList("Patterns"))
                .putInt("Type", getType());
    }
}
