package com.mitchdev.bukkit.grid;

import java.util.LinkedList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandManager {
	
	LinkedList<CommandHandler> commands;
	
	public CommandManager ( ) {
		commands = new LinkedList<CommandHandler>();
	}
	
	public void addHandler ( CommandHandler command ) {
		commands.add(command);		
	}

	public boolean handle(Grid grid, CommandSender sender, Command cmd, String commandLabel, String[] args) {
		for ( CommandHandler c : commands ) {
			if ( c.onCommand ( grid, sender, cmd, commandLabel, args ) ) {
				return true;
			}
		}
		return false;
	}

}
