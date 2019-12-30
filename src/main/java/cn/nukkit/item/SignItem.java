package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.utils.Identifier;

public class SignItem extends Item {
    private final Identifier blockId;

    private SignItem(Identifier id, Identifier blockId) {
        super(id);
        this.blockId = blockId;
    }

    public static ItemFactory factory(Identifier blockId) {
        return identifier -> new SignItem(identifier, blockId);
    }

    @Override
    public Block getBlock() {
        return Block.get(blockId);
    }

    @Override
    public int getMaxStackSize() {
        return 16;
    }
}
