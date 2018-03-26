package cn.nukkit.server.entity.component;

import cn.nukkit.api.entity.component.Colorable;
import cn.nukkit.api.util.data.DyeColor;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;

public class ColorableComponent implements Colorable {
    private DyeColor color;

    public ColorableComponent(DyeColor color) {
        this.color = color;
    }

    @Override
    public DyeColor getColor() {
        return color;
    }

    @Override
    public void setColor(@Nonnull DyeColor color) {
        this.color = Preconditions.checkNotNull(color, "color");
    }
}
