package cn.nukkit.block;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class BlockCommandImpulse extends BlockCommand {

    @Override
    public String getName() {
        return "Impulse Command Block";
    }

    @Override
    public int getId() {
        return IMPULSE_COMMAND_BLOCK;
    }

    @Override
    public void reactOnPeak() {
        super.reactOnPeak();
        if (getEntity().needsRedstone()) {
            getEntity().runCommand();
        }
    }

}
