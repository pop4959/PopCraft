package org.popcraft.popcraft.commands;

import java.util.HashMap;
import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.popcraft.popcraft.utils.Message;
import net.md_5.bungee.api.ChatColor;

public class Piggyback implements Listener, CommandExecutor
{
	public static HashMap<UUID, Boolean> piggyback = new HashMap<UUID, Boolean>();
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		Player p = (Player)sender;
		if (cmd.getName().equalsIgnoreCase("piggyback"))
		{
			if (getRideable(p.getUniqueId()))
			{
				Message.normal(sender, "Piggyback " + ChatColor.RED + "disabled" + ChatColor.GOLD + ".");
				setRideable(p.getUniqueId(), false);
			}
			else
			{
				Message.normal(sender, "Piggyback " + ChatColor.RED + "enabled" + ChatColor.GOLD + ".");
				setRideable(p.getUniqueId(), true);
			}
		}
		return true;
	}
    
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e)
	{
		if (e.getRightClicked() instanceof Player)
		{
			Player rider = e.getPlayer();
			Player target = (Player)e.getRightClicked();
			{
				if(rider.getVehicle()==null && rider.getPassenger()==null)
				{
					if(target.getPassenger()!=null)
						do
							target = (Player)target.getPassenger();
						while (target.getPassenger()!=null);
					if(getRideable(rider.getUniqueId()) && getRideable(target.getUniqueId()))
						target.setPassenger(rider);
				}
				else
				{
					if(target==rider.getPassenger())
						rider.eject();
				}
			}
		}
	}
    
    private static void setRideable(UUID uuid, Boolean state)
    {
    	piggyback.put(uuid, state);
    }
    
    private static boolean getRideable(UUID uuid)
    {
    	if (!piggyback.containsKey(uuid))
    	{
    		piggyback.put(uuid, false);
    	}
    	Boolean state = piggyback.get(uuid);
    	return state;
    }
}