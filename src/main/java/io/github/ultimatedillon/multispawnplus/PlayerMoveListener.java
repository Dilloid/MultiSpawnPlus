package io.github.ultimatedillon.multispawnplus;

import java.util.ArrayList;
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
	MultiSpawnPlus plugin;
	String[] portals;
	String[] allowed;
	String[] group;
	String blockGroup;
	String destination = "";
	Block portal = null;
	Block below = null;
	
	public PlayerMoveListener(MultiSpawnPlus plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        
        this.plugin = plugin;
        allowed = plugin.allowed;
    }
	
	public FileConfiguration getConfig() {
		return plugin.getConfig();
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (getConfig().contains("portals")) {
			Set<String> portalList = getConfig().getConfigurationSection("portals").getKeys(false);
			portals = portalList.toArray(new String[portalList.size()]);
			
			World world = player.getWorld();
			int playerX = player.getLocation().getBlockX();
			int playerY = player.getLocation().getBlockY();
			int playerZ = player.getLocation().getBlockZ();
			
			Location playerLoc = new Location(world, playerX, playerY, playerZ);
			below = playerLoc.subtract(0, 1, 0).getBlock();
			
			if (portals != null) {
				for (int i = 0; i < portals.length; i++) {
					World blockWorld = Bukkit.getWorld(getConfig().getString("portals." + portals[i] + ".world"));
					int blockX = getConfig().getInt("portals." + portals[i] + ".X");
					int blockY = getConfig().getInt("portals." + portals[i] + ".Y");
					int blockZ = getConfig().getInt("portals." + portals[i] + ".Z");
					
				    Location portalLoc = new Location(blockWorld, blockX, blockY, blockZ);
				    portal = portalLoc.getBlock();
				    
				    if (below.getLocation().toString().equalsIgnoreCase(portal.getLocation().toString())) {
				    	Boolean canTeleport = true;
						Bukkit.getLogger().info(player + "triggered the " + portals[i] + " portal!");
						
						if (!getConfig().getString("portals." + portals[i] + ".destination").equalsIgnoreCase("random")) {
							destination = getConfig().getString("portals." + portals[i] + ".destination");
						} else {
							blockGroup = getConfig().getString("portals." + portals[i] + ".spawn-group");
							ArrayList<String> groupList = new ArrayList<String>();
							for (int j = 0; j < allowed.length; j++) {
								if (getConfig().getString("spawns." + allowed[j] + ".spawn-group").equalsIgnoreCase(blockGroup)) {
									groupList.add(allowed[j]);
								}
							}
							group = groupList.toArray(new String[groupList.size()]);
							
							try {
								Random rand = new Random();
								destination = group[rand.nextInt(group.length)];
							} catch (IllegalArgumentException err) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThere are no spawn points "
										+ "in that group!"));
								canTeleport = false;
							}
						}
						
						if (canTeleport == true) {
							World destWorld = Bukkit.getWorld(getConfig().getString("spawns." + destination + ".world"));
							double x = getConfig().getInt("spawns." + destination + ".X") + 0.5;
							double y = getConfig().getInt("spawns." + destination + ".Y");
							double z = getConfig().getInt("spawns." + destination + ".Z") + 0.5;
							int yaw = getConfig().getInt("spawns." + destination + ".yaw");
							int pitch = getConfig().getInt("spawns." + destination + ".pitch");
							
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
				}
			} else {
				Bukkit.getLogger().info("Derp.");
			}
		}
	}
}
