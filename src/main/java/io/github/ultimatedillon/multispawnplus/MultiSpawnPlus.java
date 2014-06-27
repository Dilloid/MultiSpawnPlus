package io.github.ultimatedillon.multispawnplus;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class MultiSpawnPlus extends JavaPlugin {
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		getLogger().info("MultiSpawnPlus: " + event.getPlayer() + " has joined!");
	}
}
