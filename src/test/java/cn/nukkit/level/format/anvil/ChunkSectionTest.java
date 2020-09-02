package cn.nukkit.level.format.anvil;

import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockWall;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.ByteArrayTag;
import cn.nukkit.nbt.tag.CompoundTag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChunkSectionTest {
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

        int higherBits = 0x81_34_00_00;
        expected |= higherBits;
        
        section.setBlockData(x, y, z, expected);
        assertEquals(BlockID.COBBLE_WALL, section.getBlockId(x, y, z));
        assertEquals(expected, section.getBlockData(x, y, z));

        compoundTag = section.toNBT();
        assertEquals(3, compoundTag.getList("DataHyper", ByteArrayTag.class).size());
        assertEquals((byte) 0b0001_1001, compoundTag.getList("DataHyper", ByteArrayTag.class).get(0).data[anvilIndex]);
        assertEquals((byte) 0b0011_0100, compoundTag.getList("DataHyper", ByteArrayTag.class).get(1).data[anvilIndex]);
        assertEquals((byte) 0b1000_0001, compoundTag.getList("DataHyper", ByteArrayTag.class).get(2).data[anvilIndex]);
        compoundTag.remove("ContentVersion");
        section = new ChunkSection(compoundTag);
        section.delayPaletteUpdates();
        assertEquals(BlockID.COBBLE_WALL, section.getBlockId(x, y, z));
        assertEquals(expected, section.getBlockData(x, y, z));
    }
}
