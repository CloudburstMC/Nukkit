package cn.nukkit.block;

/**
 * @author nmaster
 */
public class BlockCommandChain extends BlockCommand {

    @Override
    public String getName() {
        return "Chain Command Block";
    }

    @Override
    public int getId() {
        return CHAIN_COMMAND_BLOCK;
    }

}
