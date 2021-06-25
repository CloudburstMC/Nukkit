package cn.nukkit.level.format.updater;

import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.format.anvil.ChunkSection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.powernukkit.tests.junit.jupiter.PowerNukkitExtension;

import static cn.nukkit.block.BlockID.LEAVES2;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(PowerNukkitExtension.class)
class NewLeafUpdaterTest {
    final ChunkSection section = new ChunkSection(0);
    final int x = 5;
    final int y = 6;
    final int z = 7;
    final NewLeafUpdater updater = new NewLeafUpdater(section); 
    final BlockState acacia = BlockState.of(LEAVES2);
    final BlockState darkOak = BlockState.of(LEAVES2, 1);
    BlockState original;
    
    void setOriginal(BlockState original) {
        this.original = original;
        section.setBlockState(x, y, z, original);
    }
    
    void check(BlockState expected, boolean change) {
        assertEquals(change, updater.update(0, 0, 0, x, y, z, original));
        assertEquals(expected, section.getBlockState(x, y, z, 0));
    }
    
    @BeforeEach
    void configure() {
        updater.setForceOldSystem(false);
        section.delayPaletteUpdates();
    }
    
    @Test
    void simpleAcacia() {
        setOriginal(acacia);
        check(acacia, false);
    }

    @Test
    void simpleDarkOak() {
        setOriginal(darkOak);
        check(darkOak, false);
    }

    @Test
    void acaciaOldPersistent() {
        updater.setForceOldSystem(true);
        setOriginal(acacia.withData(0b0100));
        check(acacia.withData(0b0100), false);
    }

    @Test
    void acaciaNewPersistent() {
        setOriginal(acacia.withData(0b0100));
        check(acacia.withData(0b0100), false);
    }

    @Test
    void acaciaOldCheckDecay() {
        setOriginal(acacia.withData(0b0010));
        check(acacia.withData(0b1000), true);
    }

    @Test
    void acaciaNewCheckDecay() {
        setOriginal(acacia.withData(0b1000));
        check(acacia.withData(0b1000), false);
    }
    
    @Test
    void acaciaOldPersistentOldCheckDecay() {
        setOriginal(acacia.withData(0b0110));
        check(acacia.withData(0b1100), true);
    }


    @Test
    void darkOakOldPersistentOldCheckDecay() {
        setOriginal(darkOak.withData(0b0111));
        check(darkOak.withData(0b1101), true);
    }

    @Test
    void darkOakOldPersistent() {
        updater.setForceOldSystem(true);
        setOriginal(darkOak.withData(0b0101));
        check(darkOak.withData(0b0101), false);
    }

    @Test
    void darkOakNewPersistent() {
        setOriginal(darkOak.withData(0b0101));
        check(darkOak.withData(0b0101), false);
    }

    @Test
    void darkOakOldCheckDecay() {
        setOriginal(darkOak.withData(0b0011));
        check(darkOak.withData(0b1001), true);
    }

    @Test
    void darkOakNewCheckDecay() {
        setOriginal(darkOak.withData(0b1001));
        check(darkOak.withData(0b1001), false);
    }
    
    @Test
    void issue482() { // https://github.com/PowerNukkit/PowerNukkit/issues/482
        setOriginal(darkOak.withData(0b1011));
        check(darkOak.withData(0b1001), true);
    }
}
