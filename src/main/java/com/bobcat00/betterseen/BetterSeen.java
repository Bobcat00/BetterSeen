package com.bobcat00.betterseen;

import org.bukkit.plugin.java.JavaPlugin;

public final class BetterSeen extends JavaPlugin
{

    @Override
    public void onEnable()
    {
    	//getServer().getPluginManager().registerEvents(new Listeners(this), this);
        this.getCommand("seen").setExecutor(new CommandSeen(this));
    }
 
    @Override
    public void onDisable()
    {
    	// HandlerList.unregisterAll(listeners);
    }

}