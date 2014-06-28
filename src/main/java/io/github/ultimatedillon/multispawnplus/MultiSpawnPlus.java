package io.github.ultimatedillon.multispawnplus;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
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
		
		if (!new File(getDataFolder(), "config.yml").exists()) {
			getConfig().addDefault("spawns.default.world", defaultWorld.getName());
			getConfig().addDefault("spawns.default.allow-random-spawn", true);
			getConfig().addDefault("spawns.default.X", coords[0]);
			getConfig().addDefault("spawns.default.Y", coords[1]);
			getConfig().addDefault("spawns.default.Z", coords[2]);
			
			getConfig().options().copyDefaults(true);
			saveConfig();
		}
		
		getConfig().set("random-spawns", null);
		Set<String> spawnList = getConfig().getConfigurationSection("spawns").getKeys(false);
		String[] spawns = spawnList.toArray(new String[spawnList.size()]);
		ArrayList<String> allowedList = new ArrayList<String>();
		
		for (int i = 0; i < spawns.length; i++) {
			if (getConfig().getBoolean("spawns." + spawns[i] + ".allow-random-spawn") == true) {
				if (spawns[i] != null && spawns[i] != "null") {
					allowedList.add(spawns[i]);
					getLogger().info("MultiSpawnPlus - Item Added: " + spawns[i]);
				}
			}
		}
		
		String[] allowed = allowedList.toArray(new String[allowedList.size()]);
		getConfig().set("random-spawns.allowed", Arrays.asList(allowed));
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
						if (args.length < 3) {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlease enter a name for this spawnpoint, and whether this new players can spawn here."));
							player.sendMessage("Usage: /multispawnplus add <name> <[true|false]>");
						} else if (args.length > 3) {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cToo many arguments!"));
							player.sendMessage("Usage: /multispawnplus add <name> <[true|false]>");
						} else if (!getConfig().contains("spawns." + args[1])) {
							if (args[2] != "true" || args[2] != "false") {
								getConfig().set("spawns." + args[1] + ".world", player.getWorld().getName());
								getConfig().set("spawns." + args[1] + ".allow-random-spawn", new Boolean(args[2]));
								getConfig().set("spawns." + args[1] + ".X", player.getLocation().getBlockX());
								getConfig().set("spawns." + args[1] + ".Y", player.getLocation().getBlockY());
								getConfig().set("spawns." + args[1] + ".Z", player.getLocation().getBlockZ());
								saveConfig();
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bSpawnpoint &f" + args[1] + " &bhas been created!"));
							} else {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cSyntax Error!"));
								player.sendMessage("Usage: /multispawnplus add <name> <[true|false]>");
							}
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
							player.sendMessage("Usage: /multispawnplus delete <name>");
						} else {
							if (!getConfig().contains(args[1])) {
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
				
				} else if (args[0].equalsIgnoreCase("spawn")) {
					if (player.hasPermission("multispawnplus.spawn")) {
						if (args.length < 2) {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlease enter a spawnpoint."));
						} else if (args.length > 2) {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cToo many arguments!"));
							player.sendMessage("Usage: /multispawnplus spawn <name>");
						} else {
							if (!getConfig().contains(args[1])) {
								World world = Bukkit.getWorld(getConfig().get("spawns." + args[1] + ".world").toString());
								int x = getConfig().getInt("spawns." + args[1] + ".X");
								int y = getConfig().getInt("spawns." + args[1] + ".Y");
								int z = getConfig().getInt("spawns." + args[1] + ".Z");
								
								Location loc = new Location(world, x, y, z);
								player.teleport(loc);
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
