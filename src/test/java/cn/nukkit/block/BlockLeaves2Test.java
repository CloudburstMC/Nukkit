package cn.nukkit.block;

import cn.nukkit.blockproperty.exception.InvalidBlockPropertyMetaException;
import cn.nukkit.blockstate.BlockState;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class BlockLeaves2Test {
    @BeforeAll
    static void init() {
        Block.init();
    }
    
    @Test
    void issue482() {
        BlockState current = BlockState.of(BlockID.LEAVES2, 11);
        assertThrows(InvalidBlockPropertyMetaException.class, current::getBlock);
    }
}
