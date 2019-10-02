package cn.nukkit.blockentity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockCommand;
import cn.nukkit.block.BlockCommandChain;
import cn.nukkit.block.BlockCommandImpulse;
import cn.nukkit.block.BlockCommandRepeating;
import cn.nukkit.block.BlockMeta;
import cn.nukkit.block.BlockRedstoneComparator;
import cn.nukkit.event.server.ServerCommandEvent;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.scheduler.TaskHandler;
import co.aikar.timings.Timings;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class BlockEntityCommand extends BlockEntitySpawnable {

    // TODO: make enum
    private static String COMMAND_BLOCK_REDSTONE_ALWAYS_ACTIVE = "Always Active";
    private static String COMMAND_BLOCK_REDSTONE_NEEDS_REDSTONE = "Needs Redstone";

    public static final List<String> REDSTONE_OPTIONS = new ArrayList<String>() {
        {
            add(COMMAND_BLOCK_REDSTONE_NEEDS_REDSTONE);
            add(COMMAND_BLOCK_REDSTONE_ALWAYS_ACTIVE);
        }
    };

    private BlockEntityCommandFormWindow formWindow;

    private boolean powered;
    private boolean lastExecutionResult;
    private int outputSignal;

    public BlockEntityCommand(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.powered = false;
        this.lastExecutionResult = false;
        this.outputSignal = -1;
    }

    public boolean isPowered() {
        return this.powered;
    }

    public void setPowered(boolean powered) {
        this.powered = powered;
    }

    public String getCommand() {
        return this.namedTag.getString("Command");
    }

    public void setCommand(String command) {
        this.namedTag.putString("Command", command);
    }

    public int getType() {
        return this.namedTag.getInt("type");
    }

    public void setType(int type) {
        this.namedTag.putInt("type", type);
    }

    public int getRedstone() {
        return this.namedTag.getInt("redstone");
    }

    public void setRedstone(int redstone) {
        this.namedTag.putInt("redstone", redstone);
    }

    public boolean needsRedstone() {
        return (getRedstone() == 0);
    }

    public boolean isAlwaysActive() {
        return (getRedstone() == 1);
    }

    public int getConditional() {
        return this.namedTag.getInt("conditional");
    }

    public boolean isUnconditional() {
        return (this.getConditional() == BlockEntityCommandConditionalOption.UNCONDITIONAL.ordinal());
    }

    public void setConditional(int conditional) {
        this.namedTag.putInt("conditional", conditional);
    }

    public BlockEntityCommandFormWindow getFormWindow() {
        return this.formWindow;
    }

    /**
     * NBT data structure for command blocks
     *
     * id: Block entity ID x: X coordinate of the block entity. y: Y coordinate of the block entity. z: Z coordinate of the block entity. keepPacked: 0 for regular block entities. 1 indicates something else[needs testing]. CustomName: Optional. The name of this command block in JSON text component, replacing the usual '@' when using commands such as /say and /tell. Command: The command to issue to the server. SuccessCount: Represents the strength of the analog signal output by redstone comparators attached
     * to this command block. Only updated when the command block is activated with a redstone signal. LastOutput: The last line of output generated by the command block. Still stored even if the gamerule commandBlockOutput is false. Appears in the GUI of the block when right-clicked, and includes a timestamp of when the output was produced. TrackOutput: 1 or 0 (true/false) - Determines whether or not the LastOutput will be stored. Can be toggled in the GUI by clicking a button near the "Previous Output"
     * textbox. Caption on the button indicates current state: "O" if true, "X" if false. powered: 1 or 0 (true/false) - States whether or not the command block is powered by redstone or not. auto: 1 or 0 (true/false) - Allows to activate the command without the requirement of a redstone signal. conditionMet: 1 or 0 (true/false) - Indicates whether a conditional command block had its condition met when last activated. True if not a conditional command block. UpdateLastExecution: 1 or 0 (true/false) -
     * Defaults to true. If set to false, loops can be created where the same command block can run multiple times in one tick. LastExecution: stores the tick a chain command block was last executed in.
     */

    @Override
    public CompoundTag getSpawnCompound() {
        log.debug("entity[{}].getSpawnCompound: {} {} {} {}", this.getId(), this.namedTag.getString("Command"), this.namedTag.getInt("type"), this.namedTag.getInt("conditional"), this.namedTag.getInt("auto"));
        CompoundTag nbt = new CompoundTag().putString("id", BlockEntity.COMMAND_BLOCK).putInt("x", (int) this.x).putInt("y", (int) this.y).putInt("z", (int) this.z);

        if (this.namedTag.contains("Command")) {
            nbt.putString("Command", this.namedTag.getString("Command"));
        }

        if (this.namedTag.contains("type")) {
            nbt.putInt("type", this.namedTag.getInt("type"));
        }

        if (this.namedTag.contains("conditional")) {
            nbt.putInt("conditional", this.namedTag.getInt("conditional"));
        }

        if (this.namedTag.contains("auto")) {
            nbt.putInt("auto", this.namedTag.getInt("auto"));
        }

        return nbt;
    }

    @Override
    public boolean onUpdate() {

        if (closed) {
            return false;
        }

        log.debug("entity[{}].onUpdate: {} {}", this.getId(), this.namedTag.getString("Command"), this.namedTag.getInt("type"), this.namedTag.getInt("conditional"), this.namedTag.getInt("auto"));

        lastUpdate = System.currentTimeMillis();

        if (this.getBlock() instanceof BlockCommand) {
            if (this.isAlwaysActive() || (this.needsRedstone() && this.isPowered())) {
                if (this.getBlock() instanceof BlockCommandImpulse) {
                    return false;
                } else if (this.getBlock() instanceof BlockCommandRepeating) {
                    runCommand();
                    return true;
                } else if (this.getBlock() instanceof BlockCommandChain) {
                    runCommand();
                    return false;
                }
            }
        }
        return false;
    }

    @Override
    protected void initBlockEntity() {
        log.info("entity[{}].initBlockEntity: {} {} {} {}", this.getId(), this.namedTag.getString("Command"), this.namedTag.getInt("type"), this.namedTag.getInt("conditional"), this.namedTag.getInt("auto"));

        if (this.formWindow == null) {
            this.formWindow = new BlockEntityCommandFormWindow(this.getId());
        }

        if (this.namedTag.contains("Command")) {
            this.formWindow.setCommand(this.namedTag.getString("Command"));
        }

        if (this.namedTag.contains("type")) {
            this.formWindow.setType(this.namedTag.getInt("type"));
        }

        if (this.namedTag.contains("conditional")) {
            this.formWindow.setConditional(this.namedTag.getInt("conditional"));
        }

        if (this.namedTag.contains("auto")) {
            this.formWindow.setRedstone(this.namedTag.getInt("auto"));
        }

        if (this.level.isBlockPowered(this)) {
            this.setPowered(true);
        }

        this.scheduleUpdate();

        super.initBlockEntity();
    }

    @Override
    public boolean isBlockEntityValid() {
        return getBlock().getId() == Block.IMPULSE_COMMAND_BLOCK || getBlock().getId() == Block.CHAIN_COMMAND_BLOCK || getBlock().getId() == Block.REPEATING_COMMAND_BLOCK;
    }

    public void runCommand() {

        String command = getCommand();

        if (command != null && command.length() > 2) {
            Timings.serverCommandTimer.startTiming();

            ServerCommandEvent event = new ServerCommandEvent(server.getConsoleSender(), command);
            if (server.getPluginManager() != null) {
                server.getPluginManager().callEvent(event);
            }
            if (!event.isCancelled()) {
                BlockEntityCommandTask task = new BlockEntityCommandTask(this);
                Server.getInstance().getScheduler().scheduleTask(task);
            }

            Timings.serverCommandTimer.stopTiming();
        }
    }

    public void setLastExecutionResult(boolean result) {
        this.lastExecutionResult = result;
    }

    public boolean getLastExecutionResult() {
        return this.lastExecutionResult;
    }

    public void setOutputSignal(int signal) {
        if (signal < 0) {
            this.outputSignal = -1;
        } else if (signal >= 0 && signal < 15) {
            this.outputSignal = signal;
        } else {
            this.outputSignal = 15;
        }
    }

    public int getOutputSignal() {
        if (this.isAlwaysActive() || (this.needsRedstone() && this.isPowered())) {
            if (this.isUnconditional() || (!this.isUnconditional() && this.lastExecutionResult)) {
                return this.outputSignal;
            } else
                return 0;
        } else {
            return 0;
        }
    }

    @Override
    public void onBreak() {
        log.info("Breaking a command block entity...");
        BlockMeta block = (BlockMeta) getBlock();
        log.info("Directional index: " + block.getDamage());

        Position outBlockPosition = getSide(BlockFace.fromIndex(block.getDamage() & 0x7));
        BlockEntity outBlockEntity = getLevel().getBlockEntity(outBlockPosition);

        if (outBlockEntity instanceof BlockEntityComparator) {
            BlockRedstoneComparator comparatorBlock = (BlockRedstoneComparator) getLevel().getBlock(outBlockPosition);
            comparatorBlock.updateState();
        }
        super.onBreak();
    }

    public BlockCommand getBlockCommand() {
        return ((BlockCommand) this.getLevelBlock());
    }

}
