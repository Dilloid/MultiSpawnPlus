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
	private PlayerConfig playerConfig;
	private String[] allowed;
	private String[] group;
	
	public PlayerJoinListener(MultiSpawnPlus plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        allowed = plugin.allowed;
        
        playerConfig = new PlayerConfig(plugin);
    }
	
	public FileConfiguration getConfig() {
		return playerConfig.getConfig();
	}
	
	public void saveConfig() {
		playerConfig.saveConfig();
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
				String spawnGroup = getConfig().getString("options.first-join-spawn-group");
				ArrayList<String> groupList = new ArrayList<String>();
				for (int i = 0; i < allowed.length; i++) {
					if (getConfig().getString("spawns." + allowed[i] + ".spawn-group").equalsIgnoreCase(spawnGroup)) {
						groupList.add(allowed[i]);
					}
				}
				group = groupList.toArray(new String[groupList.size()]);
				
				Random rand = new Random();
				int i = rand.nextInt(group.length);
				
				World world = Bukkit.getWorld(getConfig().getString("spawns." + allowed[i] + ".world"));
				double x = getConfig().getInt("spawns." + group[i] + ".X") + 0.5;
				double y = getConfig().getInt("spawns." + group[i] + ".Y");
				double z = getConfig().getInt("spawns." + group[i] + ".Z") + 0.5;
				int yaw = getConfig().getInt("spawns." + group[i] + ".yaw");
				int pitch = getConfig().getInt("spawns." + group[i] + ".pitch");
				
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
			getConfig().set("players." + player.getUniqueId().toString() + ".name", player.getName());
			saveConfig();
		}
	}
}
