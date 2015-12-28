package cn.nukkit.event;

/**
 * 所有的监听事件的类必须实现的接口。<br>
 * An interface implemented by all classes that handles events.
 *
 * <p>插件要监听事件，需要一个类实现这个接口，在这个类里编写方法来监听。这个类称作<b>监听类</b>。
 * 监听类中监听事件的方法称作事件的<b>处理器</b>。一个监听类可以包含多个不同的事件处理器。
 * 实现监听类后，插件需要在插件管理器中注册这个监听类。<br>
 * If a plugin need to listen events, there must be a class implement this interface. This class is called a <b>listener class</b>.
 * Methods with specified parameters should be written in order to listen events. This method is called a <b>handler</b>.
 * One listener class could contain many different handlers.
 * After implemented the listener class, plugin should register it in plugin manager.</p>
 *
 * <p>事件监听器被注册后，Nukkit会在需要监听的事件发生时，使用反射来调用监听类中对应的处理器。<br>
 * After registered, Nukkit will call the handler in the listener classes by reflection when a event happens.</p>
 *
 * <p>这是一个编写监听类和处理器的例子。注意的是，标签{@code @EventHandler}和参数的类型是必需的：<br>
 * Here is an example for writing a listener class and a handler method.
 * Note that for the handler, tag {@code @EventHandler} and the parameter is required:</p>
 * <pre>
 * public class ExampleListener implements Listener {
 *    {@code @EventHandler}
 *     public void onBlockBreak(BlockBreakEvent event) {
 *          int blockID = event.getBlock().getId();
 *          if (blockID == Block.STONE) {
 *              event.getPlayer().sendMessage("Oops, my ExampleListener won't let you break a stone!")
 *              event.setCancelled(true);
 *          }
 *     }
 * }
 * </pre></p>
 *
 * <p>关于注册监听类，请看：{@link cn.nukkit.plugin.PluginManager#registerEvents}.<br>
 * For registering listener class, See: {@link cn.nukkit.plugin.PluginManager#registerEvents}.</p>
 *
 * <p>关于监听器的优先级和监听器是否接收被取消的事件，请看：{@link EventHandler}.<br>
 * For the priority of listener and whether the listener receive cancelled events or not, See: {@link EventHandler}.</p>
 *
 * @author Unknown(code) @ Nukkit Project
 * @author 粉鞋大妈(javadoc) @ Nukkit Project
 * @see cn.nukkit.event.Event
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
public interface Listener {
}
