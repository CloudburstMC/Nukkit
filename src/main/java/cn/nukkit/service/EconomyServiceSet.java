package cn.nukkit.service;

import cn.nukkit.IPlayer;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.Plugin;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

public class EconomyServiceSet implements EconomyService_v1 {

    private final Collection<EconomyService> services;

    public EconomyServiceSet(Collection<EconomyService> services) {
        services = new ArrayList<>(services);
        services.removeIf(service -> !service.isEnabled());
        this.services = Collections.unmodifiableCollection(services);
    }

    @Override
    public Plugin getPlugin() {
        return null;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "Nukkit";
    }

    public EconomyServiceSet fromCurrency(String currencyRegex) {
        Pattern pattern = Pattern.compile(currencyRegex);
        ArrayDeque<EconomyService> newSet = new ArrayDeque<>();
        for (EconomyService service : this.services) {
            if (service.getCurrency().matches(currencyRegex)) {
                newSet.add(service);
            }
        }
        return new EconomyServiceSet(newSet);
    }

    @Override
    public String getCurrency() {
        for (EconomyService service : services) {
            String currency = service.getCurrency();
            if (currency != null) {
                return currency;
            }
        }
        return "";
    }

    @Override
    public boolean hasAccount(IPlayer player, Level level) {
        for (EconomyService service : services) {
            if (service.hasAccount(player, level)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public double getBalance(IPlayer player, Level level) {
        double balance = 0;
        for (EconomyService service : services) {
            balance += service.getBalance(player, level);
        }
        return balance;
    }

    @Override
    public boolean has(IPlayer player, Level level, double amount) {
        double balanceTotal = 0;
        for (EconomyService service : services) {
            if (service.has(player, level, amount)) {
                return true;
            }
            balanceTotal += service.getBalance(player, level);
        }
        return balanceTotal >= amount;
    }

    @Override
    public double withdraw(IPlayer player, Level level, double amount) {
        for (EconomyService service : services) {
            amount = service.withdraw(player, level, amount);
            if (amount <= 0) {
                return amount;
            }
        }
        return amount;
    }

    @Override
    public double deposit(IPlayer player, Level level, double amount) {
        for (EconomyService service : services) {
            amount = service.deposit(player, level, amount);
            if (amount <= 0) {
                return amount;
            }
        }
        return amount;
    }

    @Override
    public Collection<IPlayer> getAccounts() {
        ArrayDeque<IPlayer> accounts = new ArrayDeque<>();
        for (EconomyService service : services) {
            accounts.addAll(service.getAccounts());
        }
        return accounts;
    }

}
