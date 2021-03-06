package com.mitchdev.bukkit.grid;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.block.Block;

import com.mitchdev.bukkit.grid.commands.*;

public class Grid extends JavaPlugin implements Listener {

	private static Grid instance;

	public static String getChatPrefix() {
		return "[" + ChatColor.YELLOW + "Grid" + ChatColor.RESET + "] ";
	}

	public static Grid getInstance() {
		return instance;
	}
	
	private Network globalNetwork;
	private LinkedList<Network> networks;
	private FileConfiguration padConfiguration = null;
	private CommandManager commands;
	private GridListener listener;

	private File padConfigFile = null;

	public void addNetwork(Network net) {
		this.networks.add(net);		
	}

	public Network getNetwork ( String name ) {
		if ( name.equalsIgnoreCase("global")) {
			return globalNetwork;
		}
		for ( Network network : networks ) {
			if ( network.getId ( ).equalsIgnoreCase(name) ) {
				return network;
			}
		}
		return null;
	}

	public LinkedList<Network> getNetworks ( ) {
		return this.networks;
	}

	private Pad getPad(Location loc) {
		for ( Pad pad : globalNetwork.getPads() ) {
			if ( pad.getBounds().contains(loc) ) {
				return pad;
			}
		}
		for ( Network network : networks ) {
			for ( Pad pad : network.getPads() ) {
				if ( pad.getBounds().contains(loc)) {
					return pad;
				}
			}
		}
		return null;
	}

	public Pad getPad(Player sender) {
		return getPad(sender.getLocation());
	}

	public Pad getPad(String n) {
		for ( Pad pad : globalNetwork.getPads() ) {
			if ( pad.getName().equalsIgnoreCase(n) ) {
				return pad;
			}
		}
		for ( Network network : networks ) {
			for ( Pad pad : network.getPads() ) {
				if ( pad.getName().equalsIgnoreCase(n) ) {
					return pad;
				}
			}
		}
		return null;
	}

	/**
	 * Get all pads that are defined in the global state.
	 * @return
	 */
	public LinkedList<Pad> getPads() {
		return globalNetwork.getPads();
	}

	/**
	 * Loads the configuration as defined from the file.
	 */
	private void loadConfiguration() {

		ConfigurationSection section = padConfiguration
				.getConfigurationSection("pads");

		for (String key : section.getKeys(false)) {

			ConfigurationSection padConfig = section
					.getConfigurationSection(key);
			globalNetwork.add(Pad.createFromConfiguration(this, key, padConfig));

		}
		
		ConfigurationSection n = padConfiguration.getConfigurationSection("networks");
		
		for ( String key : n.getKeys(false)) {
			networks.add(Network.createFromConfiguration(this, key, n.getConfigurationSection(key)));
		}

	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if ( sender instanceof Player ) {
			String argstr = "";
			for ( int i = 0; i < args.length; i++ ) {
				argstr += args[i];
				if ( i + 1 != args.length ) {
					argstr += " ";
				}
			}
			this.getLogger().log( Level.INFO, "[PLAYER_COMMAND] " + ((Player)sender).getName() + ": /" + commandLabel + " " + argstr );
		}
		return commands.handle ( this, sender, cmd, commandLabel, args );
	}

	@Override
	public void onDisable() {
		saveConfiguration();
	}
	
	@Override
	public void onEnable() {
		Grid.instance = this;
		globalNetwork = new Network("global");
		this.listener = new GridListener(this);
		getServer().getPluginManager().registerEvents(listener, this);
		// Load the configuration file.
		networks = new LinkedList<Network>();
		if (padConfigFile == null) {
			padConfigFile = new File(getDataFolder(), "pads.yml");
		}
		padConfiguration = YamlConfiguration.loadConfiguration(padConfigFile);
		loadConfiguration();
		// Setup the command handler.
		commands = new CommandManager();
		commands.addHandler(new ListPadsCommand());
		commands.addHandler(new SetCommand());
		commands.addHandler(new TeleportCommand());
		commands.addHandler(new DisableCommand());
		commands.addHandler(new EnableCommand());
		commands.addHandler(new InfoCommand());
		commands.addHandler(new NetworkCommand());
		commands.addHandler(new SwitchCommand());
		commands.addHandler(new TriggerCommand());
		commands.addHandler(new PadCommand());
		commands.addHandler(new TestCommand());
	}

	public void removePad(String name) {
		for ( Pad pad : globalNetwork.getPads() ) {
			if ( pad.getName().equalsIgnoreCase(name)) {
				globalNetwork.removePad(pad);
				return;
			}
		}
	}

	private void saveConfiguration() {

		padConfiguration = new YamlConfiguration();

		Map<String, Map<String, Object>> cfg = new HashMap<String, Map<String, Object>>();

		for (Pad pad : globalNetwork.getPads()) {

			cfg.put( pad.getName(), pad.getConfiguration() );

		}

		padConfiguration.createSection("pads", cfg);
		
		Map<String, Map<String, Object>> nets = new HashMap<String, Map<String, Object>>();
		for ( Network network : networks ) {
			nets.put(network.getId(), network.getConfiguration());
		}
		
		padConfiguration.createSection("networks", nets);

		try {
			padConfiguration.save(padConfigFile);
		} catch (IOException e) {
			this.getLogger().log(Level.SEVERE,
					"Could not save config to " + "pads.yml", e);
		}

	}

	public Network getGlobalNetwork() {
		return this.globalNetwork;
	}

	public LinkedList<Pad> getAssociatedPads(String id) {
		LinkedList<Pad> pads = new LinkedList<Pad>();
		for ( Network net : networks ) {
			if ( net.getId().equalsIgnoreCase(id)) {
				continue;
			}
			for ( Pad pad : net.getPads() ) {
				if ( pad.hasNetwork(id)) {
					pads.add(pad);
				}
			}
		}
		return pads;
	}

}
