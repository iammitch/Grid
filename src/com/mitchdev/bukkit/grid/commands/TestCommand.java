package com.mitchdev.bukkit.grid.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mitchdev.bukkit.grid.CommandHandler;
import com.mitchdev.bukkit.grid.Grid;

public class TestCommand extends CommandHandler {

	@Override
	public boolean onCommand(Grid grid, CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		
		if ( cmd.getName().equals("grid") && args.length != 0 && args[0].equalsIgnoreCase("test")) {
			
			if ( !(sender instanceof Player) ) {
				sender.sendMessage(Grid.getChatPrefix() + "This command must be run by a player.");
			}
			
			String[] permissions = { "grid.pad.use", "grid.pad.enable", "grid.pad.disable", "grid.pad.set.password", "grid.pad.bypass" };
			
			for ( String perm : permissions ) {
				
				boolean has = sender.hasPermission(perm);
				
				sender.sendMessage( Grid.getChatPrefix() +  
						"'" + ChatColor.AQUA + perm + ChatColor.RESET + "' - " + (has ? ChatColor.GREEN : ChatColor.RED ) + (has?"Yes":"No") );
				
			}
			
			return true;
			
		}
		
		return false;
	}

}
