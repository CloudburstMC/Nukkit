package cn.nukkit.item;

import cn.nukkit.utils.Identifier;

@FunctionalInterface
public interface ItemFactory {

    Item create(Identifier identifier);
}
