package com.bobcat00.betterseen;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.earth2me.essentials.User;
import com.earth2me.essentials.utils.DateUtil;

import net.ess3.api.IEssentials;

public class CommandSeen implements CommandExecutor
{
    private final BetterSeen plugin;

    public CommandSeen(BetterSeen plugin)
    {
        this.plugin = plugin;
    }
    
    private static String formatTime(long time)
    {
    	Date date = new Date(time);
    	SimpleDateFormat sdf = new SimpleDateFormat("MMMM d yyyy h:mm aa");
    	return sdf.format(date);
    }
 
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        long playtime    = 0L;
    	long firstPlayed = 0L;
    	long lastPlayed  = 0L;
    	
    	Player        onlinePlayer  = null;
    	OfflinePlayer offlinePlayer = null;
    	
    	boolean online        = false;
    	boolean permSeen      = false;
    	boolean permVanishsee = false;
    	
    	if (cmd.getName().equalsIgnoreCase("seen"))
    	{
        	if (sender instanceof Player)
        	{
        		permSeen      = sender.hasPermission("essentials.seen");
        		permVanishsee = sender.hasPermission("essentials.vanish.see");
        	}
        	else
        	{
        		permSeen      = true;
        		permVanishsee = true;
        	}
        	
    		if (!permSeen)
    		{
                //sender.sendMessage("You don't have permission.");
                return true;
    		}
    		
        	if (args.length != 1)
        	{
                //sender.sendMessage("Must have exactly one argument.  Usage:");
                //sender.sendMessage("/seen <playername>");
                return true;
            }
        	
    		// Hook in to Essentials.
    		Plugin ess = Bukkit.getPluginManager().getPlugin("Essentials");
    		IEssentials EssentialsInterface = (IEssentials)ess;
    		
    		// Need to handle four cases:
    		// 1. Player is online
    		// 2. Player is online but vanished
    		// 3. Player is offline
    		// 4. Player has never been on the server
    		
    		onlinePlayer = this.plugin.getServer().getPlayer(args[0]);
    		if (onlinePlayer != null)
    		{
    			// Player is online
    			User user = EssentialsInterface.getUser(onlinePlayer.getName()); // Essentials data
    			playtime    = onlinePlayer.getStatistic(Statistic.PLAY_ONE_MINUTE); // Returns number of ticks
        		firstPlayed = onlinePlayer.getFirstPlayed();
        		lastPlayed  = onlinePlayer.getLastPlayed();
        		if (!user.isVanished() || permVanishsee)
        		{
        			online = true;
        		}
    		}
    		if (!online)
    		{
    			// Player is vanished, offline, or has never been on the server
    		    User user = EssentialsInterface.getUser(args[0]); // Essentials data
    		    if (user == null)
    		    {
    		        return true;
    		    }
    		    UUID uuid = user.getConfigUUID();
    		    if (uuid == null)
    		    {
    		        return true;
    		    }
    		    offlinePlayer = this.plugin.getServer().getOfflinePlayer(uuid);
    		    playtime = offlinePlayer.getStatistic(Statistic.PLAY_ONE_MINUTE); // Returns number of ticks
    		    if (user.getBase().isOnline() && user.isVanished())
    		    {
                    playtime = playtime - ((System.currentTimeMillis() - user.getLastVanishTime()) / 50L);
                }
	    		firstPlayed = offlinePlayer.getFirstPlayed();
	    		lastPlayed  = offlinePlayer.getLastPlayed();
	    		if (lastPlayed == 0L)
	    		{
	    			return true;
	    		}
	    		
    		}
    		
    		// Now we finally output stuff
    		
    		// First and last played
    		if (playtime    != 0L) sender.sendMessage(ChatColor.GOLD + " - Playtime: "    + ChatColor.RESET + DateUtil.formatDateDiff(System.currentTimeMillis() - (playtime * 50L)));
    		if (firstPlayed != 0L) sender.sendMessage(ChatColor.GOLD + " - Joined: "      + ChatColor.RESET + formatTime(firstPlayed));
    		if (lastPlayed  != 0L) sender.sendMessage(ChatColor.GOLD + " - Last online: " + ChatColor.RESET + formatTime(lastPlayed));
    		
    		return true;
    	}
    	
        return false;
    }
}
