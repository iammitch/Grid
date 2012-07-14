package com.mitchdev.bukkit.grid;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import org.bukkit.material.RedstoneTorch;

/**
 * Represents a teleportation pad.
 * @author Mitch
 *
 */
public class Pad {
	
	/**
	 * Creates a pad from the speficied configuration.
	 * @param grid Reference to the Grid
	 * @param id The name of the pad.
	 * @param padConfig The configuration settings that the pad is being configured from.
	 * @return Return's a configured pad with the specified settings.
	 */
	public static Pad createFromConfiguration(Grid grid, String id, ConfigurationSection padConfig) {
		
		Pad pad = new Pad(grid);
		
		pad.id = id;
		
		pad.password = padConfig.getString("password");
		pad.bounds = AABB.fromString ( padConfig.getString("bounds") );
		pad.disabled = padConfig.getBoolean("disabled");
		pad.owner = padConfig.getString("owner");
		
		for ( String str : padConfig.getStringList("switches") ) {
			String[] location = str.split(",");
			pad.switches.add(new Location(grid.getServer().getWorld(location[0]), Double.parseDouble(location[1]), Double.parseDouble(location[2]), Double.parseDouble(location[3])));
		}
		
		for ( String str : padConfig.getStringList("triggers") ) {
			String[] location = str.split(",");
			pad.triggers.add(new Location(grid.getServer().getWorld(location[0]), Double.parseDouble(location[1]), Double.parseDouble(location[2]), Double.parseDouble(location[3])));
		}
		
		pad.primaryNetwork = padConfig.getString("primaryNetwork");
		if ( pad.primaryNetwork == null ) {
			pad.primaryNetwork = "";
		}
		
		for ( String str : padConfig.getStringList("networks")) {
			if ( str == null || str.length() == 0 ) {
				continue;
			}
			pad.associatedNetworks.add(str);
		}
		
		return pad;
	
	}

	/**
	 * Reference to the grid.
	 */
	Grid grid;
	
	/**
	 * The id of the pad.
	 */
	private String id;
	
	/**
	 * The password that is needed to access the pad.
	 */
	private String password;
	
	/**
	 * The bounds of the pad, used to determine if a player is in the pad or if another pad intersects the given pad.
	 */
	private AABB bounds;
	
	/**
	 * The owner of the pad. This is usually the person who created the pad.
	 */
	private String owner;
	
	/**
	 * Boolean field that indicates if the pad is explicitly disabled.
	 */
	private boolean disabled;
	
	/**
	 * The primary network that the pad belongs to.
	 */
	private String primaryNetwork;
	
	/**
	 * Secondary networks that the pad belongs to.
	 */
	private LinkedList<String> associatedNetworks;
	
	/**
	 * List of switches that need to be powered to use the pad.
	 */
	private LinkedList<Location> switches;
	
	/**
	 * List of triggers that will be fired when the pad is activated.
	 */
	private LinkedList<Location> triggers;
	
	/**
	 * The visibility of the pad.
	 */
	private Visibility visibility;
	
	/**
	 * Creates a new pad.
	 * @param grid Reference to the grid.
	 */
	protected Pad ( Grid grid ) {
		this.visibility = Visibility.Visible;
		this.grid = grid;
		this.switches = new LinkedList<Location>();
		this.triggers = new LinkedList<Location>();
		this.primaryNetwork = "";
		this.associatedNetworks = new LinkedList<String>();
	}
	
	/**
	 * Creates a new pad.
	 * @param grid Reference to the grid.
	 * @param id The ID of the pad.
	 * @param bounds The bounds that make up this pad.
	 * @param password The password that needs to be used to access this pad.
	 */
	public Pad ( Grid grid, String id, AABB bounds, String password ) {
		this.primaryNetwork = "";
		this.grid = grid;
		this.bounds = bounds;
		this.id = id;
		this.password = password;
		this.switches = new LinkedList<Location>();
		this.triggers = new LinkedList<Location>();
		this.associatedNetworks = new LinkedList<String>();
	}
	
	/**
	 * Add a network to the pad's list.
	 * @param name The name of the network to add.
	 * @param isPrimary If true, the new network will replace the existing primary gate. If false, the new network is simply being added as a secondary network.
	 */
	public void addNetwork ( String name, boolean isPrimary ) {
		if ( isPrimary ) {
			if ( primaryNetwork != null ) {
				addNetwork ( primaryNetwork, false );
			}
			primaryNetwork = name;
		}
		else {
			for ( String str : associatedNetworks ) {
				if ( str.equalsIgnoreCase( name ) ) {
					return;
				}
			}
			associatedNetworks.add(name);
		}
	}
	
	/**
	 * Add a switch to the pads list of switches.
	 * @param location
	 */
	public void addSwitch ( Location location ) {
		this.switches.add(location);
	}
	
	/**
	 * Add a trigger to the pads list of triggers.
	 * @param location
	 */
	public void addTrigger(Location location) {
		this.triggers.add(location);
		// Disable the trigger.
		Block block = location.getBlock();
		block.setType(Material.TORCH);
	}
	
	/**
	 * Removes all switches from the pad.
	 */
	public void clearSwitches() {
		this.switches.clear();
	}
	
	/**
	 * Removes all triggers from the pad.
	 */
	public void clearTriggers() {
		this.triggers.clear();
	}
	
	/**
	 * Fires all triggers that are associated with the pad. They will last for 5 seconds.
	 */
	public void fireTriggers() {
		
		for ( Location loc : triggers ) {
			loc.getBlock().setType(Material.REDSTONE_TORCH_ON);
		}
		
		Thread t = new Thread(){
			public void run ( ) {
				try {
					Thread.sleep(5000);
					for ( Location loc : triggers ) {
						loc.getBlock().setType(Material.TORCH);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		
		t.start();
		
	}
	
	/**
	 * Get all secondary networks that are on this pad.
	 * @return
	 */
	public LinkedList<String> getAssociatedNetworks ( ) {
		return this.associatedNetworks;
	}
	
	/**
	 * Get the bounds of the pad.
	 * @return
	 */
	public AABB getBounds ( ) {
		return this.bounds;
	}
	
	/**
	 * Get the pads configuration as a HashMap. This is used to save the state of the pad.
	 * @return A HashMap that contains the maps settings. These will be used to restore the state of the pad.
	 */
	Map<String, Object> getConfiguration ( ) {
		
		Map<String, Object> settings = new HashMap < String, Object > ();
		
		settings.put( "password", password );
		settings.put( "bounds", bounds.toString() );
		settings.put( "disabled", disabled );
		settings.put( "owner", owner );
		
		settings.put( "primaryNetwork", primaryNetwork );
		settings.put( "networks", associatedNetworks.toArray() );
		
		LinkedList<String> sw = new LinkedList<String>();
		for ( Location loc : switches ) {
			sw.add(String.format("%s,%f,%f,%f",loc.getWorld().getName(),loc.getX(),loc.getY(),loc.getZ()));
		}
		
		LinkedList<String> tr = new LinkedList<String>();
		for ( Location loc : triggers ) {
			tr.add(String.format("%s,%f,%f,%f",loc.getWorld().getName(),loc.getX(),loc.getY(),loc.getZ()));
		}
		
		settings.put( "switches", sw.toArray() );
		settings.put( "triggers", tr.toArray() );
		
		return settings;
		
		
	}
	
	/**
	 * Gets the location of the pad.
	 * @return
	 */
	public Location getLocation ( ) {
		return this.bounds.center;
	}
	
	/**
	 * Get the name of the pad.
	 * @return
	 */
	public String getName ( ) {
		return this.id;
	}

	/**
	 * Get all networks associated with this pad.
	 * @return
	 */
	public LinkedList<String> getNetworks ( ) {
		LinkedList<String> nets = new LinkedList<String>();
		nets.add(primaryNetwork);
		nets.addAll(associatedNetworks);
		return nets;
	}

	/**
	 * Get the owner of the pad.
	 * @return Returns the owner of the pad, or null of not defined.
	 */
	public String getOwner( ) {
		return this.owner;
	}

	/**
	 * Get the pads primary network.
	 * @return The primary network that this pad is associated with. If no primary network is set, this will be ''.
	 */
	public String getPrimaryNetwork ( ) {
		return this.primaryNetwork;
	}
	
	/**
	 * Get all switches that are defined on this pad.
	 * @return
	 */
	public LinkedList<Location> getSwitches() {
		return this.switches;
	}

	/**
	 * Get all triggers that are defined on this pad.
	 * @return
	 */
	public LinkedList<Location> getTriggers() {
		return this.triggers;
	}

	/**
	 * Get the visibility of this pad.
	 * @return
	 */
	public Visibility getVisibility() {
		return this.visibility;
	}
	
	/**
	 * Utility function that is used to determine if the switch has the given network on it.
	 * @param string
	 * @return
	 */
	public boolean hasNetwork(String string) {
		if ( primaryNetwork.equalsIgnoreCase(string) ) {
			return true;
		}
		for ( String net : associatedNetworks ) {
			if ( net.equalsIgnoreCase(string)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Function that determines if the pad is enabled. This checks all connected switches and checks that their state is ON.
	 * @return
	 */
	public boolean isEnabled ( ) {
		
		for ( Location loc : switches ) {
			
			Block block = loc.getBlock();
			
			if ( block == null ) {
				return false;
			}
			
			Material mat = block.getType();
			// Can't travel to it if the block is a disabled redstone torch..
			if ( mat.getId() == Material.REDSTONE_TORCH_OFF.getId() || mat.getId() == Material.REDSTONE_LAMP_OFF.getId()) {
				return false;
			}
			
		}
		
		return true;
		
	}
	
}
