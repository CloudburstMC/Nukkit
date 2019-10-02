package cn.nukkit.event.builtin;

import cn.nukkit.block.BlockCommand;
import cn.nukkit.block.BlockCommandChain;
import cn.nukkit.block.BlockCommandImpulse;
import cn.nukkit.block.BlockCommandRepeating;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityCommand;
import cn.nukkit.blockentity.BlockEntityCommandFormWindow;
import cn.nukkit.blockentity.BlockEntityCommandType;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.math.Vector3;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class PlayerFormRespondedEventListener implements Listener {

    @EventHandler(
                    ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerFormResponded(PlayerFormRespondedEvent event) {

        FormWindow form = event.getWindow();

        if (form instanceof BlockEntityCommandFormWindow) {
            BlockEntityCommandFormWindow commandForm = (BlockEntityCommandFormWindow) form;
            FormResponseCustom response = (FormResponseCustom) event.getResponse();

            if ((response != null) && (response.getResponses().size() > 0)) {

                BlockEntity entity = event.getPlayer().getLevel().getBlockEntityById(commandForm.getId());

                if (entity instanceof BlockEntityCommand) {
                    BlockEntityCommand commandEntity = (BlockEntityCommand) entity;

                    if (response.getInputResponse(0) != null) {
                        String command = response.getInputResponse(0);
                        log.debug("Setting command to {}", command);
                        commandEntity.setCommand(command);
                    }

                    if (response.getDropdownResponse(1) != null) {
                        int type = response.getDropdownResponse(1).getElementID();
                        log.debug("Setting type to {}", type);
                        commandEntity.setType(type);

                        Vector3 pos = commandEntity.getLocation().asBlockVector3().asVector3();
                        Vector3 dir = commandEntity.getBlock().getLocation().getDirectionVector().normalize();

                        log.debug("x: {}, y: {}, z: {}", pos.x, pos.y, pos.z);
                        log.debug("dx: {}, dy: {}, dz: {}",dir.x, dir.y, dir.z);

                        log.info("Command block with damage: {}", commandEntity.getBlockCommand().getDamage());

                        BlockCommand commandBlock;

                        switch (type) {
                        case 0:
                            commandBlock = new BlockCommandImpulse();
                            break;
                        case 1:
                            commandBlock = new BlockCommandChain();
                            break;
                        case 2:
                            commandBlock = new BlockCommandRepeating();
                            break;
                        default:
                            commandBlock = new BlockCommandImpulse();
                            break;
                        }

                        int damage = ((BlockEntityCommand) entity).getBlockCommand().getDamage();
                        commandBlock.setDamage(damage);

                        commandEntity.getLevel().setBlock(pos, commandBlock);
                    }

                    if (response.getDropdownResponse(2) != null) {
                        int redstone = response.getDropdownResponse(2).getElementID();
                        
                        commandEntity.setRedstone(redstone);
                    }

                    if (response.getDropdownResponse(3) != null) {
                        int conditional = response.getDropdownResponse(3).getElementID();
                        
                        commandEntity.setConditional(conditional);

                        BlockCommand block = commandEntity.getBlockCommand();

                        int oldDirection = commandEntity.getBlockCommand().getDamage();
                        int newDirection = (oldDirection & 0x7) | (conditional << 3);

                        block.setDamage(newDirection);

                        commandEntity.getLevel().setBlock(block.getLocation().asBlockVector3().asVector3(), block);

                    }

                    log.debug("Command block entity set... command:{}, type:{}, conditional: {}, redstone:{} by player {}", 
                    		commandEntity.getCommand(), 
                    		commandEntity.getType(), 
                    		commandEntity.getConditional(), 
                    		commandEntity.getRedstone(), 
                    		event.getPlayer().getName());
                    
                    event.getPlayer().getLevel().save();

                    if (commandEntity.getType() == BlockEntityCommandType.COMMAND_BLOCK_TYPE_REPEAT.ordinal()) {
                        commandEntity.scheduleUpdate();
                    }
                }
            }
        }
    }
}
