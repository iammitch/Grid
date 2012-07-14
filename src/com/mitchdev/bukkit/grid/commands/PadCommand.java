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
import com.mitchdev.bukkit.grid.Permissions;

public class PadCommand extends CommandHandler {

	final String[] COMMANDS = { "network", "enable", "disable", "set" };
	
	@Override
	public boolean onCommand(Grid grid, CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		
		if ( commandLabel.equalsIgnoreCase("gp") || ( cmd.getName().equalsIgnoreCase("grid") && args.length > 0 && args[0].equalsIgnoreCase("pad"))) {
			
			int offset = commandLabel.equalsIgnoreCase("gp") ? 0 : 1;
			
			if ( args.length == offset ) {
				
				sender.sendMessage( Grid.getChatPrefix() + "Pads" );
				
				sender.sendMessage("NOTE: [pad_name] can be omitted from the command if you're standing on the pad you which to manage");
				
				sender.sendMessage("/grid pad [pad_name] network add <name> [default]");
				sender.sendMessage("/grid pad [pad_name] network remove <name>");
				sender.sendMessage("/grid pad [pad_name] network default <name>");
				
				sender.sendMessage("/grid pad [pad_name] enable");
				sender.sendMessage("/grid pad [pad_name] disable");
				
				sender.sendMessage("/grid pad [pad_name] set name <new_name>");
				sender.sendMessage("/grid pad [pad_name] set visibility <visibility>");
				sender.sendMessage("/grid pad [pad_name] set owner <name>");
				sender.sendMessage("/grid pad [pad_name] set password <new_pass>");				
				
			}
			else {
				
				String n = args[offset];
				
				boolean match = false;
				for ( String c : COMMANDS ) {
					if ( c.equalsIgnoreCase ( n ) ) {
						match = true;
					}
				}
				
				if ( match && !(sender instanceof Player) ) {
					sender.sendMessage(Grid.getChatPrefix() + "This command can only be run by a player.");
					sender.sendMessage(Grid.getChatPrefix() + "Please specify a pad name to use this command from the console.");
					return true;
				}
				
				Pad pad = match ? grid.getPad((Player)sender) : grid.getPad(n);
				
				if ( pad == null ) {
					sender.sendMessage(Grid.getChatPrefix() + "Pad not found..");
					return true;
				}
				
				if ( n.equalsIgnoreCase( "network" ) ) {
					padNetworkCommand ( grid, sender, pad, (String[]) ArrayUtils.subarray(args, offset + (match?1:0), args.length) );
				}
				else if ( n.equalsIgnoreCase( "enable" ) ) {
					
				}
				else if ( n.equalsIgnoreCase( "disable" ) ) {
					
				}
				
			}
			
			return true;
			
		}
		
		return false;
	}
	
	private void padNetworkCommand ( Grid grid, CommandSender sender, Pad pad, String[] args ) {
		
		if ( args[0].equalsIgnoreCase ( "add" ) ) {
			
			if ( (sender instanceof Player) && !Permissions.getInstance().hasPermission( (Player) sender, "grid.network.add" ) ) {
				sender.sendMessage(Grid.getChatPrefix() + "You do not have permission to use this command..");
				return;
			}
			
			// Add a network to the given pad.
			String networkName = args[1];
			boolean def = false;
			if ( args.length >= 3 && args[2].equalsIgnoreCase( "default" ) ) {
				def = true;
			}
			sender.sendMessage("Add to network " + networkName );
			sender.sendMessage("Make Default? " + (def ? "Yes" : "No"));
			
			Network net = grid.getNetwork(networkName);
			
			if ( net == null ) {
				sender.sendMessage(Grid.getChatPrefix() + "No network with the name '" + ChatColor.YELLOW + networkName + ChatColor.RESET + "' exists..");
				return;
			}
			
			if ( def ) {
				if ( pad.getPrimaryNetwork().length() != 0 ) {
					grid.getNetwork(pad.getPrimaryNetwork()).removePad(pad.getName());
				}
				else {
					// It's in the default (global) network.
					grid.removePad(pad.getName());
				}
				grid.getNetwork( networkName ).addPad(pad);
			}
			
			pad.addNetwork( networkName, def );
			
			sender.sendMessage(Grid.getChatPrefix() + "SUCCESS: Added pad '" 
			+ ChatColor.YELLOW + pad.getName() + ChatColor.RESET + "' to network '" + ChatColor.YELLOW + networkName + ChatColor.RESET + "'");
			
		}
		
	}

}