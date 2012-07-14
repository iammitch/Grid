package com.mitchdev.bukkit.grid.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mitchdev.bukkit.grid.CommandHandler;
import com.mitchdev.bukkit.grid.Grid;
import com.mitchdev.bukkit.grid.Pad;

public class SetCommand extends CommandHandler {

	public SetCommand() {
		
	}
	
	@Override
	public boolean onCommand ( Grid grid, CommandSender sender, Command cmd, String commandLabel, String[] args ) {
		
		if ( commandLabel.equalsIgnoreCase("gs") || ( cmd.getName().equalsIgnoreCase("grid") && args.length > 0 && args[0].equalsIgnoreCase("set") ) ) {

			if ( !(sender instanceof Player) ) {
				sender.sendMessage(Grid.getChatPrefix() + "This sort of command must be run by a client.");
				sender.sendMessage(Grid.getChatPrefix() + "Use 'grid pad <name> set' to access this command from the console.");
			}
			
			Player player = (Player)sender;
			
			for ( Pad pad : grid.getPads ( ) ) {
				if ( pad.getBounds().contains(player) ) {
					String[] commands = new String[args.length - 1];
					for ( int i = 1; i < args.length; i++ ) {
						commands[i-1] = args[i];
					}
					doSet ( sender, pad, commands );
				}
			}
			
			return true;

		}
		
		return false;
	}

	private void doSet(CommandSender sender, Pad pad, String[] commands) {
		
	}
	
}
