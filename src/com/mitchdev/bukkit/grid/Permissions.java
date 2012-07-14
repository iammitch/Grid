package com.mitchdev.bukkit.grid;

import org.bukkit.entity.Player;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.bukkit.PermissionsEx;

public class Permissions {

	private static Permissions permissions;
	private PermissionManager pex;
	
	private Permissions() {
		
		pex = PermissionsEx.getPermissionManager();
		
	}
	
	public static Permissions getInstance() {
		if ( permissions == null ) {
			permissions = new Permissions();
		}
		return permissions;
	}
	
	public boolean hasPermission ( Player player, String permission ) {
		return pex.has( player, permission );
	}
	
}
