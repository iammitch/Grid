package com.mitchdev.bukkit.grid;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class CommandHandler {

	protected CommandHandler() {	
	}
	
	public abstract boolean onCommand ( Grid grid, CommandSender sender, Command cmd, String commandLabel, String[] args );
	
}
