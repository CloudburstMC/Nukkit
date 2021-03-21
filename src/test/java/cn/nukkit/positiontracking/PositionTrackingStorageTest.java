package cn.nukkit.positiontracking;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.OptionalInt;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author joserobjr
 */
class PositionTrackingStorageTest {
    
    File temp;
    PositionTrackingStorage storage;
    
    @BeforeEach
    void setUp() throws IOException {
        temp = File.createTempFile("PositionTrackingStorageTest_", ".pnt");
        temp.deleteOnExit();
    }

    @AfterEach
    void tearDown() throws IOException {
        if (storage != null) {
            storage.close();
        }
        assertTrue(!temp.isFile() || temp.delete(), "Failed to delete the temporary file "+temp);
    }

    @Test
    void constructor() throws IOException {
        try(PositionTrackingStorage storage = new PositionTrackingStorage(1, temp, 2)) {
            assertEquals(1, storage.getStartingHandler());
            assertEquals(2, storage.getMaxHandler());
        }
        assertEquals(10 + 4 + 4 + 4 + (1 + 8 + 4 + 8 + 8 + 8) * 2 + 4 + (8 + 4) * 15, temp.length());
        try (PositionTrackingStorage storage = new PositionTrackingStorage(1, temp)) {
            assertEquals(1, storage.getStartingHandler());
            assertEquals(2, storage.getMaxHandler());
        }
        assertThrows(IllegalArgumentException.class, ()-> {
            PositionTrackingStorage storage = new PositionTrackingStorage(2, temp);
            storage.close();
        });
        assertTrue(temp.delete());
        assertThrows(IllegalArgumentException.class, ()-> {
            PositionTrackingStorage storage = new PositionTrackingStorage(0, temp);
            storage.close();
        });
        assertThrows(IllegalArgumentException.class, ()-> {
            PositionTrackingStorage storage = new PositionTrackingStorage(-1, temp);
            storage.close();
        });
    }

    @Test
    void close() throws IOException {
        storage = new PositionTrackingStorage(1, temp);
        storage.close();
        assertThrows(IOException.class, ()-> storage.addNewPosition(new PositionTracking("x", 1, 2, 3)));
        assertTrue(temp.delete());
    }

    @Test
    void addNewPosition() throws IOException {
        storage = new PositionTrackingStorage(10, temp);
        PositionTracking pos1 = new PositionTracking("test world", 2.33, 4.55, 6.77);
        OptionalInt result = storage.addNewPosition(pos1);
        assertTrue(result.isPresent());
        assertEquals(10, result.getAsInt());
        result = storage.addNewPosition(pos1);
        assertTrue(result.isPresent());
        assertEquals(11, result.getAsInt());
        
        PositionTracking pos2 = new PositionTracking("test world", 2, 4, 6);
        result = storage.addNewPosition(pos2);
        assertTrue(result.isPresent());
        assertEquals(12, result.getAsInt());
        result = storage.addNewPosition(pos2);
        assertTrue(result.isPresent());
        assertEquals(13, result.getAsInt());

        result = storage.addNewPosition(pos1, false);
        assertTrue(result.isPresent());
        assertEquals(14, result.getAsInt());
        result = storage.addNewPosition(pos1, false);
        assertTrue(result.isPresent());
        assertEquals(15, result.getAsInt());

        result = storage.addNewPosition(pos2, false);
        assertTrue(result.isPresent());
        assertEquals(16, result.getAsInt());
        result = storage.addNewPosition(pos2, false);
        assertTrue(result.isPresent());
        assertEquals(17, result.getAsInt());

        assertEquals(pos1, storage.getPosition(10));
        assertEquals(pos1, storage.getPosition(11));
        assertEquals(pos1, storage.getPosition(10, false));
        assertEquals(pos1, storage.getPosition(11, false));
        assertEquals(pos2, storage.getPosition(12));
        assertEquals(pos2, storage.getPosition(13));
        assertEquals(pos2, storage.getPosition(12, false));
        assertEquals(pos2, storage.getPosition(13, false));
        
        assertNull(storage.getPosition(14));
        assertNull(storage.getPosition(15));
        assertNull(storage.getPosition(16));
        assertNull(storage.getPosition(17));
        assertNull(storage.getPosition(14, true));
        assertNull(storage.getPosition(15, true));
        assertNull(storage.getPosition(16, true));
        assertNull(storage.getPosition(17, true));

        assertEquals(pos1, storage.getPosition(14, false));
        assertEquals(pos1, storage.getPosition(15, false));
        assertEquals(pos2, storage.getPosition(16, false));
        assertEquals(pos2, storage.getPosition(17, false));
        
        assertTrue(storage.hasPosition(10));
        assertTrue(storage.hasPosition(11));
        assertTrue(storage.hasPosition(12));
        assertTrue(storage.hasPosition(13));
        assertFalse(storage.hasPosition(14));
        assertFalse(storage.hasPosition(15));
        assertFalse(storage.hasPosition(16));
        assertFalse(storage.hasPosition(17));
        assertTrue(storage.hasPosition(14, false));
        assertTrue(storage.hasPosition(15, false));
        assertTrue(storage.hasPosition(16, false));
        assertTrue(storage.hasPosition(17, false));
        assertFalse(storage.hasPosition(18, false));
        
        assertTrue(storage.isEnabled(10));
        assertTrue(storage.isEnabled(11));
        assertTrue(storage.isEnabled(12));
        assertTrue(storage.isEnabled(13));
        assertFalse(storage.isEnabled(14));
        assertFalse(storage.isEnabled(15));
        assertFalse(storage.isEnabled(16));
        assertFalse(storage.isEnabled(17));
        assertFalse(storage.isEnabled(18));
        
        storage.close();
        storage = new PositionTrackingStorage(10, temp);

        assertEquals(pos1, storage.getPosition(10));
        assertEquals(pos1, storage.getPosition(11));
        assertEquals(pos1, storage.getPosition(10, false));
        assertEquals(pos1, storage.getPosition(11, false));
        assertEquals(pos2, storage.getPosition(12));
        assertEquals(pos2, storage.getPosition(13));
        assertEquals(pos2, storage.getPosition(12, false));
        assertEquals(pos2, storage.getPosition(13, false));

        assertNull(storage.getPosition(14));
        assertNull(storage.getPosition(15));
        assertNull(storage.getPosition(16));
        assertNull(storage.getPosition(17));
        assertNull(storage.getPosition(14, true));
        assertNull(storage.getPosition(15, true));
        assertNull(storage.getPosition(16, true));
        assertNull(storage.getPosition(17, true));

        assertEquals(pos1, storage.getPosition(14, false));
        assertEquals(pos1, storage.getPosition(15, false));
        assertEquals(pos2, storage.getPosition(16, false));
        assertEquals(pos2, storage.getPosition(17, false));

        assertTrue(storage.hasPosition(10));
        assertTrue(storage.hasPosition(11));
        assertTrue(storage.hasPosition(12));
        assertTrue(storage.hasPosition(13));
        assertFalse(storage.hasPosition(14));
        assertFalse(storage.hasPosition(15));
        assertFalse(storage.hasPosition(16));
        assertFalse(storage.hasPosition(17));
        assertTrue(storage.hasPosition(14, false));
        assertTrue(storage.hasPosition(15, false));
        assertTrue(storage.hasPosition(16, false));
        assertTrue(storage.hasPosition(17, false));
        assertFalse(storage.hasPosition(18, false));

        assertTrue(storage.isEnabled(10));
        assertTrue(storage.isEnabled(11));
        assertTrue(storage.isEnabled(12));
        assertTrue(storage.isEnabled(13));
        assertFalse(storage.isEnabled(14));
        assertFalse(storage.isEnabled(15));
        assertFalse(storage.isEnabled(16));
        assertFalse(storage.isEnabled(17));
        assertFalse(storage.isEnabled(18));
    }
    
    @Test
    void sizeLimit() throws IOException {
        PositionTracking pos1 = new PositionTracking("pos1", 1, 2, 3);
        PositionTracking pos2 = new PositionTracking("pos2", 4, 5, 6);
        PositionTracking pos3 = new PositionTracking("pos3", 7, 8, 9);
        storage = new PositionTrackingStorage(5, temp, 3);
        OptionalInt result = storage.addOrReusePosition(pos1);
        assertTrue(result.isPresent());
        assertEquals(5, result.getAsInt());
        assertEquals(pos1, storage.getPosition(5));

        result = storage.addOrReusePosition(pos1);
        assertTrue(result.isPresent());
        assertEquals(5, result.getAsInt());
        assertEquals(pos1, storage.getPosition(5));

        result = storage.addNewPosition(pos1);
        assertTrue(result.isPresent());
        assertEquals(6, result.getAsInt());
        assertEquals(pos1, storage.getPosition(6));
        assertEquals(pos1, storage.getPosition(5));

        result = storage.addOrReusePosition(pos1);
        assertTrue(result.isPresent());
        assertTrue(result.getAsInt() == 6 || result.getAsInt() == 5);
        assertEquals(pos1, storage.getPosition(6));
        assertEquals(pos1, storage.getPosition(5));
        
        // 2

        result = storage.addOrReusePosition(pos2);
        assertTrue(result.isPresent());
        assertEquals(7, result.getAsInt());
        assertEquals(pos2, storage.getPosition(7));

        result = storage.addOrReusePosition(pos2);
        assertTrue(result.isPresent());
        assertEquals(7, result.getAsInt());
        assertEquals(pos2, storage.getPosition(7));

        result = storage.addNewPosition(pos2);
        assertFalse(result.isPresent());
        
        result = storage.addNewPosition(pos1);
        assertFalse(result.isPresent());

        result = storage.addOrReusePosition(pos1);
        assertTrue(result.isPresent());
        assertTrue(result.getAsInt() == 6 || result.getAsInt() == 5);
        assertEquals(pos1, storage.getPosition(6));
        assertEquals(pos1, storage.getPosition(5));

        result = storage.addOrReusePosition(pos2);
        assertTrue(result.isPresent());
        assertEquals(7, result.getAsInt());
        assertEquals(pos2, storage.getPosition(7));
        
        // 3

        result = storage.addNewPosition(pos3);
        assertFalse(result.isPresent());

        result = storage.addOrReusePosition(pos3);
        assertFalse(result.isPresent());
    }

    @Test
    void findTrackingHandler() throws IOException {
        PositionTracking pos1 = new PositionTracking("pos1", 1, 2, 3);
        PositionTracking pos2 = new PositionTracking("pos2", 4, 5, 6);
        PositionTracking pos3 = new PositionTracking("pos3", 7, 8, 9);
        PositionTracking pos4 = new PositionTracking("pos4", 10, 11, 12);

        IntList handlers2 = new IntArrayList();
        IntList handlers3 = new IntArrayList();
        
        storage = new PositionTrackingStorage(300, temp);
        int handler1 = storage.addNewPosition(pos1).orElseThrow(IOException::new);
        handlers3.add(storage.addNewPosition(pos3).orElseThrow(IOException::new));
        int disabled = storage.addNewPosition(pos2, false).orElseThrow(IOException::new);
        handlers3.add(storage.addNewPosition(pos3).orElseThrow(IOException::new));
        handlers2.add(storage.addNewPosition(pos2).orElseThrow(IOException::new));
        handlers3.add(storage.addNewPosition(pos3).orElseThrow(IOException::new));
        handlers2.add(storage.addNewPosition(pos2).orElseThrow(IOException::new));
        int handler4 = storage.addNewPosition(pos4, false).orElseThrow(IOException::new);
        
        assertEquals(OptionalInt.of(handler1), storage.findTrackingHandler(pos1));
        assertEquals(handlers2, storage.findTrackingHandlers(pos2));
        assertEquals(handlers3, storage.findTrackingHandlers(pos3));
        handlers2.add(0, disabled);
        assertEquals(handlers2, storage.findTrackingHandlers(pos2, false));
        assertEquals(new IntArrayList(), storage.findTrackingHandlers(pos4, true));
        assertEquals(Collections.singletonList(handler1), storage.findTrackingHandlers(pos1));
        assertEquals(Collections.singletonList(handler4), storage.findTrackingHandlers(pos4, false));
        
        assertTrue(handlers2.contains(storage.findTrackingHandler(pos2).orElse(0)));
        assertTrue(handlers3.contains(storage.findTrackingHandler(pos3).orElse(0)));
    }

    @Test
    void invalidateHandler() throws IOException {
        storage = new PositionTrackingStorage(3, temp);
        PositionTracking pos1 = new PositionTracking("pos1", 1, 2, 3);
        assertTrue(storage.addNewPosition(pos1).isPresent());
        assertTrue(storage.isEnabled(3));
        assertTrue(storage.hasPosition(3));
        assertTrue(storage.hasPosition(3, true));
        assertTrue(storage.hasPosition(3, true));
        
        assertFalse(storage.setEnabled(3, true));
        assertTrue(storage.setEnabled(3, false));
        assertFalse(storage.isEnabled(3));
        assertFalse(storage.hasPosition(3));
        assertFalse(storage.hasPosition(3, true));
        assertTrue(storage.hasPosition(3, false));
        
        assertTrue(storage.setEnabled(3, true));
        assertTrue(storage.isEnabled(3));
        assertFalse(storage.setEnabled(3, true));
        assertTrue(storage.isEnabled(3));
        assertTrue(storage.hasPosition(3));
        assertTrue(storage.hasPosition(3, true));
        assertTrue(storage.hasPosition(3, true));
        
        storage.invalidateHandler(3);
        assertFalse(storage.isEnabled(3));
        assertFalse(storage.setEnabled(3, true));
        assertFalse(storage.setEnabled(3, false));
        assertFalse(storage.hasPosition(3));
        assertFalse(storage.hasPosition(3, true));
        assertFalse(storage.hasPosition(3, false));
        storage.close();

        long size = temp.length();
        
        storage = new PositionTrackingStorage(3, temp);
        PositionTracking pos2 = new PositionTracking("pos2", 3, 2, 3);
        assertEquals(4, storage.addOrReusePosition(pos2).orElse(0));
        storage.close();
        
        assertEquals(size, temp.length());

        storage = new PositionTrackingStorage(3, temp);
        assertEquals(4, storage.addOrReusePosition(pos2).orElse(0));
        assertEquals(5, storage.addOrReusePosition(pos1).orElse(0));
    }
}
