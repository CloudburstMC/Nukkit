package cn.nukkit.service;

import cn.nukkit.IPlayer;
import cn.nukkit.level.Level;
import java.util.Collection;

public interface EconomyService extends Service{
    public String getCurrency();

    public boolean hasAccount(IPlayer player, Level level);

    public double getBalance(IPlayer player, Level level);

    public boolean has(IPlayer player, Level level, double amount);


    /**
     * Returns amount not withdrawn
     * @param player
     * @param level
     * @param amount
     * @return
     */
    public double withdraw(IPlayer player, Level level, double amount);

    /**
     * Returns amount not deposited
     * @param player
     * @param level
     * @param amount
     * @return
     */
    public double deposit(IPlayer player, Level level, double amount);

    public Collection<IPlayer> getAccounts() ;
}
