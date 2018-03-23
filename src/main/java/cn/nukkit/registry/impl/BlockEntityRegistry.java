package cn.nukkit.registry.impl;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBeacon;
import cn.nukkit.blockentity.BlockEntityBed;
import cn.nukkit.blockentity.BlockEntityBrewingStand;
import cn.nukkit.blockentity.BlockEntityCauldron;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.blockentity.BlockEntityComparator;
import cn.nukkit.blockentity.BlockEntityEnchantTable;
import cn.nukkit.blockentity.BlockEntityEnderChest;
import cn.nukkit.blockentity.BlockEntityFlowerPot;
import cn.nukkit.blockentity.BlockEntityFurnace;
import cn.nukkit.blockentity.BlockEntityHopper;
import cn.nukkit.blockentity.BlockEntityItemFrame;
import cn.nukkit.blockentity.BlockEntityJukebox;
import cn.nukkit.blockentity.BlockEntityPistonArm;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.blockentity.BlockEntitySkull;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.registry.AbstractRegistry;
import cn.nukkit.registry.RegistryType;
import cn.nukkit.registry.function.BiObjectObjectFunction;
import cn.nukkit.registry.function.IntObjectFunction;

public final class BlockEntityRegistry extends AbstractRegistry<BlockEntity, BiObjectObjectFunction<FullChunk, CompoundTag, BlockEntity>> implements BlockID {
    public static final BlockEntityRegistry INSTANCE = new BlockEntityRegistry();

    private BlockEntityRegistry() {
        super(RegistryType.BLOCK_ENTITY);
    }

    @Override
    protected void init() {
        register("furnace", BlockEntityFurnace::new, BlockEntityFurnace.class);
        register("chest", BlockEntityChest::new, BlockEntityChest.class);
        register("sign", BlockEntitySign::new, BlockEntitySign.class);
        register("enchantment_table", BlockEntityEnchantTable::new, BlockEntityEnchantTable.class);
        register("skull", BlockEntitySkull::new, BlockEntitySkull.class);
        register("flower_pot", BlockEntityFlowerPot::new, BlockEntityFlowerPot.class);
        register("brewing_stand", BlockEntityBrewingStand::new, BlockEntityBrewingStand.class);
        register("item_frame", BlockEntityItemFrame::new, BlockEntityItemFrame.class);
        register("cauldron", BlockEntityCauldron::new, BlockEntityCauldron.class);
        register("ender_chest", BlockEntityEnderChest::new, BlockEntityEnderChest.class);
        register("beacon", BlockEntityBeacon::new, BlockEntityBeacon.class);
        register("piston_arm", BlockEntityPistonArm::new, BlockEntityPistonArm.class);
        register("comparator", BlockEntityComparator::new, BlockEntityComparator.class);
        register("hopper", BlockEntityHopper::new, BlockEntityHopper.class);
        register("bed", BlockEntityBed::new, BlockEntityBed.class);
        register("jukebox", BlockEntityJukebox::new, BlockEntityJukebox.class);
    }

    @Override
    protected BlockEntity accept(BiObjectObjectFunction<FullChunk, CompoundTag, BlockEntity> func, int i, Object... args) {
        //porktodo: check if anything else needs to be done here
        return func.accept((FullChunk) args[0], (CompoundTag) args[1]);
    }
}
