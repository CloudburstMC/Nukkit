/*
 * https://PowerNukkit.org - The Nukkit you know but Powerful!
 * Copyright (C) 2020  José Roberto de Araújo Júnior
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package cn.nukkit.block;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.UnsignedIntBlockProperty;
import cn.nukkit.blockproperty.exception.InvalidBlockPropertyMetaException;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.blockstate.BlockStateRepair;
import cn.nukkit.blockstate.MutableBlockState;
import cn.nukkit.math.BlockFace;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.function.Consumer;

import static cn.nukkit.blockproperty.CommonBlockProperties.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author joserobjr
 * @since 2020-10-04
 */
@ExtendWith(PowerNukkitExtension.class)
class BlockTest {
    @SuppressWarnings("deprecation")
    private static final int TEST_BLOCK_ID = Block.list.length -2;
    private static final Field MUTABLE_STATE;
    private static UnsignedIntBlockProperty HUGE = new UnsignedIntBlockProperty("huge", false, 0xFFFFFFFF);

    static {
        try {
            MUTABLE_STATE = Block.class.getDeclaredField("mutableState");
            MUTABLE_STATE.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new ExceptionInInitializerError(e);
        }
    }
    
    BlockTestBlock block;
    
    @BeforeEach
    void setUp() {
        Block.registerBlockImplementation(TEST_BLOCK_ID, BlockTestBlock.class, "test:testblock", false);
        block = (BlockTestBlock) Block.get(TEST_BLOCK_ID);
        assertNull(getDirectMutableState());
    }

    @Test
    void setGetDamage() {
        assertNull(getDirectMutableState());
        assertEquals(0, block.getDamage());
        
        block.setDamage(0);
        assertNull(getDirectMutableState());
        assertEquals(0, block.getDamage());

        block.setDamage(1);
        assertNotNull(getDirectMutableState());
        assertEquals(1, block.getDamage());
    }

    @Test
    void getBigDamage() {
        assertNull(getDirectMutableState());
        assertEquals(0, block.getBigDamage());

        block.setDamage(0);
        assertNull(getDirectMutableState());
        assertEquals(0, block.getBigDamage());

        block.setDamage(1);
        assertNotNull(getDirectMutableState());
        assertEquals(1, block.getBigDamage());
    }

    @Test
    void getHugeDamage() {
        assertNull(getDirectMutableState());
        assertEquals(BigInteger.ZERO, block.getHugeDamage());

        block.setDamage(0);
        assertNull(getDirectMutableState());
        assertEquals(BigInteger.ZERO, block.getHugeDamage());

        block.setDamage(1);
        assertNotNull(getDirectMutableState());
        assertEquals(BigInteger.ONE, block.getHugeDamage());
    }

    @Test
    void equalsCheckingDamage() {
        Block b2 = Block.get(TEST_BLOCK_ID);

        assertNull(getDirectMutableState());
        assertNull(getDirectMutableState(b2));
        
        assertTrue(Block.equals(block, b2, true));
        assertNull(getDirectMutableState());
        assertNull(getDirectMutableState(b2));
        
        block.setDamage(1);
        assertFalse(Block.equals(block, b2, true));
        assertNotNull(getDirectMutableState());
        assertNull(getDirectMutableState(b2));

        block = (BlockTestBlock) Block.get(TEST_BLOCK_ID);
        b2.setDamage(1);
        assertFalse(Block.equals(block, b2, true));
        assertNull(getDirectMutableState());
        assertNotNull(getDirectMutableState(b2));

        block.setDamage(1);
        assertTrue(Block.equals(block, b2, true));
        assertNotNull(getDirectMutableState());
        assertNotNull(getDirectMutableState(b2));

        block = (BlockTestBlock) Block.get(TEST_BLOCK_ID);
        assertFalse(Block.equals(block, b2, true));
        b2 = Block.get(BlockID.STONE);
        assertNull(getDirectMutableState());
        assertNull(getDirectMutableState(b2));
    }

    @Test
    void setState() {
        assertNull(getDirectMutableState());
        
        block.setState(BlockState.of(TEST_BLOCK_ID, 0));
        assertNull(getDirectMutableState());

        block.setState(BlockState.of(TEST_BLOCK_ID, 1));
        assertNotNull(getDirectMutableState());
        assertEquals(1, block.getDamage());
    }

    @Test
    void setDataStorageNoRepair() {
        assertNull(getDirectMutableState());

        block.setDataStorage(0);
        assertNull(getDirectMutableState());

        block.setDataStorage(1);
        assertNotNull(getDirectMutableState());
        assertEquals(1, block.getDamage());
    }

    @Test
    void setDataStorageRepair() {
        assertNull(getDirectMutableState());

        block.setDataStorage(0, true);
        assertNull(getDirectMutableState());

        block.setDataStorage(1, true);
        assertNotNull(getDirectMutableState());
        assertEquals(1, block.getDamage());
    }

    @Test
    void setDataStorageRepairConsumer() {
        assertNull(getDirectMutableState());

        Consumer<BlockStateRepair> blockStateRepairConsumer = repair -> {
            throw new AssertionError("Shouldn't be called");
        };
        
        block.setDataStorage(0, true, blockStateRepairConsumer);
        assertNull(getDirectMutableState());

        block.setDataStorage(1, true, blockStateRepairConsumer);
        assertNotNull(getDirectMutableState());
        assertEquals(1, block.getDamage());
    }

    @Test
    void setDataStorageFromIntNoRepair() {
        assertNull(getDirectMutableState());

        block.setDataStorageFromInt(0);
        assertNull(getDirectMutableState());

        block.setDataStorageFromInt(1);
        assertNotNull(getDirectMutableState());
        assertEquals(1, block.getDamage());
    }

    @Test
    void setDataStorageFromIntRepair() {
        assertNull(getDirectMutableState());

        block.setDataStorageFromInt(0, true);
        assertNull(getDirectMutableState());

        block.setDataStorageFromInt(1, true);
        assertNotNull(getDirectMutableState());
        assertEquals(1, block.getDamage());
    }

    @Test
    void setDataStorageFromIntRepairConsumer() {
        assertNull(getDirectMutableState());

        Consumer<BlockStateRepair> blockStateRepairConsumer = repair -> {
            throw new AssertionError("Shouldn't be called");
        };

        block.setDataStorageFromInt(0, true, blockStateRepairConsumer);
        assertNull(getDirectMutableState());

        block.setDataStorageFromInt(1, true, blockStateRepairConsumer);
        assertNotNull(getDirectMutableState());
        assertEquals(1, block.getDamage());
    }

    @Test
    void setGetPropertyValueTypeBool() {
        assertNull(getDirectMutableState());
        
        block.setPropertyValue(TOGGLE, false);
        assertNull(getDirectMutableState());

        block.setPropertyValue(TOGGLE.getName(), false);
        assertNull(getDirectMutableState());
        
        assertFalse(block.getPropertyValue(TOGGLE));
        assertNull(getDirectMutableState());

        assertEquals(Boolean.FALSE, block.getPropertyValue(TOGGLE.getName()));
        assertNull(getDirectMutableState());

        block.setPropertyValue(TOGGLE, true);
        assertNotNull(getDirectMutableState());
        assertTrue(block.getPropertyValue(TOGGLE));
        
        Block b2 = Block.get(TEST_BLOCK_ID);
        b2.setPropertyValue(TOGGLE.getName(), true);
        assertNotNull(getDirectMutableState(b2));
        assertEquals(Boolean.TRUE, b2.getPropertyValue(TOGGLE.getName()));
    }

    @Test
    void setGetPropertyValueTypeInt() {
        assertNull(getDirectMutableState());

        block.setPropertyValue(REDSTONE_SIGNAL, 0);
        assertNull(getDirectMutableState());

        block.setPropertyValue(REDSTONE_SIGNAL.getName(), 0);
        assertNull(getDirectMutableState());
        
        assertEquals(0, block.getPropertyValue(REDSTONE_SIGNAL));
        assertNull(getDirectMutableState());

        assertEquals(0, block.getPropertyValue(REDSTONE_SIGNAL.getName()));
        assertNull(getDirectMutableState());

        block.setPropertyValue(REDSTONE_SIGNAL, 1);
        assertNotNull(getDirectMutableState());
        assertEquals(1, block.getPropertyValue(REDSTONE_SIGNAL));

        Block b2 = Block.get(TEST_BLOCK_ID);
        b2.setPropertyValue(REDSTONE_SIGNAL.getName(), 1);
        assertNotNull(getDirectMutableState(b2));
        assertEquals(1, b2.getPropertyValue(REDSTONE_SIGNAL.getName()));
    }

    @Test
    void setGetPropertyValueTypeArray() {
        assertNull(getDirectMutableState());

        block.setPropertyValue(FACING_DIRECTION, BlockFace.DOWN);
        assertNull(getDirectMutableState());

        block.setPropertyValue(FACING_DIRECTION.getName(), BlockFace.DOWN);
        assertNull(getDirectMutableState());

        assertEquals(BlockFace.DOWN, block.getPropertyValue(FACING_DIRECTION));
        assertNull(getDirectMutableState());

        assertEquals(BlockFace.DOWN, block.getPropertyValue(FACING_DIRECTION.getName()));
        assertNull(getDirectMutableState());

        block.setPropertyValue(FACING_DIRECTION, BlockFace.UP);
        assertNotNull(getDirectMutableState());
        assertEquals(BlockFace.UP, block.getPropertyValue(FACING_DIRECTION));

        Block b2 = Block.get(TEST_BLOCK_ID);
        b2.setPropertyValue(FACING_DIRECTION.getName(), BlockFace.UP);
        assertNotNull(getDirectMutableState(b2));
        assertEquals(BlockFace.UP, b2.getPropertyValue(FACING_DIRECTION.getName()));
    }

    @Test
    void setGetPropertyValueTypeUnsignedInt() {
        assertNull(getDirectMutableState());

        block.setPropertyValue(HUGE, 0);
        assertNull(getDirectMutableState());

        block.setPropertyValue(HUGE.getName(), 0);
        assertNull(getDirectMutableState());

        assertEquals(0, block.getPropertyValue(HUGE));
        assertNull(getDirectMutableState());

        assertEquals(0, block.getPropertyValue(HUGE.getName()));
        assertNull(getDirectMutableState());

        block.setPropertyValue(HUGE, 1);
        assertNotNull(getDirectMutableState());
        assertEquals(1, block.getPropertyValue(HUGE));

        Block b2 = Block.get(TEST_BLOCK_ID);
        b2.setPropertyValue(HUGE.getName(), 1);
        assertNotNull(getDirectMutableState(b2));
        assertEquals(1, b2.getPropertyValue(HUGE.getName()));
    }

    @Test
    void setGetBooleanValue() {
        assertNull(getDirectMutableState());
        
        block.setBooleanValue(TOGGLE, false);
        assertNull(getDirectMutableState());

        block.setBooleanValue(TOGGLE.getName(), false);
        assertNull(getDirectMutableState());
        
        assertFalse(block.getBooleanValue(TOGGLE));
        assertNull(getDirectMutableState());

        assertFalse(block.getBooleanValue(TOGGLE.getName()));
        assertNull(getDirectMutableState());
        
        block.setBooleanValue(TOGGLE, true);
        assertNotNull(getDirectMutableState());
        assertTrue(block.getBooleanValue(TOGGLE));

        Block b2 = Block.get(TEST_BLOCK_ID);
        b2.setBooleanValue(TOGGLE.getName(), true);
        assertNotNull(getDirectMutableState(b2));
        assertTrue(b2.getBooleanValue(TOGGLE.getName()));
    }

    @Test
    void setGetIntValueInt() {
        assertNull(getDirectMutableState());

        block.setIntValue(REDSTONE_SIGNAL, 0);
        assertNull(getDirectMutableState());

        block.setIntValue(REDSTONE_SIGNAL.getName(), 0);
        assertNull(getDirectMutableState());

        assertEquals(0, block.getIntValue(REDSTONE_SIGNAL));
        assertNull(getDirectMutableState());

        assertEquals(0, block.getIntValue(REDSTONE_SIGNAL.getName()));
        assertNull(getDirectMutableState());

        block.setIntValue(REDSTONE_SIGNAL, 1);
        assertNotNull(getDirectMutableState());
        assertEquals(1, block.getIntValue(REDSTONE_SIGNAL));

        Block b2 = Block.get(TEST_BLOCK_ID);
        b2.setIntValue(REDSTONE_SIGNAL.getName(), 1);
        assertNotNull(getDirectMutableState(b2));
        assertEquals(1, b2.getIntValue(REDSTONE_SIGNAL.getName()));
    }

    @Test
    void setGetIntValueUnsigned() {
        assertNull(getDirectMutableState());

        block.setIntValue(HUGE, 0);
        assertNull(getDirectMutableState());

        block.setIntValue(HUGE.getName(), 0);
        assertNull(getDirectMutableState());

        assertEquals(0, block.getIntValue(HUGE));
        assertNull(getDirectMutableState());

        assertEquals(0, block.getIntValue(HUGE.getName()));
        assertNull(getDirectMutableState());

        block.setIntValue(HUGE, 1);
        assertNotNull(getDirectMutableState());
        assertEquals(1, block.getIntValue(HUGE));

        Block b2 = Block.get(TEST_BLOCK_ID);
        b2.setIntValue(HUGE.getName(), 1);
        assertNotNull(getDirectMutableState(b2));
        assertEquals(1, b2.getIntValue(HUGE.getName()));
    }

    @Test
    void getPersistenceValue() {
        assertNull(getDirectMutableState());
        
        assertEquals("0", block.getPersistenceValue(FACING_DIRECTION));
        assertNull(getDirectMutableState());
        
        assertEquals("0", block.getPersistenceValue(FACING_DIRECTION.getName()));
        assertNull(getDirectMutableState());
        
        block.setPropertyValue(FACING_DIRECTION, BlockFace.UP);
        assertNotNull(getDirectMutableState());
        assertEquals("1", block.getPersistenceValue(FACING_DIRECTION));
        assertEquals("1", block.getPersistenceValue(FACING_DIRECTION.getName()));
    }

    private MutableBlockState getDirectMutableState() {
        return getDirectMutableState(block);
    }

    @SneakyThrows
    private MutableBlockState getDirectMutableState(Block b) {
        return (MutableBlockState) MUTABLE_STATE.get(b);
    }
    
    public static class BlockTestBlock extends BlockMeta {
        public static BlockProperties PROPERTIES = new BlockProperties(FACING_DIRECTION, TOGGLE, REDSTONE_SIGNAL, HUGE);
        
        public BlockTestBlock() {
            this(0);
        }

        public BlockTestBlock(int meta) throws InvalidBlockPropertyMetaException {
            super(meta);
        }

        @Override
        public String getName() {
            return "Test Block";
        }

        @Override
        public int getId() {
            return TEST_BLOCK_ID;
        }

        @Since("1.4.0.0-PN")
        @PowerNukkitOnly
        @Nonnull
        @Override
        public BlockProperties getProperties() {
            return PROPERTIES;
        }
    } 
}
