package io.github.ultimatedillon.multispawnplus;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class MultiSpawnPlus extends JavaPlugin {
	public void PlayerJoinEvent(Player player, String message) {
		getLogger().info("MultiSpawnPlus: " + player + " has joined!");
	}
}
