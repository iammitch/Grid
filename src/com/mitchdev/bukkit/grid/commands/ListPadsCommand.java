package com.mitchdev.bukkit.grid.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mitchdev.bukkit.grid.CommandHandler;
import com.mitchdev.bukkit.grid.Grid;
import com.mitchdev.bukkit.grid.Pad;
import com.mitchdev.bukkit.grid.Visibility;

public class ListPadsCommand extends CommandHandler {

	public ListPadsCommand() {
		
	}
	
	@Override
	public boolean onCommand ( Grid grid, CommandSender sender, Command cmd, String commandLabel, String[] args ) {
		
		if ( cmd.getName().equalsIgnoreCase("grid") ) {
				
			if ( commandLabel.equalsIgnoreCase("gl") || ( args.length > 0 && args[0].equalsIgnoreCase( "list" ) ) ) {
				
				boolean seeHidden = false;
				boolean seeUnlisted = false;
				boolean isServer = false;
				
				if ( sender instanceof Player ) {
					
					Player p = (Player)sender;
					if ( p.hasPermission("grid.list.hidden") ) {
						seeHidden = true;
					}
					if( p.hasPermission("grid.list.unlisted") ) {
						seeUnlisted = true;
					}
					
				}
				else {
					seeHidden = true;
					seeUnlisted = true;
					isServer = true;
				}
				
				sender.sendMessage( ChatColor.AQUA + "--- Pads ---");
				
				// Iterate over the default pads (IE: Don't belong to a network)
				
				sender.sendMessage ( ChatColor.YELLOW + "default:" );
				
				for ( Pad pad : grid.getPads() ) {
					if ( pad.getVisibility() == Visibility.Hidden && !seeHidden ) {
						continue;
					}
					if( pad.getVisibility() == Visibility.Unlisted && !seeUnlisted ) {
						continue;
					}
					sender.sendMessage(String.format(" %s - %s %s %s", pad.getName(), "-", "-", "-"));
				}
				
				return true;
				
			}
			
		}
		
		return false;
		
	}
	
}
