package io.github.ultimatedillon.multispawnplus;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
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
	String[] spawns;
	String[] allowed;
	
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
			getConfig().addDefault("options.random-spawn-on-join", false);
			getConfig().addDefault("spawns.default.world", defaultWorld.getName());
			getConfig().addDefault("spawns.default.allow-random-spawn", true);
			getConfig().addDefault("spawns.default.X", coords[0]);
			getConfig().addDefault("spawns.default.Y", coords[1]);
			getConfig().addDefault("spawns.default.Z", coords[2]);			
			
			getConfig().options().copyDefaults(true);
		}
		
		ReloadRandoms();
		saveConfig();
	}
	
	public void ReloadRandoms() {
		getConfig().set("random-spawns", null);
		Set<String> spawnList = getConfig().getConfigurationSection("spawns").getKeys(false);
		spawns = spawnList.toArray(new String[spawnList.size()]);
		ArrayList<String> allowedList = new ArrayList<String>();
		
		for (int i = 0; i < spawns.length; i++) {
			if (getConfig().getBoolean("spawns." + spawns[i] + ".allow-random-spawn") == true) {
				if (spawns[i] != null && spawns[i] != "null") {
					allowedList.add(spawns[i]);
					getLogger().info("MultiSpawnPlus - Random Spawn Added: " + spawns[i]);
				}
			}
		}
		
		allowed = allowedList.toArray(new String[allowedList.size()]);
		
		if (getConfig().getBoolean("options.random-spawn-on-join") != true) {
			getConfig().set("options.random-spawn-on-join", false);
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("multispawnplus")) {
			if (args.length < 1) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b#####################"));
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eMultiSpawnPlus v0.3.2"));
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eby UltimateDillon"));
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b#####################"));
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "Use &6/msp help &ffor command syntax."));
			} else {
				
				//region Add
				if (args[0].equalsIgnoreCase("add")) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						
						if (player.hasPermission("multispawnplus.delete")) {
							if (args.length < 3) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlease enter a name for this spawnpoint, and whether it can be used as a random spawn."));
								player.sendMessage("Usage: /msp add <name> [true|false]");
							} else if (args.length > 3) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cToo many arguments!"));
								player.sendMessage("Usage: /msp add <name> [true|false]");
							} else {
								if (!getConfig().contains("spawns." + args[1])) {
									if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false")) {
										getConfig().set("spawns." + args[1] + ".world", player.getWorld().getName());
										getConfig().set("spawns." + args[1] + ".allow-random-spawn", new Boolean(args[2]));
										getConfig().set("spawns." + args[1] + ".X", player.getLocation().getBlockX());
										getConfig().set("spawns." + args[1] + ".Y", player.getLocation().getBlockY());
										getConfig().set("spawns." + args[1] + ".Z", player.getLocation().getBlockZ());
										saveConfig();
										player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bSpawnpoint &f" + args[1] + " &bhas been created!"));
									} else if (!args[2].equalsIgnoreCase("true") && !args[2].equalsIgnoreCase("false")) {
										player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cSyntax Error!"));
										player.sendMessage("Usage: /msp add <name> [true|false]");
									} else {
										player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cDerp. Something went wrong!"));
									}
								} else {
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cA spawnpoint with that name already exists!"));
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUse &f/msp delete <name> &bto delete existing spawnpoints."));
								}
							}
						} else {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have permission to do this"));
						}
					} else {
						sender.sendMessage("MultiSpawnPlus: This command can only be run by a player");
					}
				//endregion
				
				//region Delete
				} else if (args[0].equalsIgnoreCase("delete")) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						
						if (player.hasPermission("multispawnplus.delete")) {
							if (args.length < 2) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlease enter a spawnpoint to delete."));
							} else if (args.length > 2) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cToo many arguments!"));
								player.sendMessage("Usage: /msp delete <name>");
							} else {
								if (!getConfig().contains(args[1])) {
									getConfig().set("spawns." + args[1], null);
									saveConfig();
									ReloadRandoms();
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bSpawnpoint &f" + args[1] + " &bdeleted."));
								} else {
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThat spawnpoint doesn't exist."));
								}
							}
						} else {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have permission to do this"));
						}
					} else {
						if (args.length < 2) {
							sender.sendMessage("MultiSpawnPlus: Please enter a spawnpoint to delete.");
						} else if (args.length > 2) {
							sender.sendMessage("MultiSpawnPlus: Too many arguments!");
							sender.sendMessage("MultiSpawnPlus: Usage: /msp delete <name>");
						} else {
							if (!getConfig().contains(args[1])) {
								getConfig().set("spawns." + args[1], null);
								saveConfig();
								ReloadRandoms();
								sender.sendMessage("MultiSpawnPlus: Spawnpoint " + args[1] + " deleted.");
							} else {
								sender.sendMessage("MultiSpawnPlus: That spawnpoint doesn't exist.");
							}
						}
					}
				//endregion
				
				//region Spawn
				} else if (args[0].equalsIgnoreCase("spawn")) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						
						if (player.hasPermission("multispawnplus.spawn")) {
							if (args.length < 2) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlease enter a spawnpoint."));
							} else if (args.length > 2) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cToo many arguments!"));
								player.sendMessage("Usage: /msp spawn <name>");
							} else {
								if (getConfig().contains("spawns." + args[1])) {
									World world = Bukkit.getWorld(getConfig().get("spawns." + args[1] + ".world").toString());
									int x = getConfig().getInt("spawns." + args[1] + ".X");
									int y = getConfig().getInt("spawns." + args[1] + ".Y");
									int z = getConfig().getInt("spawns." + args[1] + ".Z");
									
									if (world == null) {
										player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThat world does not exist!"));
									} else {
										Location loc = new Location(world, x, y, z);
										player.teleport(loc);
									}
								} else {
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThat spawnpoint doesn't exist."));
								}
							}
						} else {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have permission to do this"));
						}
					} else {
						sender.sendMessage("MultiSpawnPlus: This command can only be run by a player");
					}
				//endregion
				
				//region Random
				} else if (args[0].equalsIgnoreCase("random")) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						
						if (player.hasPermission("multispawnplus.random")) {
							if (args.length < 1) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlease enter a spawnpoint."));
							} else if (args.length > 1) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cToo many arguments!"));
								player.sendMessage("Usage: /msp random");
							} else {
								Random rand = new Random();
								int i = rand.nextInt(allowed.length);
								
								World world = Bukkit.getWorld(getConfig().getString("spawns." + allowed[i] + ".world"));
								int x = getConfig().getInt("spawns." + allowed[i] + ".X");
								int y = getConfig().getInt("spawns." + allowed[i] + ".Y");
								int z = getConfig().getInt("spawns." + allowed[i] + ".Z");
								
								getLogger().info(world + ", " + x + ", " + y + ", " + z);
								
								if (world == null) {
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThat world does not exist!"));
								} else {
									Location loc = new Location(world, x, y, z);
									player.teleport(loc);
								}
							}
						} else {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have permission to do this"));
						}
					} else {
						sender.sendMessage("MultiSpawnPlus: This command can only be run by a player");
					}
				//endregion
				
				//region List
				} else if (args[0].equalsIgnoreCase("list")) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						
						if (player.hasPermission("multispawnplus.list")) {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6All spawn points in order of creation:"));
							
							for (int i = 0; i < spawns.length; i++) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "- &b" + spawns[i]));
							}
						} else {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have permission to do this"));
						}
					} else {
						sender.sendMessage("MultiSpawnPlus: All spawn points in order of creation:");
						
						for (int i = 0; i < spawns.length; i++) {
							sender.sendMessage("- " + spawns[i]);
						}
					}
				//endregion
					
				//region Reload
				} else if (args[0].equalsIgnoreCase("reload")) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						
						if (player.hasPermission("multispawnplus.reload")) {
							reloadConfig();
							ReloadRandoms();
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bMultiSpawnPlus config reloaded!"));
						} else {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have permission to do this"));
						}
					} else {
						reloadConfig();
						ReloadRandoms();
						sender.sendMessage("MultiSpawnPlus: Config reloaded!");
					}
				//endregion
				
				//region Help
				} else if (args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						
						if (player.hasPermission("multispawnplus.help")) {
							return false;
						} else {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have permission to do this"));
						}
					} else {
						return false;
					}
				//endregion
				
				} else {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInvalid argument!"));
					} else {
						sender.sendMessage("MultiSpawnPlus: Invalid argument!");
					}
					
					return false;
				}
			}
			
			return true;
		}
		
		return false;
	}
}
