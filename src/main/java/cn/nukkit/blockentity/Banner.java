package cn.nukkit.blockentity;

import cn.nukkit.utils.BannerPattern;
import cn.nukkit.utils.DyeColor;
import com.google.common.collect.ImmutableList;

public interface Banner extends BlockEntity {

    DyeColor getBase();

    void setBase(DyeColor color);

    int getBannerType();

    void setBannerType(int type);

    void addPattern(BannerPattern pattern);

    BannerPattern getPattern(int index);

    ImmutableList<BannerPattern> getPatterns();

    void removePattern(int index);
}
