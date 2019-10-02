package cn.nukkit.block;

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
