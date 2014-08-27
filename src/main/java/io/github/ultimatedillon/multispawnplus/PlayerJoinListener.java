package io.github.ultimatedillon.multispawnplus;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
	private MultiSpawnPlus plugin;
	private PlayerConfig playerConfig;
	
	public PlayerJoinListener(MultiSpawnPlus plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
        this.plugin = plugin;
        
        playerConfig = new PlayerConfig(plugin);
    }
	
	public FileConfiguration getConfig() {
		return playerConfig.getConfig();
	}
	
	public void saveConfig() {
		playerConfig.saveConfig();
	}
	
	public void sendMessage(CommandSender sender, String message) {
		sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		if (!getConfig().contains("players." + player.getUniqueId().toString())) {
			getConfig().set("players." + player.getUniqueId().toString() + ".name", player.getName());
			saveConfig();
			
			Bukkit.getLogger().info("MultiSpawnPlus: " + player.getName() + " has joined for the first time! "
					+ "Writing player info to players.yml");
			
			if (getConfig().getBoolean("options.random-spawn-on-join") == true) {
				String dest = "";
				boolean canTeleport = true;
				
				String spawnGroup = getConfig().getString("options.first-join-spawn-group");
				String[] groups = plugin.getLocations(true, spawnGroup);
				
				try {
					Random rand = new Random();
					dest = groups[rand.nextInt(groups.length)];
				} catch (IllegalArgumentException err) {
					sendMessage(player, "&cThere are no spawn points in group &f" + dest + "&b!");
					canTeleport = false;
				}
				
				World world = Bukkit.getWorld(getConfig().getString("spawns." + dest + ".world"));
				String locString = getConfig().getString("spawns." + dest + ".location");
				String[] locArray = locString.replace(" ", "").split(",");
				
				if (canTeleport) {
					if (world == null) {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThat world does not exist!"));
					} else {
						Bukkit.getLogger().info("MultiSpawnPlus: - Teleporting " 
								+ player.getName() 
								+ " to " + dest
								+ "(" + world + ", " 
								+ locArray[0] + ", " 
								+ locArray[1] + ", " 
								+ locArray[2] + ", " 
								+ locArray[3] + ", " 
								+ locArray[4] + ")");
						
						Location loc = new Location(world, 
								Double.valueOf(locArray[0]) + 0.5, 
								Double.valueOf(locArray[1]), 
								Double.valueOf(locArray[2]) + 0.5, 
								Integer.valueOf(locArray[3]), 
								Integer.valueOf(locArray[4]));
						
						player.teleport(loc);
					}
				}
			}
		} else {
			Bukkit.getLogger().info("MultiSpawnPlus: " + player.getName() + " has joined!");
			getConfig().set("players." + player.getUniqueId().toString() + ".name", player.getName());
			saveConfig();
		}
	}
}
