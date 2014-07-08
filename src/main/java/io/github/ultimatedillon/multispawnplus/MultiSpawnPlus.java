package io.github.ultimatedillon.multispawnplus;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public final class MultiSpawnPlus extends JavaPlugin {
	String[] spawns;
	String[] allowed;
	String[] portals;
	
	@Override
	public void onEnable() {
		reloadPlugin();
		
        new PlayerJoinListener(this);
        new PlayerMoveListener(this);
    }
	
	public void reloadPlugin() {
		World defaultWorld = Bukkit.getServer().getWorlds().get(0);
		
		int[] coords = {
			defaultWorld.getSpawnLocation().getBlockX(),
			defaultWorld.getSpawnLocation().getBlockY(),
			defaultWorld.getSpawnLocation().getBlockZ()
		};
		
		float yaw = defaultWorld.getSpawnLocation().getYaw();
		float pitch = defaultWorld.getSpawnLocation().getPitch();
		
		if (!new File(getDataFolder(), "config.yml").exists()) {
			getConfig().addDefault("options.random-spawn-on-join", false);
			getConfig().addDefault("spawns.default.world", defaultWorld.getName());
			getConfig().addDefault("spawns.default.allow-random-spawn", false);
			getConfig().addDefault("spawns.default.X", coords[0]);
			getConfig().addDefault("spawns.default.Y", coords[1]);
			getConfig().addDefault("spawns.default.Z", coords[2]);
			getConfig().addDefault("spawns.default.yaw", Float.toString(yaw));
			getConfig().addDefault("spawns.default.pitch", Float.toString(pitch));
			
			getConfig().addDefault("portals.default.world", defaultWorld.getName());
			getConfig().addDefault("portals.default.destination", "default");
			getConfig().addDefault("portals.default.X", 0);
			getConfig().addDefault("portals.default.Y", 0);
			getConfig().addDefault("portals.default.Z", 0);
			
			getConfig().options().copyDefaults(true);
		}
		
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
		
		if (getConfig().getConfigurationSection("portals") == null) {
			getConfig().set("portals", null);
		}
		
		saveConfig();
	}
	
	public void reloadPortals() {
		Set<String> portalList = getConfig().getConfigurationSection("portals").getKeys(false);
		portals = portalList.toArray(new String[portalList.size()]);
	}
	
	public Block getTargetBlock(Player player, int range) {
	    Location loc = player.getEyeLocation();
	    Vector dir = loc.getDirection().normalize();
	 
	    Block b = null;
	 
	    for (int i = 0; i <= range; i++) {
	        b = loc.add(dir).getBlock();
	    }
	 
	    return b;
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("multispawnplus")) {
			if (args.length < 1) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b#####################"));
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eMultiSpawnPlus v1.1.45"));
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eby UltimateDillon"));
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b#####################"));
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "Use &6/msp help &ffor command syntax."));
			} else {
				
				//region Add
				if (args[0].equalsIgnoreCase("add")) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						
						if (player.hasPermission("multispawnplus.add.spawn")) {
							if (args.length < 3) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlease enter a name for this spawnpoint, and whether it can be used as a random spawn."));
								player.sendMessage("Usage: /msp add <name> [true|false]");
							} else if (args.length > 3) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cToo many arguments!"));
								player.sendMessage("Usage: /msp add <name> [true|false]");
							} else {
								if (args[1] == "random") {
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cSpawnpoints must not be named random!"));
								} else if (getConfig().contains("spawns." + args[1])) {
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cA spawnpoint with that name already exists!"));
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUse &f/msp delete <name> &bto delete existing spawnpoints."));
								} else {
									if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false")) {
										getConfig().set("spawns." + args[1] + ".world", player.getWorld().getName());
										getConfig().set("spawns." + args[1] + ".allow-random-spawn", new Boolean(args[2]));
										getConfig().set("spawns." + args[1] + ".X", player.getLocation().getBlockX());
										getConfig().set("spawns." + args[1] + ".Y", player.getLocation().getBlockY());
										getConfig().set("spawns." + args[1] + ".Z", player.getLocation().getBlockZ());
										getConfig().set("spawns." + args[1] + ".yaw", player.getLocation().getYaw());
										getConfig().set("spawns." + args[1] + ".pitch", player.getLocation().getPitch());
										saveConfig();
										player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bSpawnpoint &f" + args[1] + " &bhas been created!"));
									} else if (!args[2].equalsIgnoreCase("true") && !args[2].equalsIgnoreCase("false")) {
										player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cSyntax Error!"));
										player.sendMessage("Usage: /msp add <name> [true|false]");
									} else {
										player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cDerp. Something went wrong!"));
									}
								}
							}
						} else {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have permission to do this"));
						}
					} else {
						sender.sendMessage("MultiSpawnPlus: This command can only be run by a player");
					}
				//endregion
					
				//region AddPortal
				} else if (args[0].equalsIgnoreCase("addportal")) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						
						if (player.hasPermission("multispawnplus.add.portal")) {
							if (args.length < 2) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlease enter a name for this "
										+ "portal block, and the destination spawnpoint."));
								player.sendMessage("Usage: /msp addportal <name> <destination> "
										+ "(Leave the destination blank to make the destination be chosen randomly.)");
							} else if (args.length > 3) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cToo many arguments!"));
								player.sendMessage("Usage: /msp addportal <name> <destination> "
										+ "(Leave the destination blank to make the destination be chosen randomly.)");
							} else {
								if (getConfig().contains("portals." + args[1])) {
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cA portal with that name "
											+ "already exists!"));
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUse &f/msp deleteportal "
											+ "<name> &bto delete existing portal blocks."));
								} else {
									Block targetBlock = player.getTargetBlock(null, 8);
									if (!targetBlock.getType().equals(Material.AIR)) {
										Location loc = targetBlock.getLocation();
										getConfig().set("portals." + args[1] + ".world", loc.getWorld().getName());
										getConfig().set("portals." + args[1] + ".X", loc.getBlockX());
										getConfig().set("portals." + args[1] + ".Y", loc.getBlockY());
										getConfig().set("portals." + args[1] + ".Z", loc.getBlockZ());
										
										if (args.length == 3) {
											if (!getConfig().contains("spawns." + args[2])) {
												player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThe "
														+ "destination spawn point doesn't exist!"));
											} else {
												getConfig().set("portals." + args[1] + ".destination", args[2]);
												
												saveConfig();
												reloadPortals();
												player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bPortal &f" + args[1] 
														+ " &bhas been created at " + loc.getBlockX() + ", " 
														+ loc.getBlockY() + ", " + loc.getBlockZ() + "."));
											}
										} else {
											getConfig().set("portals." + args[1] + ".destination", "random");
											
											saveConfig();
											reloadPortals();
											player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bPortal &f" + args[1] 
													+ " &bhas been created at " + loc.getBlockX() + ", " 
													+ loc.getBlockY() + ", " + loc.getBlockZ() + "."));
										}
									} else {
										player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlease select a block "
												+ "to use as the portal block!"));
									}
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
						
						if (player.hasPermission("multispawnplus.delete.spawn")) {
							if (args.length < 2) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlease enter a spawnpoint to delete."));
							} else if (args.length > 2) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cToo many arguments!"));
								player.sendMessage("Usage: /msp delete <name>");
							} else {
								if (getConfig().contains("spawns." + args[1])) {
									getConfig().set("spawns." + args[1], null);
									saveConfig();
									reloadPlugin();
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
								reloadPlugin();
								sender.sendMessage("MultiSpawnPlus: Spawnpoint " + args[1] + " deleted.");
							} else {
								sender.sendMessage("MultiSpawnPlus: That spawnpoint doesn't exist.");
							}
						}
					}
				//endregion
					
				//region DelPortal
				} else if (args[0].equalsIgnoreCase("delportal")) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						
						if (player.hasPermission("multispawnplus.delete.portal")) {
							if (args.length < 2) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlease enter a portal to delete."));
							} else if (args.length > 2) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cToo many arguments!"));
								player.sendMessage("Usage: /msp delportal <name>");
							} else {
								if (getConfig().contains("portals." + args[1])) {
									getConfig().set("portals." + args[1], null);
									saveConfig();
									reloadPortals();
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&Portal block &f" + args[1] + " &bdeleted."));
								} else {
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThat portal doesn't exist."));
								}
							}
						} else {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have permission to do this"));
						}
					} else {
						if (args.length < 2) {
							sender.sendMessage("MultiSpawnPlus: Please enter a portal to delete.");
						} else if (args.length > 2) {
							sender.sendMessage("MultiSpawnPlus: Too many arguments!");
							sender.sendMessage("MultiSpawnPlus: Usage: /msp delportal <name>");
						} else {
							if (!getConfig().contains(args[1])) {
								getConfig().set("portals." + args[1], null);
								saveConfig();
								reloadPlugin();
								sender.sendMessage("MultiSpawnPlus: Portal " + args[1] + " deleted.");
							} else {
								sender.sendMessage("MultiSpawnPlus: That portal doesn't exist.");
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
									int yaw = getConfig().getInt("spawns." + args[1] + ".yaw");
									int pitch = getConfig().getInt("spawns." + args[1] + ".pitch");
									
									if (world == null) {
										player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThat world does not exist!"));
									} else {
										Location loc = new Location(world, x, y, z, yaw, pitch);
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
								int yaw = getConfig().getInt("spawns." + allowed[i] + ".yaw");
								int pitch = getConfig().getInt("spawns." + allowed[i] + ".pitch");
								
								getLogger().info("MultiSpawnPlus: - Teleporting " + player.getName() + " to " + allowed[i]
										+ "(" + world + ", " + x + ", " + y + ", " + z + ", " + yaw + ", " + pitch + ")");
								
								if (world == null) {
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThat world does not exist!"));
								} else {
									Location loc = new Location(world, x, y, z, yaw, pitch);
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
							reloadPlugin();
							reloadPortals();
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6MultiSpawnPlus config reloaded!"));
						} else {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have permission to do this"));
						}
					} else {
						reloadConfig();
						reloadPlugin();
						reloadPortals();
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
