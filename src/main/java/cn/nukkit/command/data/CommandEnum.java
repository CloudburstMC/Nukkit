package cn.nukkit.command.data;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.List;

/**
 * @author lukeeey
 * @author CreeperFace
 */
@Data
public class CommandEnum {
    private final String name;
    private final List<String> values;

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
