package cn.nukkit.test.answer;

import cn.nukkit.block.Block;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.Level;
import lombok.RequiredArgsConstructor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 * @author joserobjr
 */
@RequiredArgsConstructor
public class AnswerPositionedBlock implements Answer<Block> {
    private final BlockState state;
    
    @Override
    public Block answer(InvocationOnMock invocationOnMock) {
        Level level = (Level) invocationOnMock.getMock();
        Object[] arguments = invocationOnMock.getArguments();
        int x = (Integer) arguments[0];
        int y = (Integer) arguments[1];
        int z = (Integer) arguments[2];
        int layer = (Integer) arguments[3];
        return state.getBlock(level, x, y, z, layer);
    }
}
