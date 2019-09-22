package cn.nukkit.block;

import lombok.extern.log4j.Log4j2;

@Log4j2
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
