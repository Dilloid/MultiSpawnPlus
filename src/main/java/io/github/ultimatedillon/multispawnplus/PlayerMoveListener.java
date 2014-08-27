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
	MultiSpawnPlus plugin;
	String[] portals;
	String blockGroup;
	String destination = "";
	Block portal = null;
	Block below = null;
	
	public PlayerMoveListener(MultiSpawnPlus plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        
        this.plugin = plugin;
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
					
					String portalLocString = getConfig().getString("portals." + portals[i] + ".location");
					String[] portalLocArray = portalLocString.replace(" ", "").split(",");
					
				    Location portalLoc = new Location(blockWorld, 
				    		Double.valueOf(portalLocArray[0]), 
				    		Double.valueOf(portalLocArray[1]), 
				    		Double.valueOf(portalLocArray[2]));
				    
				    portal = portalLoc.getBlock();
				    
				    if (below.getLocation().toString().equalsIgnoreCase(portal.getLocation().toString())) {
				    	boolean canTeleport = true;
						Bukkit.getLogger().info(player.getName() + " triggered the " + portals[i] + " portal!");
						
						boolean randVal = getConfig().getBoolean("portals." + portals[i] + ".random-target");
						
						if (!randVal) {
							destination = getConfig().getString("portals." + portals[i] + ".destination");
						} else {
							blockGroup = getConfig().getString("portals." + portals[i] + ".spawn-group");
							String[] group = plugin.getLocations(true, blockGroup);
							
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
							String locString = getConfig().getString("spawns." + destination + ".location");
							String[] locArray = locString.replace(" ", "").split(",");
							
							if (destWorld == null) {
								event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThat world does not exist!"));
							} else {
								Location target = new Location(destWorld, 
										Double.valueOf(locArray[0]) + 0.5, 
										Double.valueOf(locArray[1]), 
										Double.valueOf(locArray[2]) + 0.5, 
										Integer.valueOf(locArray[3]), 
										Integer.valueOf(locArray[4]));
								
								event.getPlayer().teleport(target);
								
								Bukkit.getLogger().info("[MultiSpawnPlus] Teleporting " + event.getPlayer().getName() + " to " + destination
									+ " (" + locString + " in " + destWorld.getName() + ")");
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
