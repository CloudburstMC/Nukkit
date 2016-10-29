package cn.nukkit.inventory;

import java.util.Set;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public interface TransactionGroup {

    long getCreationTime();

    Set<Transaction> getTransactions();

    Set<Inventory> getInventories();

    void addTransaction(Transaction transaction);

    boolean canExecute();

    boolean execute();

    boolean execute(boolean force);

    boolean hasExecuted();
}
