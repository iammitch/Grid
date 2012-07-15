package com.mitchdev.bukkit.grid.commands;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mitchdev.bukkit.grid.CommandHandler;
import com.mitchdev.bukkit.grid.Grid;
import com.mitchdev.bukkit.grid.Network;
import com.mitchdev.bukkit.grid.Pad;

public class NetworkCommand extends CommandHandler {

	@Override
	public boolean onCommand ( Grid grid, CommandSender sender, Command cmd, String commandLabel, String[] args ) {
		
		if ( cmd.getName().equalsIgnoreCase("grid") && args.length == 1 && args[0].equalsIgnoreCase("networks") ) {
			showNetworkList ( grid, sender );
			return true;
		}
		else if ( commandLabel.equalsIgnoreCase("gn") || ( cmd.getName().equalsIgnoreCase("grid") && args.length > 0 && args[0].equalsIgnoreCase("network") ) ) {
			
			if ( ( commandLabel.equalsIgnoreCase("gn") && args.length == 0 ) || args.length == 1 ) {
				
				// Write out the commands that are here.
				sender.sendMessage(Grid.getChatPrefix() + "Network");
				sender.sendMessage("/grid network add <name>");
				sender.sendMessage("/grid network remove <name> [dest] - Remove network with the given name. Will be placed into network [dest] if given, or default if none.");
				
				sender.sendMessage("/grid network list <name> - List pads on a given network.");
				
				sender.sendMessage("/grid network <name> entry pad <name> <target_pad> - Define the entry pad <target_pad> if teleporting onto network from <name>");
				sender.sendMessage("/grid network <name> entry network <name> <target_pad> - Define the entry pad <target_pad> if teleporting onto network from <name>");
				
				sender.sendMessage("/grid network <name> gateway <name> - Define the gateway pad that is used to exit the network.");
				
			}
			else {
				
				int offset = commandLabel.equalsIgnoreCase("gn") ? 0 : 1;
				
				if ( args[offset].equalsIgnoreCase("add") ) {
					
					String name = args[offset+1];
					
					// Do we already have a network by this name.
					if ( grid.getNetwork ( name ) != null ) {
						sender.sendMessage( Grid.getChatPrefix() + "ERROR: Network already exists..");
						return true;
					}
					
					Network net = new Network ( name );
					grid.addNetwork ( net );
					
					sender.sendMessage( Grid.getChatPrefix() + "SUCCESS: Created network '" + ChatColor.YELLOW + name + ChatColor.RESET + "'");
					
				}
				
				else if ( args[offset].equalsIgnoreCase("list") ) {
					
					showNetworkList ( grid, sender );
					
				}
				
				else {
					
					// Check name for list of valid networks.
					Network network = grid.getNetwork ( args[offset] );
					if ( network != null ) {
						networkCommand ( network, grid, sender, (String[]) ArrayUtils.subarray( args, offset + 1, args.length ) );
					}
					
				}
				
			}
			
			return true;
			
		}
		
		return false;
	}
	
	public void networkCommand ( Network network, Grid grid, CommandSender sender, String[] args ) {
		
		if ( args.length == 0 ) {
			return;
		}
		
		if ( args[0].equalsIgnoreCase("gateway") ) {
		
			Pad pad = network.getPad(args[1]);
			if ( pad == null ) {
				sender.sendMessage(Grid.getChatPrefix() + "Target network is not a primary member of the given network.");
				return;
			}
			
			network.setGateway ( args[1] );
			
			sender.sendMessage(Grid.getChatPrefix() + "Network gateway set to '" + args[1] + "'");
			
			return;
			
		}
		
	}
	
	public void showNetworkList ( Grid grid, CommandSender sender ) {
		
		if ( sender instanceof Player ) {
			if ( !sender.hasPermission( "grid.network.list" ) ) {
				sender.sendMessage(Grid.getChatPrefix() + "You do not have permission to use this command.");
				return;
			}	
		}
		
		sender.sendMessage(Grid.getChatPrefix() + "Network List");
		
		sender.sendMessage(ChatColor.YELLOW + "Number of Networks: " + ChatColor.RESET + grid.getNetworks().size() );
		
		for ( Network network : grid.getNetworks() ) {
			
			sender.sendMessage( ChatColor.GREEN + "Network: " + ChatColor.YELLOW + network.getId() );
			
		}
		
	}
	
}
