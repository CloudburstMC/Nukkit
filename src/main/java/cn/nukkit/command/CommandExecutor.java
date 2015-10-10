package cn.nukkit.command;

/**
 * 能监听命令执行的类实现的接口。<br />
 * An interface what can be implemented by classes which listens command executing.
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @author 粉鞋大妈(javadoc) @ Nukkit Project
 * @see cn.nukkit.plugin.PluginBase
 * @see cn.nukkit.command.CommandExecutor#onCommand
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
public interface CommandExecutor {
    /**
     * 在命令执行时会调用的方法。<br />
     * Called when a command is executed.
     *
     * <p>一个命令可以是{@code /a_LABEL an_arg1 AN_ARG2...}的形式，这时{@code label}变量的值为{@code "a_label"}，
     * {@code args}数组的元素有{@code "an_arg1","AN_ARG2",...}。注意到{@code label}变量会被转化成小写，
     * 而{@code args}数组内字符串元素的大小写不变。<br />
     * A command can be such a form like {@code /a_LABEL an_arg1 AN_ARG2...}. At this time, the value of
     * variable {@code label} is {@code "a_label"}, and the values of elements of array {@code args} are
     * {@code "an_arg1","AN_ARG2",...}. Notice that the value of variable {@code label} will be converted to
     * lower case, but the cases of elements of array {@code args} won't change.</p>
     *
     * <p>关于返回值，如果返回{@code false}，Nukkit会给sender发送这个命令的使用方法等信息，来表示这个命令没有使用成功。
     * 如果你的命令成功的发挥了作用，你应该返回{@code true}来表示这个命令已执行成功。<br />
     * If this function returns {@code false}, Nukkit will send command usages to command sender, to explain that
     * the command didn't work normally. If your command works properly, a {@code true} should be returned to explain
     * that the command works.</p>
     *
     * <p>如果你想测试一个命令发送者是否有权限执行这个命令，
     * 可以使用{@link cn.nukkit.command.Command#testPermissionSilent}。<br />
     * If you want to test whether a command sender has the permission to execute a command,
     * you can use {@link cn.nukkit.command.Command#testPermissionSilent}.</p>
     *
     * @param sender 这个命令的发送者，可以是玩家或控制台等。<br />
     *               The sender of this command, this can be a player or a console.
     * @param command 要被发送的命令。<br />
     *                The command to send.
     * @param label 这个命令的标签。<br />
     *              Label of the command.
     * @param args 这个命令的参数列表。<br />
     *             Arguments of this command.
     * @return 这个命令执行是否执行成功。<br />whether this command is executed successfully.
     * @since Nukkit 1.0 | Nukkit API 1.0.0
     */
    boolean onCommand(CommandSender sender, Command command, String label, String[] args);
}
