package io.github.ultimatedillon.multispawnplus;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
	PlayerConfig playerConfigClass;
	FileConfiguration playerConfig;
	
	FileConfiguration config;
	String[] allowed;
	String[] group;
	
	public PlayerJoinListener(MultiSpawnPlus plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        config = plugin.getConfig();
        allowed = plugin.allowed;
        
        playerConfigClass = new PlayerConfig(plugin);
        playerConfig = playerConfigClass.getConfig();
    }
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		if (!playerConfig.contains("players." + player.getUniqueId().toString())) {
			playerConfig.set("players." + player.getUniqueId().toString() + ".name", player.getName());
			playerConfigClass.saveConfig();
			
			Bukkit.getLogger().info("MultiSpawnPlus: " + player.getName() + " has joined for the first time! "
					+ "Writing player info to players.yml");
			
			if (config.getBoolean("options.random-spawn-on-join") == true) {
				String spawnGroup = config.getString("options.first-join-spawn-group");
				ArrayList<String> groupList = new ArrayList<String>();
				for (int i = 0; i < allowed.length; i++) {
					if (config.getString("spawns." + allowed[i] + ".spawn-group").equalsIgnoreCase(spawnGroup)) {
						groupList.add(allowed[i]);
					}
				}
				group = groupList.toArray(new String[groupList.size()]);
				
				Random rand = new Random();
				int i = rand.nextInt(group.length);
				
				World world = Bukkit.getWorld(config.getString("spawns." + allowed[i] + ".world"));
				double x = config.getInt("spawns." + group[i] + ".X") + 0.5;
				double y = config.getInt("spawns." + group[i] + ".Y");
				double z = config.getInt("spawns." + group[i] + ".Z") + 0.5;
				int yaw = config.getInt("spawns." + group[i] + ".yaw");
				int pitch = config.getInt("spawns." + group[i] + ".pitch");
				
				Bukkit.getLogger().info("MultiSpawnPlus: - Teleporting " + player.getName() + " to " + group[i]
						+ "(" + world + ", " + x + ", " + y + ", " + z + ", " + yaw + ", " + pitch + ")");
				
				if (world == null) {
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThat world does not exist!"));
				} else {
					Location loc = new Location(world, x, y, z, yaw, pitch);
					player.teleport(loc);
				}
			}
		} else {
			Bukkit.getLogger().info("MultiSpawnPlus: " + player.getName() + " has joined!");
			playerConfig.set("players." + player.getUniqueId().toString() + ".name", player.getName());
			playerConfigClass.saveConfig();
		}
	}
}
