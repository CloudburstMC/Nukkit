package cn.nukkit.blockentity;

import java.util.ArrayList;
import java.util.Collection;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.math.BlockFace;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockCommand;
import cn.nukkit.block.BlockCommandChain;
import cn.nukkit.block.BlockRedstoneComparator;
import cn.nukkit.level.Position;
import cn.nukkit.scheduler.Task;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class BlockEntityCommandTask extends Task {

    private BlockEntityCommand entity;

    public BlockEntityCommandTask(BlockEntityCommand e) {
        this.entity = e;
    }

    @Override
    public void onRun(int currentTick) {

        if ((entity.needsRedstone() && !entity.isPowered()) && !(entity.getType() == BlockEntityCommandType.COMMAND_BLOCK_TYPE_CHAIN.ordinal())) {
            log.info("Command '{}': execution skipped - needs power, but is not powered. dont care for chain blocks", entity.getCommand());
            return;
        }

        if (!entity.isUnconditional() && !entity.getLastExecutionResult()) {
            log.info("Command '{}': execution skipped - is conditional, but previous command failed.", entity.getCommand());
            return;
        }

        String command = entity.getCommand();

        Collection<String> commands = new ArrayList<>();

        if (command.contains("@")) {
            commands = resolveAtSymbol(command);
        } else {
            commands.add(command);
        }

        for (String c : commands) {
            String strippedCommand = c.replaceAll("^/", "");

            // boolean executionResult =
            // entity.server.dispatchCommand(entity.server.getConsoleSender(),
            // strippedCommand);

            int outputSignal = entity.server.dispatchCommandWithOutputSignal(entity.server.getConsoleSender(), strippedCommand);
            log.info("Output signal from command execution: {}", outputSignal);
            entity.setOutputSignal(outputSignal);

            Position outBlockPosition = entity.getSide(BlockFace.fromIndex((entity.getBlock().getDamage() & 0x7)));
            // Position inBlockPosition =
            // entity.getSide(BlockFace.fromIndex(entity.getDirection()).getOpposite());

            BlockEntity outBlockEntity = entity.getLevel().getBlockEntity(outBlockPosition);
            // BlockEntity inBlockEntity =
            // entity.getLevel().getBlockEntity(inBlockPosition);

            if (outBlockEntity instanceof BlockEntityCommand) {
                BlockEntityCommand nextBlockCommandEntity = (BlockEntityCommand) outBlockEntity;
                nextBlockCommandEntity.setLastExecutionResult((outputSignal != -1));
                nextBlockCommandEntity.scheduleUpdate();
            } else if (outBlockEntity instanceof BlockEntityComparator) {
                BlockRedstoneComparator comparatorBlock = (BlockRedstoneComparator) entity.getLevel().getBlock(outBlockPosition);
                comparatorBlock.updateState();

            }
        }
    }

    private Collection<String> resolveAtSymbol(String symbol) {

        Collection<String> commands = new ArrayList<>();

        if (symbol.contains(" @p ")) {
            log.debug("Player by @p (nearest player to command block)");

            Player nearestPlayer = null;
            double minDistance = Double.MAX_VALUE;

            Collection<Player> onlinePlayers = entity.server.getOnlinePlayers().values();

            for (Player p : onlinePlayers) {

                final double playerDistance = p.getLocation().distance(entity.getBlock().getLocation().asBlockVector3().asVector3());

                if (playerDistance < minDistance) {
                    nearestPlayer = p;
                    minDistance = playerDistance;

                    nearestPlayer.sendPopup(" §e-§a> §fYou are closest §a<§e- \n §fdistance: §6" + minDistance);
                }
            }
            nearestPlayer.sendPopup(" §e-§a> §fYou are closest §a<§e- \n §fdistance: §6" + minDistance);
            commands.add(symbol.replace("@p", nearestPlayer.getName()));

        } else if (symbol.contains(" @a ")) {
            log.debug("Players by @a");

            for (Player p : entity.server.getOnlinePlayers().values()) {
                p.sendPopup(" §e-§a> §fYou are one of all. §a<§e-");
                commands.add(symbol.replace("@a", p.getName()));
            }

        } else if (symbol.contains(" @s ")) {
            log.debug("Player by @s (undefined for command block)");
        }

        return commands;
    }

}
