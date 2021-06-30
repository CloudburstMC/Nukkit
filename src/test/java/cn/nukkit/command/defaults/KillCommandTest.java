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

package cn.nukkit.command.defaults;

import cn.nukkit.Server;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.mob.EntityCreeper;
import cn.nukkit.entity.passive.EntityRabbit;
import cn.nukkit.entity.passive.EntitySkeletonHorse;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.generic.BaseFullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.powernukkit.tests.api.MockLevel;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author joserobjr
 * @since 2021-06-29
 */
@ExtendWith(PowerNukkitExtension.class)
class KillCommandTest {
    @MockLevel(name = "world")
    Level level;
    Server server;
    
    EntityCreeper creeper;
    EntitySkeletonHorse skeleton;
    EntityRabbit rabbit;
    
    @Mock
    CommandSender sender;
    
    @Captor
    ArgumentCaptor<TranslationContainer> messageCaptor;
    
    KillCommand command;
    
    @BeforeEach
    void setUp() {
        server = level.getServer();
        CompoundTag defaultNBT = Entity.getDefaultNBT(new Vector3(0, 64, 0));
        level.setBlockStateAt(0, 63, 0, BlockState.of(BlockID.STONE));
        BaseFullChunk chunk = level.getChunk(0, 0);
        creeper = new EntityCreeper(chunk, defaultNBT);
        skeleton = new EntitySkeletonHorse(chunk, defaultNBT);
        rabbit = new EntityRabbit(chunk, defaultNBT);
        
        creeper.setNameTag("      ");
        skeleton.setNameTag(TextFormat.RED+" ");
        rabbit.setNameTag(TextFormat.BOLD+""+TextFormat.RED);
        command = new KillCommand("kill");
    }

    @Test
    void execute() {
        when(sender.getServer()).thenReturn(server);
        when(sender.hasPermission(anyString())).thenReturn(true);
        command.execute(sender, "kill", new String[]{"@e"});
        verify(sender).sendMessage(messageCaptor.capture());
        TranslationContainer translationContainer = messageCaptor.getValue();
        assertEquals("commands.kill.successful", translationContainer.getText());
        assertEquals(1, translationContainer.getParameters().length);
        
        Set<String> entities =  new HashSet<>(Arrays.asList(translationContainer.getParameter(0).split(", ")));
        assertTrue(entities.contains("Creeper"));
        assertTrue(entities.contains("Skeleton Horse"));
        assertTrue(entities.contains("Rabbit"));
    }
}
