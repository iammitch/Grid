package com.mitchdev.bukkit.grid;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class AABB {

	public static AABB fromString ( String string ) {
		String[] parts = string.split(",");
		World world = Grid.getInstance().getServer().getWorld(parts[0]);
		Location loc = new Location ( world, Double.parseDouble( parts[1] ), Double.parseDouble( parts[2] ), Double.parseDouble( parts[3] ) );
		return new AABB ( loc, Integer.parseInt( parts[4] ), Integer.parseInt( parts[5] ), Integer.parseInt( parts[6] ) );
	}
	
	Location center;
	public int width;
	public int height;
	
	public int depth;
	
	public AABB ( Location center, int width, int height, int depth ) {
		this.center = center;
		this.width = width;
		this.height = height;
		this.depth = depth;
	}
	
	public boolean contains ( AABB other ) {
		return false;
	}
	
	public boolean contains ( Location p ) {
		if ( !p.getWorld().equals( center.getWorld() ) ) {
			return false;
		}
		if ( center.getX() - width > p.getX() || center.getX() + 1 + width < p.getX() ) {
			return false;
		}
		if ( center.getY() - height > p.getY() || center.getY() + 1 + height < p.getY() ) {
			return false;
		}
		if ( center.getZ() - depth > p.getZ() || center.getZ() + 1 + depth < p.getZ() ) {
			return false;
		}
		return true;
	}
	
	public boolean contains ( Player player ) {
		return contains ( player.getLocation() );
	}
	
	@Override
	public String toString ( ) {
		return String.format( "%s,%f,%f,%f,%d,%d,%d", center.getWorld().getName(), center.getX(), center.getY(), center.getZ(), width, height, depth );
	}
	
}
