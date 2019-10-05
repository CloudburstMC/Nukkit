package cn.nukkit.command.data;

import lombok.ToString;

import java.util.List;

/**
 * @author CreeperFace
 */
@ToString
public class CommandEnum {

    private String name;
    private List<String> values;

    public CommandEnum(String name, List<String> values) {
        this.name = name;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public List<String> getValues() {
        return values;
    }

    public int hashCode() {
        return name.hashCode();
    }
}
