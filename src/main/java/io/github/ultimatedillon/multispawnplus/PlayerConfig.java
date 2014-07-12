package io.github.ultimatedillon.multispawnplus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class PlayerConfig {
	private FileConfiguration playerConfig;
	private File playerConfigFile;
	Plugin plugin;
	
	public PlayerConfig(MultiSpawnPlus plugin) {
		this.plugin = plugin;
	}
	
	@SuppressWarnings("deprecation")
	public void reloadConfig() {
		if (playerConfigFile == null) {
			playerConfigFile = new File(plugin.getDataFolder(), "players.yml");
		}
		playerConfig = YamlConfiguration.loadConfiguration(playerConfigFile);
		
		InputStream defConfigStream = plugin.getResource("players.yml");
		if (defConfigStream != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
			playerConfig.setDefaults(defConfig);
		}
	}
	
	public FileConfiguration getConfig() {
	    if (playerConfig == null) {
	        reloadConfig();
	    }
	    return playerConfig;
	}
	
	public void saveConfig() {
	    if (playerConfig == null || playerConfigFile == null) {
	        return;
	    }
	    try {
	        getConfig().save(playerConfigFile);
	    } catch (IOException ex) {
	        Bukkit.getLogger().log(Level.SEVERE, "Could not save config to " + playerConfigFile, ex);
	    }
	}
}
