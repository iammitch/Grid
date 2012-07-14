package com.mitchdev.bukkit.grid;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

public class Network {

	String id;
	
	String displayName;
	
	LinkedList<String> owners;
	LinkedList<String> allowedPlayers;
	
	boolean disabled;
	LinkedList<Pad> pads;
	
	private String entryPad;
	
	private Visibility visibility;
	
	public Network ( String id ) {
		this.id = id;
		this.displayName = "";
		this.owners = new LinkedList<String>();
		this.allowedPlayers = new LinkedList<String>();
		this.pads = new LinkedList<Pad>();
		this.entryPad = "";
	}
	
	public void addOwner ( String owner ) {
		this.owners.add(owner);
	}
	
	public Visibility getVisibility ( ) {
		return this.visibility;
	}
	
	public String getId ( ) {
		return id;
	}
	
	public LinkedList<Pad> getPads ( ) {
		return this.pads;
	}
	
	public void addPad ( Pad pad ) {
		this.pads.add(pad);
	}
	
	public void removePad ( Pad pad ) {
		this.pads.remove(pad);
	}
	
	public void removePad ( String name ) {
		for ( Pad pad : pads ) {
			if ( pad.getName().equalsIgnoreCase(name)) {
				pads.remove(pad);
				return;
			}
		}
	}
	
	public String getDisplayName ( ) {
		return this.displayName;
	}
	
	public void setDisplayName ( String value ) {
		this.displayName = value;
	}

	public Map<String, Object> getConfiguration() {
		Map<String, Object> settings = new HashMap < String, Object > ();
		
		settings.put( "disabled", disabled );
		settings.put( "displayName", displayName );
		settings.put( "entryPad", entryPad );
		
		Map<String, Map<String, Object>> cfg = new HashMap<String, Map<String, Object>>();

		for (Pad pad : pads) {
			cfg.put(pad.getName(), pad.getConfiguration());
		}
		
		settings.put("pads", cfg);
		
		return settings;
	}
	
	public static Network createFromConfiguration ( Grid grid, String id, ConfigurationSection c ) {
		
		Network net = new Network ( id );
		
		net.setDisplayName( c.getString("displayName") );
		net.entryPad = c.getString("entryPad");
		
		// Settings..
		
		// Create our pads.
		for ( String key : c.getConfigurationSection("pads").getKeys(false)) {
			net.pads.add(Pad.createFromConfiguration( grid, key, c.getConfigurationSection("pads").getConfigurationSection(key)));
		}
		
		return net;
		
	}

	public Pad getEntryPad() {
		if ( this.entryPad == null || this.entryPad.length() == 0 ) {
			return null;
		}
		else {
			return this.getPad(this.entryPad);
		}
	}
	
	public Pad getPad ( String name ) {
		for ( Pad pad : pads ) {
			if ( pad.getName().equalsIgnoreCase(name)) { 
				return pad;
			}
		}
		return null;
	}

	public void setGateway(String string) {
		this.entryPad = string;		
	}
	
}
