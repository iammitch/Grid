package com.mitchdev.bukkit.grid.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.mitchdev.bukkit.grid.CommandHandler;
import com.mitchdev.bukkit.grid.Grid;

public class DisableCommand extends CommandHandler {

	@Override
	public boolean onCommand ( Grid grid, CommandSender sender, Command cmd, String commandLabel, String[] args ) {
		return false;
	}
	
}
