package com.mitchdev.bukkit.grid.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mitchdev.bukkit.grid.CommandHandler;
import com.mitchdev.bukkit.grid.Grid;
import com.mitchdev.bukkit.grid.Pad;

public class InfoCommand extends CommandHandler {

	@Override
	public boolean onCommand(Grid grid, CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		
		if ( commandLabel.equalsIgnoreCase("gi") || ( cmd.getName().equals( "grid" ) && args.length > 0 && args[0].equalsIgnoreCase("info") ) ) {
			
			if ( !(sender instanceof Player) ) {
				sender.sendMessage(Grid.getChatPrefix() + "This command can only be run by a player.");
				sender.sendMessage(Grid.getChatPrefix() + "Look at 'grid pad <name> info' to view pad information via a console.");
			}
			
			for ( Pad pad : grid.getPads() ) {
				if ( pad.getBounds().contains((Player)sender) ) {
					// This is it!
					padInfo ( pad, sender );
					return true;
				}
			}
	
			sender.sendMessage( Grid.getChatPrefix() + "No pad found..." );
			
			return true;
			
		}
	
		return false;
		
	}

	public void padInfo ( Pad pad, CommandSender sender ) {
		
		sender.sendMessage(ChatColor.AQUA + "--- " + pad.getName() + " ---");
		
		sender.sendMessage( ChatColor.YELLOW + "Owner: " + ChatColor.RESET + pad.getOwner() );
		sender.sendMessage( ChatColor.YELLOW + "Networks:" );
		
		for ( String network : pad.getNetworks() ) {
			
			sender.sendMessage( " " + network );
			
		}
		
		sender.sendMessage( ChatColor.YELLOW + "Switches: " + ChatColor.RESET + pad.getSwitches().size() );
		sender.sendMessage( ChatColor.YELLOW + "Triggers: " + ChatColor.RESET + pad.getTriggers().size() );
		
	}
	
	
}
