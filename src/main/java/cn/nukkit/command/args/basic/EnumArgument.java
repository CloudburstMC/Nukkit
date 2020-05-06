package cn.nukkit.command.args.basic;

import cn.nukkit.command.args.BaseArgument;
import lombok.Getter;

import java.io.StringReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Getter
public class EnumArgument<T> implements BaseArgument<T> {
    private final String enumName;
    private final List<String> enumValues = new LinkedList<>();

    public EnumArgument(String enumName, String[] enumValues) {
        this.enumName = enumName;
        this.enumValues.addAll(Arrays.asList(enumValues));
    }

    @Override
    public T parse(StringReader reader) {
        return null;
    }
}
