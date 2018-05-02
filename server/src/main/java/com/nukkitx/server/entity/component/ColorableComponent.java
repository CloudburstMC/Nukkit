package com.nukkitx.server.entity.component;

import com.google.common.base.Preconditions;
import com.nukkitx.api.entity.component.Colorable;
import com.nukkitx.api.metadata.data.DyeColor;

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
