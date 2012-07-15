package com.mitchdev.bukkit.grid.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mitchdev.bukkit.grid.CommandHandler;
import com.mitchdev.bukkit.grid.Grid;
import com.mitchdev.bukkit.grid.Network;
import com.mitchdev.bukkit.grid.Pad;
import com.mitchdev.bukkit.grid.Visibility;

public class ListPadsCommand extends CommandHandler {

	public ListPadsCommand() {
		
	}
	
	@Override
	public boolean onCommand ( Grid grid, CommandSender sender, Command cmd, String commandLabel, String[] args ) {
		
		if ( !sender.hasPermission( "grid.list" ) ) {
			sender.sendMessage(Grid.getChatPrefix() + "ERROR: You do not have permission to use this command..");
			return true;
		}
		
		if ( cmd.getName().equalsIgnoreCase("grid") ) {
				
			if ( commandLabel.equalsIgnoreCase("gl") || ( args.length > 0 && args[0].equalsIgnoreCase( "list" ) ) ) {
				
				sender.sendMessage( ChatColor.AQUA + "--- Pads ---");
				
				// Iterate over the default pads (IE: Don't belong to a network)
				
				listNetwork ( grid, sender, grid.getGlobalNetwork() );
				
				for ( Network network : grid.getNetworks() ) {
					
					if ( sender.hasPermission("grid.list.network." + network.getId())) {
					
						listNetwork ( grid, sender, network );
						
					}
					
				}
				
				return true;
				
			}
			
		}
		
		return false;
		
	}
	
	private void listNetwork ( Grid grid, CommandSender sender, Network network ) {
		
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
		
		sender.sendMessage ( ChatColor.YELLOW + network.getId() + ":" );
		
		for ( Pad pad : network.getPads() ) {
			
			if ( pad.getVisibility() == Visibility.Hidden && !seeHidden ) {
				continue;
			}
			if( pad.getVisibility() == Visibility.Unlisted && !seeUnlisted ) {
				continue;
			}
			
			printPad(sender, pad.getName(), pad);
			
		}
		
		// Get associated pads.
		for ( Pad pad : grid.getAssociatedPads ( network.getId( ) ) ) {
			
			if ( pad.getVisibility() == Visibility.Hidden && !seeHidden ) {
				continue;
			}
			if( pad.getVisibility() == Visibility.Unlisted && !seeUnlisted ) {
				continue;
			}
			
			printPad(sender, pad.getName() + ChatColor.GOLD + "@" + pad.getPrimaryNetwork() + ChatColor.RESET, pad);
			
		}
		
	}
	
	private void printPad ( CommandSender sender, String name, Pad pad ) {
		String vis = "-";
		
		switch ( pad.getVisibility() ) {
		
		case Visible:
			vis = ChatColor.GREEN + "V" + ChatColor.RESET;
			break;
		case Hidden:
			vis = ChatColor.YELLOW + "H" + ChatColor.RESET;
			break;
		case Unlisted:
			vis = ChatColor.RED + "U" + ChatColor.RESET;
			break;
		
		}
		
		sender.sendMessage( String.format(" %s - %s %s %s", name, vis, pad.getSwitches().size(), pad.getTriggers().size() ) );
	}
	
}
