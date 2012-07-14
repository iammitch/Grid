package com.mitchdev.bukkit.grid;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class GridListener implements Listener {

	private Grid grid;

	public GridListener(Grid grid) {
		this.grid = grid;
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {

	}

	@EventHandler
	/**
	 * Callback function that is fired whenever a sign is changed. We listen to this method
	 * so that we are able to create a pad if the player places a sign with the right values
	 * on it. 
	 * @param e
	 */
	public void onSignChange(SignChangeEvent e) {

		Player player = e.getPlayer();

		if (e.getLine(0).equalsIgnoreCase("[Grid]")) {

			if (!Permissions.getInstance().hasPermission(player,
					"grid.pad.create")) {
				player.sendMessage(Grid.getChatPrefix()
						+ "You do not have access to this function..");
				e.getBlock().breakNaturally();
				return;

			}

			String size = e.getLine(1);
			String id = e.getLine(2);
			String network = e.getLine(3);

			// Build the location.
			Location loc = e.getBlock().getLocation();
			String[] s = size.split(",");
			int x = Integer.parseInt(s[0]);
			int y = Integer.parseInt(s[1]);
			int z = Integer.parseInt(s[2]);
			loc.setY(loc.getY() + y);

			AABB bounds = new AABB(loc, x, y, z);

			// Check bounds against all pads.
			for (Pad pad : grid.getPads()) {
				if (pad.getBounds().contains(bounds)) {
					// Can't place portal here, it intersects another portal.
					player.sendMessage(Grid.getChatPrefix()
							+ "New pad overlaps an existing pad, aborting..");
					e.getBlock().breakNaturally();
					return;
				}
			}

			// Name check.
			if (network == null || network.trim().length() == 0) {

				// Check that the pad won't clash with a name that we already
				// have
				// on the global network.
				for (Pad pad : grid.getGlobalNetwork().getPads()) {
					if (pad.getName().equalsIgnoreCase(id)) {
						player.sendMessage(Grid.getChatPrefix()
								+ "Pad name conflicts with an existing pad, aborting..");
						e.getBlock().breakNaturally();
						return;
					}
				}

				// If we get here, place the portal.
				Pad pad = new Pad(grid, id, bounds, "");

				grid.getGlobalNetwork().add(pad);

			} else {

				// First check and make sure that we have the permission to
				// create a network pad.

				if (Permissions.getInstance().hasPermission(player,
						"grid.network." + network + ".pad.create")) {
					player.sendMessage(Grid.getChatPrefix()
							+ "You do not have access to this function..");
					e.getBlock().breakNaturally();
					return;
				}

				// Check the network.

				Network net = grid.getNetwork(network);

				if (net == null) {
					player.sendMessage(Grid.getChatPrefix()
							+ "No network with the given name specified, aborting!");
					e.getBlock().breakNaturally();
					return;
				} else {
					for (Pad pad : net.getPads()) {
						if (pad.getName().equalsIgnoreCase(id)) {
							player.sendMessage(Grid.getChatPrefix()
									+ "Pad name conflicts with an existing pad on this network, aborting..");
							e.getBlock().breakNaturally();
							return;
						}
					}
				}

				// If we get here, place the portal.
				Pad pad = new Pad(grid, id, bounds, "");

				pad.addNetwork(network, true);

				net.add(pad);

			}

			e.getBlock().breakNaturally();
			player.sendMessage(Grid.getChatPrefix()
					+ "Successfully created pad!");

		}

	}

}
