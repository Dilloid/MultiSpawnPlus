package io.github.ultimatedillon.multispawnplus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class MainConfig {
	private FileConfiguration config;
	private File configFile;
	Plugin plugin;
	
	String[] spawns;
	String[] allowed;
	String[] portals;
	
	public MainConfig(MultiSpawnPlus plugin) {
		this.plugin = plugin;
	}
	
	@SuppressWarnings("deprecation")
	public void reloadConfig() {
		if (configFile == null) {
			configFile = new File(plugin.getDataFolder(), "config.yml");
		}
		config = YamlConfiguration.loadConfiguration(configFile);
		
		InputStream defConfigStream = plugin.getResource("config.yml");
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			config.setDefaults(defConfig);
		}
	}
	
	public void copyDefaults() {
		World defaultWorld = Bukkit.getServer().getWorlds().get(0);
		int[] coords = {
			defaultWorld.getSpawnLocation().getBlockX(),
			defaultWorld.getSpawnLocation().getBlockY(),
			defaultWorld.getSpawnLocation().getBlockZ()
		};
		float yaw = defaultWorld.getSpawnLocation().getYaw();
		float pitch = defaultWorld.getSpawnLocation().getPitch();
		
		if (!getConfig().contains("options.random-spawn-on-join")) {
			getConfig().set("options.random-spawn-on-join", false);
		} else if (getConfig().getBoolean("options.random-spawn-on-join") != true) {
			getConfig().set("options.random-spawn-on-join", false);
		}
		
		if (!getConfig().contains("options.first-join-spawn-group")) {
			getConfig().set("options.first-join-spawn-group", "default");
		}
		
		getConfig().set("spawns.default.world", defaultWorld.getName());
		getConfig().set("spawns.default.allow-random-spawn", false);
		getConfig().set("spawns.default.spawn-group", "default");
		getConfig().set("spawns.default.X", coords[0]);
		getConfig().set("spawns.default.Y", coords[1]);
		getConfig().set("spawns.default.Z", coords[2]);
		getConfig().set("spawns.default.yaw", Float.toString(yaw));
		getConfig().set("spawns.default.pitch", Float.toString(pitch));
		
		getConfig().set("portals.default.world", defaultWorld.getName());
		getConfig().set("portals.default.destination", "default");
		getConfig().set("portals.default.spawn-group", "default");
		getConfig().set("portals.default.X", 0);
		getConfig().set("portals.default.Y", 0);
		getConfig().set("portals.default.Z", 0);
		
		Set<String> spawnList = config.getConfigurationSection("spawns").getKeys(false);
		spawns = spawnList.toArray(new String[spawnList.size()]);
		ArrayList<String> allowedList = new ArrayList<String>();
		
		for (int i = 0; i < spawns.length; i++) {
			if (config.getBoolean("spawns." + spawns[i] + ".allow-random-spawn") == true) {
				if (spawns[i] != null && spawns[i] != "null") {
					allowedList.add(spawns[i]);
				}
			}
			
			if (!config.contains("spawns." + spawns[i] + ".world") ||
				config.getString("spawns." + spawns[i] + ".world") == null) {
				config.set("spawns." + spawns[i] + ".world", defaultWorld.getName());
			}
			
			if (!config.contains("spawns." + spawns[i] + ".allow-random-spawn")) {
				config.set("spawns." + spawns[i] + ".allow-random-spawn", new Boolean(false));
			}
			
			if (!config.contains("spawns." + spawns[i] + ".spawn-group") ||
				config.get("spawns." + spawns[i] + ".spawn-group") == null) {
				config.set("spawns." + spawns[i] + ".spawn-group", "default");
			}
			
			if (!config.contains("spawns." + spawns[i] + ".X") ||
				config.get("spawns." + spawns[i] + ".X") == null) {
				config.set("spawns." + spawns[i] + ".X", coords[0]);
			}
			
			if (!config.contains("spawns." + spawns[i] + ".Y") ||
				config.get("spawns." + spawns[i] + ".Y") == null) {
				config.set("spawns." + spawns[i] + ".Y", coords[1]);
			}
			
			if (!config.contains("spawns." + spawns[i] + ".Z") ||
				config.get("spawns." + spawns[i] + ".Z") == null) {
				config.set("spawns." + spawns[i] + ".Z", coords[2]);
			}
			
			if (!config.contains("spawns." + spawns[i] + ".yaw") ||
				config.get("spawns." + spawns[i] + ".yaw") == null) {
				config.set("spawns." + spawns[i] + ".yaw", Float.toString(yaw));
			}
			
			if (!config.contains("spawns." + spawns[i] + ".pitch") ||
				config.get("spawns." + spawns[i] + ".pitch") == null) {
				config.set("spawns." + spawns[i] + ".pitch", Float.toString(pitch));
			}
		}
	
		allowed = allowedList.toArray(new String[allowedList.size()]);
		
		Set<String> portalList = config.getConfigurationSection("portals").getKeys(false);
		portals = portalList.toArray(new String[portalList.size()]);
		
		for (int i = 0; i < portals.length; i++) {
			if (!config.contains("portals." + portals[i] + ".world") ||
				config.getString("portals." + portals[i] + ".world") == null) {
				config.set("portals." + portals[i] + ".world", defaultWorld.getName());
			}
			
			if (!config.contains("portals." + portals[i] + ".destination")) {
				config.set("portals." + portals[i] + ".destination", "default");
			}
			
			if (!config.contains("portals." + portals[i] + ".spawn-group") ||
				config.get("portals." + portals[i] + ".spawn-group") == null) {
				config.set("portals." + portals[i] + ".spawn-group", "default");
			}
			
			if (!config.contains("portals." + portals[i] + ".X") ||
				config.get("portals." + portals[i] + ".X") == null) {
				config.set("portals." + portals[i] + ".X", coords[0]);
			}
			
			if (!config.contains("portals." + portals[i] + ".Y") ||
				config.get("portals." + portals[i] + ".Y") == null) {
				config.set("portals." + portals[i] + ".Y", coords[1]);
			}
			
			if (!config.contains("portals." + portals[i] + ".Z") ||
				config.get("portals." + portals[i] + ".Z") == null) {
				config.set("portals." + portals[i] + ".Z", coords[2]);
			}
		}
	
		if (validFirstGroup() == false) {
			Bukkit.getLogger().info("MultiSpawnPlus: The spawn group you have chosen under 'first-join-spawn-group' "
					+ "doesn't have any random spawn points to use! Disabling random spawning on first join...");
			
			config.set("options.random-spawn-on-join", new Boolean(false));
		}
	}
	
	public boolean validFirstGroup() {
		String firstGroup = getConfig().getString("options.first-join-spawn-group");
		
		for (int i = 0; i < allowed.length; i++) {
			if (getConfig().getString("spawns." + allowed[i] + ".spawn-group").equalsIgnoreCase(firstGroup)) {
				return true;
			}
		}
		return false;
	}
	
	public FileConfiguration getConfig() {
	    if (config == null) {
	        reloadConfig();
	    }
	    return config;
	}
	
	public void saveConfig() {
	    if (config == null || configFile == null) {
	        return;
	    }
	    try {
	        getConfig().save(configFile);
	    } catch (IOException ex) {
	        Bukkit.getLogger().log(Level.SEVERE, "Could not save config to " + configFile, ex);
	    }
	}
}
