

package cn.nukkit.blockentity;

import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

import cn.nukkit.blockentity.BlockEntity;

public class BlockEntityObserver extends BlockEntitySpawnable {

    private int runtimeId;

    public BlockEntityObserver(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    protected void readSaveData(CompoundTag nbt){

            this.runtimeId = nbt.getInt("runtimeId");
              
    }


    public void initBlockEntity(){

    }

    public int getSideRuntimeId(){
        return this.runtimeId;
    }

    public void setSideRuntimeId(int runtimeId){
        this.runtimeId = runtimeId;
    }

    @Override
    public boolean isBlockEntityValid() {
        return false;
    }


}