package com.mitchdev.bukkit.grid.commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mitchdev.bukkit.grid.CommandHandler;
import com.mitchdev.bukkit.grid.Grid;
import com.mitchdev.bukkit.grid.Network;
import com.mitchdev.bukkit.grid.Pad;
import com.mitchdev.bukkit.grid.Visibility;

public class TeleportCommand extends CommandHandler {

	private Pad getPad ( Grid grid, Location location ) {
		
		for ( Pad pad : grid.getPads ( ) ) {
			if ( pad.getBounds().contains(location) ) {
				return pad;
			}
		}
		
		for ( Network network : grid.getNetworks( ) ) {
			for ( Pad pad : network.getPads() ) {
				if ( pad.getBounds().contains(location) ) {
					return pad;
				}
			}
		}
		
		return null;
		
	}
	
	private Pad getPad ( Grid grid, String name ) {
		
		if ( name.charAt(0) == '@' ) {
			// Get network.
			String network = name.substring(1);
			Network net = grid.getNetwork(network);
			if ( net != null ) {
				return net.getEntryPad();
			}
		}
		else {
			
			for ( Pad pad : grid.getPads ( ) ) {
				if ( pad.getName().equalsIgnoreCase(name) ) {
					return pad;
				}
			}
			
			// Check global network pads.
			for ( Network network : grid.getNetworks( ) ) {
				for ( Pad pad : network.getPads() ) {
					if ( pad.getName().equalsIgnoreCase(name) && pad.hasNetwork("global") ) {
						return pad;
					}
				}
			}
		}
		
		return null;
		
	}
	
	private Pad getPad ( Pad pad, Grid grid, String name ) {
		if ( name.charAt(0) == '@' ) {
			// Get network.
			String network = name.substring(1);
			Network net = grid.getNetwork(network);
			if ( net != null ) {
				return net.getEntryPad();
			}
		}
		else {
			for ( String net : pad.getNetworks( ) ) {
				for ( Pad p : grid.getNetwork ( net ).getPads() ) {
					if ( p.getName().equalsIgnoreCase(name)) {
						return p;
					}
				}
			}
		}
		
		// Otherwise, global.
		return getPad ( grid, name );
		
	}
	
	@Override
	public boolean onCommand ( Grid location, CommandSender sender, Command cmd, String commandLabel, String[] args ) {

		if ( cmd.getName().equals("t") ) {

			if ( !(sender instanceof Player) ) {
				sender.sendMessage(Grid.getChatPrefix() + "This command can only be run by a player..");
				return true;
			}

			Player player = (Player)sender;

			if ( !sender.hasPermission( "grid.pad.use" ) ) {
				sender.sendMessage(Grid.getChatPrefix() + "You do not have permission to use this command..");
				return true;
			}
			else {

				// Does the player collide with one of our pads?
				Location dest = player.getLocation();
				
				Pad localPad = getPad ( location, dest );
				
				if ( localPad == null ) {
					player.sendMessage(Grid.getChatPrefix() + "Pad not found.");
					return true;
				}
				
				if( !localPad.isEnabled() ) {
					
					if ( localPad.getVisibility ( ) == Visibility.Visible ) {
						player.sendMessage(Grid.getChatPrefix() + "Pad is disabled.");
					}			
					else {
						player.sendMessage(Grid.getChatPrefix() + "You're not standing on a pad!");
					}
					return true;
					
				}
				
				Pad targetPad = localPad.getPrimaryNetwork().length() == 0 ? getPad ( location, args[0] ) : getPad ( localPad, location, args[0] );
				
				if ( targetPad == null ) {
					player.sendMessage(Grid.getChatPrefix() + "Target pad not found.");
					return true;
				}
				
				if( !targetPad.isEnabled() ) {
					
					if ( targetPad.getVisibility ( ) == Visibility.Visible ) {
						player.sendMessage(Grid.getChatPrefix() + "Remote pad is disabled.");
					}			
					else {
						player.sendMessage(Grid.getChatPrefix() + "Target pad not found.");
					}
					return true;
					
				}
				
				if ( localPad == targetPad ) {
					
					player.sendMessage(Grid.getChatPrefix() + "Can't teleport to self!");
					
				}
				else {
				
					player.sendMessage(Grid.getChatPrefix() + "Teleporting...");
					
					Location loc = targetPad.getLocation().clone();
					loc = loc.add(0.5, -targetPad.getBounds().height, 0.5);
					player.teleport(loc);
					targetPad.fireTriggers();
				
				}

			} // end else
			
			return true;

		} // end if ( cmd.getName().equals("t") ) {

		return false;

	}

}
