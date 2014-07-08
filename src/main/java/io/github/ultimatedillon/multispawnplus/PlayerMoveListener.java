package io.github.ultimatedillon.multispawnplus;

import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {
	FileConfiguration config;
	String[] portals;
	String[] allowed;
	String destination = "";
	Block portal = null;
	Block below = null;
	
	public PlayerMoveListener(MultiSpawnPlus plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        config = plugin.getConfig();
        allowed = plugin.allowed;
    }
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (config.contains("portals")) {
			Set<String> portalList = config.getConfigurationSection("portals").getKeys(false);
			portals = portalList.toArray(new String[portalList.size()]);
			
			World world = player.getWorld();
			int playerX = player.getLocation().getBlockX();
			int playerY = player.getLocation().getBlockY();
			int playerZ = player.getLocation().getBlockZ();
			
			Location playerLoc = new Location(world, playerX, playerY, playerZ);
			below = playerLoc.subtract(0, 1, 0).getBlock();
			
			if (portals != null) {
				for (int i = 0; i < portals.length; i++) {
					World blockWorld = Bukkit.getWorld(config.getString("portals." + portals[i] + ".world"));
					int blockX = config.getInt("portals." + portals[i] + ".X");
					int blockY = config.getInt("portals." + portals[i] + ".Y");
					int blockZ = config.getInt("portals." + portals[i] + ".Z");
					
				    Location portalLoc = new Location(blockWorld, blockX, blockY, blockZ);
				    portal = portalLoc.getBlock();
				    
				    if (below.getLocation().toString().equalsIgnoreCase(portal.getLocation().toString())) {
						Bukkit.getLogger().info("Portal " + portals[i] + " Triggered!");
						
						if (!config.getString("portals." + portals[i] + ".destination").equalsIgnoreCase("random")) {
							destination = config.getString("portals." + portals[i] + ".destination");
						} else {
							Random rand = new Random();
							destination = allowed[rand.nextInt(allowed.length)];
						}
						
						Bukkit.getLogger().info("Destination: " + destination);
						
						World destWorld = Bukkit.getWorld(config.getString("spawns." + destination + ".world"));
						int x = config.getInt("spawns." + destination + ".X");
						int y = config.getInt("spawns." + destination + ".Y");
						int z = config.getInt("spawns." + destination + ".Z");
						int yaw = config.getInt("spawns." + destination + ".yaw");
						int pitch = config.getInt("spawns." + destination + ".pitch");
						
						if (destWorld == null) {
							event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThat world does not exist!"));
						} else {
							Location target = new Location(destWorld, x, y, z, yaw, pitch);
							event.getPlayer().teleport(target);
							
							Bukkit.getLogger().info("MultiSpawnPlus: - Teleporting " + event.getPlayer().getName() + " to " + destination
								+ "(" + destWorld + ", " + x + ", " + y + ", " + z + ", " + yaw + ", " + pitch + ")");
						}
					}
				}
			} else {
				Bukkit.getLogger().info("Derp.");
			}
		}
	}
}
