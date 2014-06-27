package io.github.ultimatedillon.multispawnplus;

import org.bukkit.plugin.java.JavaPlugin;

public final class MultiSpawnPlus extends JavaPlugin {
	@Override
	public void onEnable() {
        new PlayerJoinListener(this);
    }
}
