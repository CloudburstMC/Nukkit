package cn.nukkit.blockproperty;

import cn.nukkit.blockproperty.exception.InvalidBlockPropertyMetaException;
import cn.nukkit.math.BlockFace;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class BlockPropertyTest {
    BlockProperty<BlockFace> direction = CommonBlockProperties.FACING_DIRECTION;
    
    @Test
    void validateMeta() {
        assertThrows(InvalidBlockPropertyMetaException.class, ()-> direction.validateMeta(7, 0));
        assertThrows(InvalidBlockPropertyMetaException.class, ()-> direction.validateMeta(7L, 0));
        assertThrows(InvalidBlockPropertyMetaException.class, ()-> direction.validateMeta(BigInteger.valueOf(7), 0));
    }

    @Test
    void getValue() {
        assertEquals(BlockFace.EAST, direction.getValue(13, 0));
        assertEquals(BlockFace.EAST, direction.getValue(13L, 0));
        assertEquals(BlockFace.EAST, direction.getValue(BigInteger.valueOf(13), 0));   
    }

    @Test
    void setValue() {
        assertEquals(12, direction.setValue(13, 0, BlockFace.WEST));
        assertEquals(12L, direction.setValue(13L, 0, BlockFace.WEST));
        assertEquals(BigInteger.valueOf(12), direction.setValue(BigInteger.valueOf(13), 0, BlockFace.WEST));
    }

    @Test
    void setIntValue() {
        assertEquals(2, direction.getIntValue(-21, 2));
        assertEquals(2, direction.getIntValue(-21L, 2));
        assertEquals(2, direction.getIntValue(BigInteger.valueOf(-21), 2));
    }
}
