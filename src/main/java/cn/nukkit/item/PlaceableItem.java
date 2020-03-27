package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.utils.Identifier;

public class PlaceableItem extends Item {
    private final Identifier blockId;

    private PlaceableItem(Identifier id, Identifier blockId) {
        super(id);
        this.blockId = blockId;
    }

    public static ItemFactory factory(Identifier blockId) {
        return identifier -> new PlaceableItem(identifier, blockId);
    }

    @Override
    public Block getBlock() {
        return Block.get(blockId);
    }
}
