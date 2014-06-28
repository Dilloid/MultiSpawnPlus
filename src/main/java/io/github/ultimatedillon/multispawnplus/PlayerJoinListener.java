package io.github.ultimatedillon.multispawnplus;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public final class PlayerJoinListener implements Listener {
	FileConfiguration config;
	String[] allowed;
	
	public PlayerJoinListener(MultiSpawnPlus plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        config = plugin.getConfig();
        allowed = plugin.allowed;
    }
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (!event.getPlayer().hasPlayedBefore()) {
			Bukkit.getLogger().info("MultiSpawnPlus: " + event.getPlayer().getName() + " has joined for the first time.");
			
			if (config.getBoolean("options.random-spawn-on-join") == true) {
				Random rand = new Random();
				int i = rand.nextInt(allowed.length);
				
				World world = Bukkit.getWorld(config.getString("spawns." + allowed[i] + ".world"));
				int x = config.getInt("spawns." + allowed[i] + ".X");
				int y = config.getInt("spawns." + allowed[i] + ".Y");
				int z = config.getInt("spawns." + allowed[i] + ".Z");
				int yaw = config.getInt("spawns." + allowed[i] + ".yaw");
				int pitch = config.getInt("spawns." + allowed[i] + ".pitch");
				
				Bukkit.getLogger().info("MultiSpawnPlus: - Teleporting " + event.getPlayer().getName() + " to " + allowed[i]
						+ "(" + world + ", " + x + ", " + y + ", " + z + ", " + yaw + ", " + pitch + ")");
				
				if (world == null) {
					event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThat world does not exist!"));
				} else {
					Location loc = new Location(world, x, y, z, yaw, pitch);
					event.getPlayer().teleport(loc);
				}
			}
		} else {
			Bukkit.getLogger().info("MultiSpawnPlus: " + event.getPlayer().getName() + " has joined!");
		}
	}
}
