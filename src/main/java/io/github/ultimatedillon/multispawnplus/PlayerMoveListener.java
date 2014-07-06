package io.github.ultimatedillon.multispawnplus;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public final class PlayerMoveListener implements Listener {
	FileConfiguration config;
	String[] portals;
	
	public PlayerMoveListener(MultiSpawnPlus plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        config = plugin.getConfig();
        portals = plugin.portals;
    }
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Location playerLoc = event.getPlayer().getLocation();
		Block below = playerLoc.subtract(0, 1, 0).getBlock();
		
		for (int i = 0; i < portals.length; i++) {
			String blockLoc = config.getString("locations." + portals[i] + ".location");
			
			String[] arg = blockLoc.split(",");
		    double[] parsed = new double[3];
	        for(int a = 0; a < 2; a++){
	              parsed[a] = Double.parseDouble(arg[a + 1]);
	        }
	        
		    Location loc = new Location(Bukkit.getWorld(arg[0]), parsed[1], parsed[2], parsed[3]);
		    Block portal = loc.getBlock();
		    
			if (below == portal) {
				String destination;
				
				if (config.getString("locations." + portals[i] + ".destination") == "random") {
					Random rand = new Random();
					destination = portals[rand.nextInt(portals.length)];
				} else {
					destination = config.getString("locations." + portals[i] + ".destination");
				}
				
				World world = Bukkit.getWorld(config.getString("spawns." + destination + ".world"));
				int x = config.getInt("spawns." + destination + ".X");
				int y = config.getInt("spawns." + destination + ".Y");
				int z = config.getInt("spawns." + destination + ".Z");
				int yaw = config.getInt("spawns." + destination + ".yaw");
				int pitch = config.getInt("spawns." + destination + ".pitch");
				
				Bukkit.getLogger().info("MultiSpawnPlus: - Teleporting " + event.getPlayer().getName() + " to " + destination
						+ "(" + world + ", " + x + ", " + y + ", " + z + ", " + yaw + ", " + pitch + ")");
				
				if (world == null) {
					event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThat world does not exist!"));
				} else {
					Location target = new Location(world, x, y, z, yaw, pitch);
					event.getPlayer().teleport(target);
				}
			}
		}
	}
}
