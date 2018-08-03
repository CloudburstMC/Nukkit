package cn.nukkit;

/**
 * 描述一个可以被中断的线程的接口。<br>
 * An interface to describe a thread that can be interrupted.
 *
 * <p>在Nukkit服务器停止时，Nukkit会找到所有实现了{@code InterruptibleThread}的线程，并逐一中断。<br>
 * When a Nukkit server is stopping, Nukkit finds all threads implements {@code InterruptibleThread},
 * and interrupt them one by one.</p>
 *
 * @author MagicDroidX(code) @ Nukkit Project
 * @author 粉鞋大妈(javadoc) @ Nukkit Project
 * @see cn.nukkit.scheduler.AsyncWorker
 * @see cn.nukkit.command.CommandReader
 * @since Nukkit 1.0 | Nukkit API 1.0.0
 */
public interface InterruptibleThread {
}
