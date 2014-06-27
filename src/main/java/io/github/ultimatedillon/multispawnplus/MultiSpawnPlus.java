package io.github.ultimatedillon.multispawnplus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
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
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("multispawnplus")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (args.length < 1) {
					return false;
				}
				
				if (args[0].equalsIgnoreCase("add")) {
					if (player.hasPermission("multispawnplus.delete")) {
						if (args.length < 2) {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlease enter a name for this spawnpoint."));
						} else if (args.length > 2) {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cToo many arguments!"));
							return false;
						} else if (!getConfig().contains("spawns." + args[1])) {
							getConfig().set("spawns." + args[1] + ".world", player.getWorld().getName());
							getConfig().set("spawns." + args[1] + ".X", player.getLocation().getBlockX());
							getConfig().set("spawns." + args[1] + ".Y", player.getLocation().getBlockY());
							getConfig().set("spawns." + args[1] + ".Z", player.getLocation().getBlockZ());
							saveConfig();
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bSpawnpoint &f" + args[1] + " &bhas been created!"));
						} else {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cA spawnpoint with that name already exists!"));
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUse &f/multispawnplus delete <name> &bto delete existing spawnpoints."));
						}
					} else {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have permission to do this"));
					}
				
				} else if (args[0].equalsIgnoreCase("delete")) {
					if (player.hasPermission("multispawnplus.delete")) {
						if (args.length < 2) {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlease enter a spawnpoint to delete."));
						} else if (args.length > 2) {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cToo many arguments!"));
							return false;
						} else {
							if (getConfig().contains(args[1])) {
								getConfig().set("spawns." + args[1], null);
								saveConfig();
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bSpawnpoint &f" + args[1] + " &bdeleted."));
							} else {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThat spawnpoint doesn't exist."));
							}
						}
					} else {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have permission to do this"));
					}
				
				} else if (args[0].equalsIgnoreCase("reload")) {
					if (player.hasPermission("multispawnplus.reload")) {
						reloadConfig();
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bMultiSpawnPlus config reloaded!"));
					} else {
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have permission to do this"));
					}
				
				} else {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInvalid argument!"));
					return false;
				}
			} else {
				sender.sendMessage("This command can only be run by a player");
			}
			
			return true;
		}
		
		return false;
	}
}
