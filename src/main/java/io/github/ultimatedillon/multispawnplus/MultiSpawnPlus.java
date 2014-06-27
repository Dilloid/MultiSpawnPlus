package io.github.ultimatedillon.multispawnplus;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public final class MultiSpawnPlus extends JavaPlugin {
	@Override
	public void onEnable() {
        new PlayerJoinListener(this);
        
        LoadConfig();
    }
	
	public void LoadConfig() {
		World defaultWorld = Bukkit.getServer().getWorlds().get(0);
		
		int[] coords = {
			defaultWorld.getSpawnLocation().getBlockX(),
			defaultWorld.getSpawnLocation().getBlockY(),
			defaultWorld.getSpawnLocation().getBlockZ()
		};
		
		getConfig().addDefault("spawns.default.world", defaultWorld.getName());
		getConfig().addDefault("spawns.default.X", coords[0]);
		getConfig().addDefault("spawns.default.Y", coords[1]);
		getConfig().addDefault("spawns.default.Z", coords[2]);
		
		getConfig().options().copyDefaults(true);
        saveConfig();
	}
}
