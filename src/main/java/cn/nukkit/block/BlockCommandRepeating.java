package cn.nukkit.block;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class BlockCommandRepeating extends BlockCommand {

    @Override
    public String getName() {
        return "Repeating Command Block";
    }

    @Override
    public int getId() {
        return REPEATING_COMMAND_BLOCK;
    }

    @Override
    public void reactOnPeak() {
        this.getEntity().scheduleUpdate();
    }

    public void reactOnDip() {
        this.getEntity().scheduleUpdate();
    }

}
