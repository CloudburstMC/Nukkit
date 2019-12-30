package cn.nukkit.block;

import cn.nukkit.utils.Identifier;

@FunctionalInterface
public interface BlockFactory {

    Block create(Identifier id);
}
