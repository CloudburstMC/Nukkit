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

package cn.nukkit.entity;

import cn.nukkit.level.Level;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.format.LevelProvider;
import cn.nukkit.math.Vector3;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.powernukkit.tests.api.MockLevel;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;

/**
 * @author joserobjr
 * @since 2021-06-26
 */
@ExtendWith(PowerNukkitExtension.class)
class EntityTest {
    @MockLevel
    Level level;
    
    @Mock
    FullChunk chunk;
    
    Entity entity;

    @BeforeEach
    void setUp() {
        LevelProvider provider = level.getProvider();
        lenient().when(chunk.getProvider()).thenReturn(provider);
    }

    @ParameterizedTest
    @MethodSource("getEntityIdStream")
    void testNames(String id) {
        if (id.equals("Human") && Entity.getSaveId(id).orElse(-1) == -1) {
            return;
        }
        entity = createEntity(id);
        assertNotNull(entity, ()-> "Entity " + Entity.getSaveId(id));
        assertNotNull(entity.getOriginalName(), "Static Name");
        String staticName = entity.getOriginalName();
        assertEquals(staticName, entity.getName());
        assertFalse(entity.hasCustomName(), "Should not have custom");
        assertEquals(entity.getName(), entity.getVisibleName());
        
        if (entity instanceof EntityNameable) {
            EntityNameable nameable = (EntityNameable) entity;
            nameable.setNameTag("Customized");
            assertTrue(entity.hasCustomName(), "Should have custom");
            assertNotNull(entity.getOriginalName(), "Static name should not be null");
            assertEquals(staticName, entity.getOriginalName(), "Static name should not change");
            assertEquals("Customized", entity.getName());
            assertNotEquals(entity.getName(), entity.getOriginalName());
            
            nameable.setNameTag(" ");
            assertTrue(entity.hasCustomName());
            assertNotNull(entity.getOriginalName());
            assertEquals(" ", entity.getName());
            assertNotEquals(entity.getName(), entity.getOriginalName());
            assertEquals(entity.getOriginalName(), entity.getVisibleName());
        }
    }

    Entity createEntity(String id) {
        return Entity.createEntity(id, chunk, Entity.getDefaultNBT(new Vector3(0, 64, 0)));
    }

    static Stream<String> getEntityIdStream() {
        return Arrays.stream(Entity.getKnownEntityIds().toIntArray())
                .mapToObj(Entity::getSaveId)
                .map(Objects::requireNonNull);
    }

    @AfterEach
    void tearDown() {
        try {
            if (entity != null) {
                entity.close();
            }
        } finally {
            entity = null;
        }
    }
}
