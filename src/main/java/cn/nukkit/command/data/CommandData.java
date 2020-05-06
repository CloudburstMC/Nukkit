package cn.nukkit.command.data;

import cn.nukkit.Server;
import com.nukkitx.protocol.bedrock.data.CommandParamData;
import lombok.NonNull;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This stores the information about a command.
 * To create during {@link cn.nukkit.command.Command} constructors, use the
 * {@link CommandData#builder(String)} method to obtain a builder.
 *
 * @see cn.nukkit.command.PluginCommand
 */
@ToString
public class CommandData {
    private final String cmdName;
    private final String description;
    private final String usage;
    private final String permMsg;
    private final List<String> permissions;
    private final CommandEnum aliases;
    private final List<CommandParameter[]> overloads;
    private String registeredName;

    private CommandData(String cmdName, String description, String usage, String permissionMessage, List<String> permissions, CommandEnum aliases, List<CommandParameter[]> overloads) {
        this.cmdName = cmdName;
        this.description = description;
        this.usage = usage;
        this.permMsg = permissionMessage;
        this.permissions = permissions;
        this.aliases = aliases;
        this.overloads = overloads;
    }

    public static Builder builder(@NonNull String commandName) {
        return new Builder(commandName);
    }

    public String getRegisteredName() {
        return this.registeredName == null ? cmdName : registeredName;
    }

    public void setRegisteredName(String name) {
        this.registeredName = name;
    }

    public String getDescription() {
        return this.description;
    }

    public List<String> getPermissions() {
        return this.permissions;
    }

    public List<String> getAliases() {
        return this.aliases.getValues();
    }

    public void removeAlias(String alias) {
        this.aliases.getValues().remove(alias);
    }

    public com.nukkitx.protocol.bedrock.data.CommandData toNetwork() {
        String description = Server.getInstance().getLanguage().translate(this.description);

        CommandParamData[][] overloadData = new CommandParamData[this.overloads.size()][];

        for (int i = 0; i < overloadData.length; i++) {
            CommandParameter[] parameters = this.overloads.get(i);
            CommandParamData[] params = new CommandParamData[parameters.length];
            for (int i2 = 0; i2 < parameters.length; i2++) {
                params[i2] = parameters[i2].toNetwork();
            }
            overloadData[i] = params;
        }

        return new com.nukkitx.protocol.bedrock.data.CommandData(this.getRegisteredName(), description, Collections.emptyList(),
                (byte) 0, this.aliases.toNetwork(), overloadData);
    }

    public List<CommandParameter[]> getOverloads() {
        return this.overloads;
    }

    public String getPermissionMessage() {
        return this.permMsg;
    }

    public String getUsage() {
        return this.usage;
    }

    public static class Builder {
        private final String name;
        private String desc = "";
        private String usage = "";
        private String permMsg = "";
        private List<String> perms = new ArrayList<>();
        private List<String> aliases = new ArrayList<>();
        private List<CommandParameter[]> overloads = new ArrayList<>();

        public Builder(@NonNull String name) {
            this.name = name.toLowerCase();
        }

        public CommandData build() {
            if (overloads.size() == 0) {
                this.overloads.add(new CommandParameter[]{new CommandParameter("args", CommandParamType.RAWTEXT, true)});
            }
            return new CommandData(name, desc, usage, permMsg, perms, new CommandEnum(name, aliases), overloads);
        }

        /**
         * Sets the description string for this Command. When sent to the client, the string will be
         * parsed through the Server translation layer. You will need to provide the
         * {@link cn.nukkit.locale.LocaleManager LocaleManager} with your translation texts if you wish to use this feature.
         *
         * @param description Description string, which may contain a language file constant (ex: <code>%commands.ban.description</code>)
         * @return Builder instance for chaining calls
         */
        public Builder setDescription(@NonNull String description) {
            this.desc = description;
            return this;
        }

        /**
         * Sets the usage message. This is used when a command's execution retuns <code>false</code>, and sends
         * the message string to the user.
         *
         * @param usage Usage string for this command (ex: "<code>/say &lt;message&gt;</code>")
         * @return Builder instance for chaining calls
         */
        public Builder setUsageMessage(@NonNull String usage) {
            this.usage = usage;
            return this;
        }

        /**
         * Sets the message to send to the user when they lack the required permissions to run this
         * Command. If this is not set, the built in failure message will be sent.
         *
         * @param message Custom message to send to user if they do not have the required permission
         * @return Builder instance for chaining calls
         */
        public Builder setPermissionMessage(@NonNull String message) {
            this.permMsg = message;
            return this;
        }

        /**
         * Clears all current permissions and sets as the passed in collection.
         * <p/>Note: A player only needs to have <em>one</em> of the permissions in the list to
         * be able to execute the command.
         *
         * @param permissions Collection of String premissions
         * @return Builder instance for chaining calls
         */
        public Builder setPermissions(@NonNull String... permissions) {
            this.perms = Arrays.asList(permissions);
            return this;
        }

        /**
         * Adds the passed permission String to the list of allowed permissions.
         * <p/>Note: A player only needs to have <em>one</em> of the permissions in the list to
         * be able to execute the command.
         *
         * @param permission Permission string to add
         * @return Builder instance for chaining calls
         */
        public Builder addPermission(@NonNull String permission) {
            this.perms.add(permission);
            return this;
        }

        /**
         * Adds all of the permissions in the passed String collection to the list of allowed permissions,
         * without clearing the current permissions set.
         * <p/>Note: A player only needs to have <em>one</em> of the permissions in the list to
         * be able to execute the command.
         *
         * @param permissions Collection of permissions to add
         * @return Builder instance for chaining calls
         */
        public Builder addPermissions(@NonNull String... permissions) {
            this.perms.addAll(Arrays.asList(permissions));
            return this;
        }

        /**
         * Clears the current aliases and sets the passed collection of Strings as the new aliases.
         *
         * @param aliases Collection of String aliases for this Command
         * @return Builder instance for chaining calls
         */
        public Builder setAliases(@NonNull String... aliases) {
            this.aliases = Arrays.asList(aliases);
            return this;
        }

        /**
         * Adds the alias to the set of aliases for this Command.
         *
         * @param alias Alias to add
         * @return Builder instance for chaining calls
         */
        public Builder addAlias(@NonNull String alias) {
            this.aliases.add(alias);
            return this;
        }

        /**
         * Adds all the aliases in the passed String collection, without clearing the current aliases.
         *
         * @param aliases Collection of String aliases to add to this Command.
         * @return Builder instance for chaining calls
         */
        public Builder addAliases(@NonNull String... aliases) {
            this.aliases.addAll(Arrays.asList(aliases));
            return this;
        }

        /**
         * Clears the command overloads and sets the passed collection as the overloads for this Command.
         *
         * @param paramSet Collection of {@link CommandParameter} arrays to set as the new overloads
         * @return Builder instance for chaining calls
         */
        public Builder setParameters(@NonNull CommandParameter[]... paramSet) {
            this.overloads = Arrays.asList(paramSet);
            return this;
        }

        /**
         * @param parameters
         * @return
         * @see #setParameters(CommandParameter[]...)
         */
        public Builder setParameters(@NonNull List<CommandParameter[]> parameters) {
            this.overloads = parameters;
            return this;
        }

        /**
         * Adds the set of parameters as a Command overload, without clearing the current overloads.
         *
         * @param paramSet The set of {@link CommandParameter} arrays to add to the overloads.
         * @return Builder instance for chaining calls
         */
        public Builder addParameters(@NonNull CommandParameter[]... paramSet) {
            this.overloads.addAll(Arrays.asList(paramSet));
            return this;
        }

    }
}
