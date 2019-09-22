package cn.nukkit.block;

/**
 * @author nmaster
 */
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
