package cn.nukkit.level.format.anvil;

import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockWall;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.ByteArrayTag;
import cn.nukkit.nbt.tag.CompoundTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(PowerNukkitExtension.class)
class ChunkSectionTest {
    @Test
    void omgThatIsHugePersistence() {
        ChunkSection section = new ChunkSection(4);
        BigInteger sixteenBytesData = new BigInteger("86151413121110090807060504030201", 16);
        section.setBlockState(0,0,0,BlockState.of(BlockID.STONE, sixteenBytesData));
        CompoundTag nbt = section.toNBT();

        ChunkSection loaded = new ChunkSection(nbt);
        assertEquals(sixteenBytesData, loaded.getBlockState(0,0,0).getHugeDamage());
    }
    
    @Test
    void longPersistence() {
        ChunkSection section = new ChunkSection(4);
        section.setBlockState(0,0,0,BlockState.of(BlockID.STONE, 0x81_34_00_00L));
        CompoundTag nbt = section.toNBT();

        ChunkSection loaded = new ChunkSection(nbt);
        assertEquals(BigInteger.valueOf(0x81_34_00_00L), loaded.getBlockState(0,0,0).getHugeDamage());
    }
    
    @Test
    void negativePersistence() {
        ChunkSection section = new ChunkSection(4);
        section.setBlockState(0,0,0,BlockState.of(BlockID.STONE, 1));
        CompoundTag nbt = section.toNBT();
        
        nbt.getByteArray("Data")[0] = -1;
        assertEquals(-1, nbt.getByteArray("Data")[0]);
        
        ChunkSection loaded = new ChunkSection(nbt);
        assertEquals(15, loaded.getBlockState(0,0,0).getExactIntStorage());
    }
    
    @Test
    void issuePowerNukkit698() {
        ChunkSection section = new ChunkSection(4);
        section.delayPaletteUpdates();
        BlockState bigState = BlockState.of(2, 0x01_00_00_00_0_0L);
        section.setBlockState(1,2,3, bigState);
        assertEquals(bigState, section.getBlockState(1,2,3));
        
        CompoundTag tag = section.toNBT();
        ChunkSection loadedSection = new ChunkSection(tag);
        
        assertEquals(bigState, loadedSection.getBlockState(1,2,3));
    }
    
    @Test
    void hugeIdPersistence() {
        ChunkSection section = new ChunkSection(4);
        section.delayPaletteUpdates();
        BlockWall wall = new BlockWall();
        wall.setWallType(BlockWall.WallType.BRICK);
        wall.setConnection(BlockFace.NORTH, BlockWall.WallConnectionType.SHORT);
        wall.setConnection(BlockFace.EAST, BlockWall.WallConnectionType.TALL);
        wall.setWallPost(true);
        int expected = 0b0001_1001_0000_0110;
        int x = 5, y = 6, z = 7, anvilIndex = 1653;
        assertEquals(expected, wall.getDamage());
        assertTrue(section.setBlock(x, y, z, wall.getId(), wall.getDamage()));
        assertEquals(BlockID.COBBLE_WALL, section.getBlockId(x, y, z));
        assertEquals(expected, section.getBlockData(x, y, z));

        CompoundTag compoundTag = section.toNBT();
        assertEquals(1, compoundTag.getList("DataHyper", ByteArrayTag.class).size());
        assertEquals((byte) 0b0001_1001, compoundTag.getList("DataHyper", ByteArrayTag.class).get(0).data[anvilIndex]);
        section = new ChunkSection(compoundTag);
        section.delayPaletteUpdates();
        assertEquals(BlockID.COBBLE_WALL, section.getBlockId(x, y, z));
        assertEquals(expected, section.getBlockData(x, y, z));

        int higherBits = 0x71_34_00_00;
        expected |= higherBits;
        
        section.setBlockData(x, y, z, expected);
        assertEquals(BlockID.COBBLE_WALL, section.getBlockId(x, y, z));
        assertEquals(expected, section.getBlockData(x, y, z));

        compoundTag = section.toNBT();
        assertEquals(3, compoundTag.getList("DataHyper", ByteArrayTag.class).size());
        assertEquals((byte) 0b0001_1001, compoundTag.getList("DataHyper", ByteArrayTag.class).get(0).data[anvilIndex]);
        assertEquals((byte) 0b0011_0100, compoundTag.getList("DataHyper", ByteArrayTag.class).get(1).data[anvilIndex]);
        assertEquals((byte) 0b0111_0001, compoundTag.getList("DataHyper", ByteArrayTag.class).get(2).data[anvilIndex]);
        compoundTag.remove("ContentVersion");
        section = new ChunkSection(compoundTag);
        section.delayPaletteUpdates();
        assertEquals(BlockID.COBBLE_WALL, section.getBlockId(x, y, z));
        assertEquals(expected, section.getBlockData(x, y, z));
    }
}
