package com.mitchdev.bukkit.grid.commands;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mitchdev.bukkit.grid.CommandHandler;
import com.mitchdev.bukkit.grid.Grid;
import com.mitchdev.bukkit.grid.Pad;

public class SwitchCommand extends CommandHandler {

	@Override
	public boolean onCommand(Grid grid, CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		
		if ( cmd.getName().equalsIgnoreCase("grid") && args.length > 0 && args[0].equalsIgnoreCase("switch") ) {
			
			if ( !(sender instanceof Player ) ) {
				sender.sendMessage(Grid.getChatPrefix() + "This command can only be sent by a player..");
				return true;
			}
			
			if ( args[1].equalsIgnoreCase("add") ) {
				
				Pad pad = grid.getPad((Player)sender);
				if ( pad == null ) {
					sender.sendMessage(Grid.getChatPrefix() + "ERROR: You must be standing on a pad to use this command..");
					return true;
				}
				
				Block looking = ((Player)sender).getTargetBlock(null, 32);
				
				if ( looking == null ) {
					sender.sendMessage(Grid.getChatPrefix() + "ERROR: You must be looking at either a " + ChatColor.RED + 
							"Redstone Torch" + ChatColor.RESET + " or a " + ChatColor.RED + "Redstone Lamp");
					return true;
				}
				
				Material material = looking.getType();
				if ( material.getId() == Material.REDSTONE_TORCH_OFF.getId() || material.getId() == Material.REDSTONE_TORCH_ON.getId() 
						|| material.getId() == Material.REDSTONE_LAMP_OFF.getId() || material.getId() == Material.REDSTONE_LAMP_ON.getId() ) {
					pad.addSwitch(looking.getLocation());
					sender.sendMessage(Grid.getChatPrefix() + "SUCCESS: Switch added to pad '" + ChatColor.YELLOW + pad.getName() + ChatColor.RESET + "'");
				}
				else {
					sender.sendMessage(Grid.getChatPrefix() + "ERROR: You must be looking at either a " + ChatColor.RED + 
							"Redstone Torch" + ChatColor.RESET + " or a " + ChatColor.RED + "Redstone Lamp");
					return true;
				}
				
			}
			else if ( args[1].equalsIgnoreCase("clear") ) {
				
				Pad pad = grid.getPad((Player)sender);
				if ( pad == null ) {
					sender.sendMessage(Grid.getChatPrefix() + "ERROR: You must be standing on a pad to use this command..");
					return true;
				}
				
				pad.clearSwitches();
				sender.sendMessage(Grid.getChatPrefix() + "SUCCESS:Cleared switches on pad '" + ChatColor.YELLOW + pad.getName() + ChatColor.RESET + "'");
				
			}
			
		}
		
		return false;
	}

}
