package cn.nukkit.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.block.BlockLever;
import cn.nukkit.block.BlockUnknown;
import cn.nukkit.test.LogLevelAdjuster;
import org.apache.logging.log4j.Level;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author joserobjr
 */
class ItemBlockTest {
    LogLevelAdjuster logLevelAdjuster = new LogLevelAdjuster();
    
    @Test
    void badBlockData() {
        Item stone = Item.getBlock(BlockID.LEVER);
        assertThat(stone).isInstanceOf(ItemBlock.class);
        assertThat(stone.getBlock()).isInstanceOf(BlockLever.class);
        logLevelAdjuster.onlyNow(ItemBlock.class, Level.ERROR, ()-> 
                stone.setDamage(1000)
        );
        assertThat(stone.getBlock()).isInstanceOf(BlockUnknown.class);
        stone.setDamage(0);
        assertThat(stone.getBlock()).isInstanceOf(BlockLever.class);
    }

    @BeforeAll
    static void beforeAll() {
        Block.init();
        Item.init();
    }

    @AfterEach
    void tearDown() {
        logLevelAdjuster.restoreLevels();
    }
}
