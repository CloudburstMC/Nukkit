package cn.nukkit.test;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.plugin.simple.Command;
import cn.nukkit.plugin.simple.Main;
import cn.nukkit.plugin.simple.Permission;

@Main(name="hello",api="1.0.9",commands = {
        @Command(
                name="hello",
                permission = "hello.test"
        )
},permissions = {
        @Permission(
                permission = "hello.test",
                theDefault = "op"
        )
})
public class PluginTest extends PluginBase {
}
