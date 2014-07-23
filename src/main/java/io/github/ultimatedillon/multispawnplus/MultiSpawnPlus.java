package io.github.ultimatedillon.multispawnplus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import net.gravitydevelopment.updater.Updater;

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
import org.mcstats.Metrics;

public final class MultiSpawnPlus extends JavaPlugin {
	String version = "v1.2.39";
	
	String[] spawns;
	String[] allowed;
	String[] portals;
	
	PlayerJoinListener joinListener;
	PlayerMoveListener moveListener;
	
	Metrics metrics;
	Updater updater;
	
	@Override
	public void onDisable() {
  		getServer().getScheduler().cancelTasks(this);
  	}
	
	@Override
	public void onEnable() {
		reloadPlugin();
		
        joinListener = new PlayerJoinListener(this);
        moveListener = new PlayerMoveListener(this);
        
        if (getConfig().getBoolean("options.allow-plugin-metrics", true)) {
	        try {
	            metrics = new Metrics(this);
	            metrics.start();
	            
	            getLogger().info("MultiSpawnPlus: Succesfully submitting stats to MCStats.org!");
	        } catch (IOException e) {
	            getLogger().info("MultiSpawnPlus: Failed to submit stats to MCStats.org! Error: " + e);
	        }
        } else {
        	getLogger().info("MultiSpawnPlus: Plugin Metrics disallowed! Disabling submission of stats to MCStats.org...");
        }
        
        if (getConfig().getBoolean("options.auto-update") == true) {
			updater = new Updater(this, 81894, this.getFile(), Updater.UpdateType.DEFAULT, true);
        } else if (getConfig().getBoolean("options.auto-update") == false) {
        	updater = new Updater(this, 81894, this.getFile(), Updater.UpdateType.NO_DOWNLOAD, false);
        	
        	String latest = updater.getLatestName().substring(15);
        	if (version.equalsIgnoreCase(latest)) {
        		getLogger().info("MultiSpawnPlus: No updates available.");
        	} else {
        		getLogger().info("MultiSpawnPlus: Update available! (" + latest + ")");
        		getLogger().info("MultiSpawnPlus: Download here: " + updater.getLatestFileLink());
        	}
        }
    }
	
	public void reloadPlugin() {
		reloadConfig();
		
		World defaultWorld = Bukkit.getServer().getWorlds().get(0);
		
		int[] coords = {
			defaultWorld.getSpawnLocation().getBlockX(),
			defaultWorld.getSpawnLocation().getBlockY(),
			defaultWorld.getSpawnLocation().getBlockZ()
		};
		
		float yaw = defaultWorld.getSpawnLocation().getYaw();
		float pitch = defaultWorld.getSpawnLocation().getPitch();
		
		if (!new File(getDataFolder(), "config.yml").exists()) {
			getConfig().addDefault("version", version);
			getConfig().addDefault("options.random-spawn-on-join", false);
			getConfig().addDefault("options.first-join-spawn-group", "default");
			getConfig().addDefault("options.auto-update", false);
			getConfig().addDefault("options.allow-plugin-metrics", true);
			getConfig().addDefault("spawns.default.world", defaultWorld.getName());
			getConfig().addDefault("spawns.default.allow-random-spawn", false);
			getConfig().addDefault("spawns.default.spawn-group", "default");
			getConfig().addDefault("spawns.default.X", coords[0]);
			getConfig().addDefault("spawns.default.Y", coords[1]);
			getConfig().addDefault("spawns.default.Z", coords[2]);
			getConfig().addDefault("spawns.default.yaw", Float.toString(yaw));
			getConfig().addDefault("spawns.default.pitch", Float.toString(pitch));
			
			getConfig().addDefault("portals.default.world", defaultWorld.getName());
			getConfig().addDefault("portals.default.destination", "default");
			getConfig().addDefault("portals.default.spawn-group", "default");
			getConfig().addDefault("portals.default.X", 0);
			getConfig().addDefault("portals.default.Y", 0);
			getConfig().addDefault("portals.default.Z", 0);
			
			getConfig().options().copyDefaults(true);
		}
		
		getConfig().set("version", version);
		
		if (!getConfig().contains("options.random-spawn-on-join")) {
			getConfig().set("options.random-spawn-on-join", false);
		} else if (getConfig().getBoolean("options.random-spawn-on-join") != true) {
			getConfig().set("options.random-spawn-on-join", false);
		}
		
		if (!getConfig().contains("options.first-join-spawn-group")) {
			getConfig().set("options.first-join-spawn-group", "default");
		}
		
		if (!getConfig().contains("options.auto-update")) {
			getConfig().set("options.auto-update", false);
		} else if (getConfig().getBoolean("options.auto-update") != true) {
			getConfig().set("options.auto-update", false);
		}
		
		if (!getConfig().contains("options.allow-plugin-metrics")) {
			getConfig().set("options.allow-plugin-metrics", true);
		} else if (getConfig().getBoolean("options.allow-plugin-metrics") != true) {
			getConfig().set("options.allow-plugin-metrics", false);
		}
		
		if (getConfig().getConfigurationSection("spawns") == null) {
			getConfig().options().copyDefaults(true);
		}
		
		Set<String> spawnList = getConfig().getConfigurationSection("spawns").getKeys(false);
		spawns = spawnList.toArray(new String[spawnList.size()]);
		ArrayList<String> allowedList = new ArrayList<String>();
		
		for (int i = 0; i < spawns.length; i++) {
			if (getConfig().getBoolean("spawns." + spawns[i] + ".allow-random-spawn") == true) {
				if (spawns[i] != null && spawns[i] != "null") {
					allowedList.add(spawns[i]);
				}
			}
			
			if (!getConfig().contains("spawns." + spawns[i] + ".world") ||
				getConfig().getString("spawns." + spawns[i] + ".world") == null) {
				getConfig().set("spawns." + spawns[i] + ".world", defaultWorld.getName());
			}
			
			if (!getConfig().contains("spawns." + spawns[i] + ".allow-random-spawn")) {
				getConfig().set("spawns." + spawns[i] + ".allow-random-spawn", new Boolean(false));
			}
			
			if (!getConfig().contains("spawns." + spawns[i] + ".spawn-group") ||
				getConfig().get("spawns." + spawns[i] + ".spawn-group") == null) {
				getConfig().set("spawns." + spawns[i] + ".spawn-group", "default");
			}
			
			if (!getConfig().contains("spawns." + spawns[i] + ".X") ||
				getConfig().get("spawns." + spawns[i] + ".X") == null) {
				getConfig().set("spawns." + spawns[i] + ".X", coords[0]);
			}
			
			if (!getConfig().contains("spawns." + spawns[i] + ".Y") ||
				getConfig().get("spawns." + spawns[i] + ".Y") == null) {
				getConfig().set("spawns." + spawns[i] + ".Y", coords[1]);
			}
			
			if (!getConfig().contains("spawns." + spawns[i] + ".Z") ||
				getConfig().get("spawns." + spawns[i] + ".Z") == null) {
				getConfig().set("spawns." + spawns[i] + ".Z", coords[2]);
			}
			
			if (!getConfig().contains("spawns." + spawns[i] + ".yaw") ||
				getConfig().get("spawns." + spawns[i] + ".yaw") == null) {
				getConfig().set("spawns." + spawns[i] + ".yaw", Float.toString(yaw));
			}
			
			if (!getConfig().contains("spawns." + spawns[i] + ".pitch") ||
				getConfig().get("spawns." + spawns[i] + ".pitch") == null) {
				getConfig().set("spawns." + spawns[i] + ".pitch", Float.toString(pitch));
			}
		}
		
		allowed = allowedList.toArray(new String[allowedList.size()]);
		
		Set<String> portalList = getConfig().getConfigurationSection("portals").getKeys(false);
		portals = portalList.toArray(new String[portalList.size()]);
		
		if (getConfig().getConfigurationSection("portals") == null) {
			getConfig().options().copyDefaults(true);
		}
		
		for (int i = 0; i < portals.length; i++) {
			if (!getConfig().contains("portals." + portals[i] + ".world") ||
				getConfig().getString("portals." + portals[i] + ".world") == null) {
				getConfig().set("portals." + portals[i] + ".world", defaultWorld.getName());
			}
			
			if (!getConfig().contains("portals." + portals[i] + ".destination")) {
				getConfig().set("portals." + portals[i] + ".destination", "default");
			}
			
			if (!getConfig().contains("portals." + portals[i] + ".spawn-group") ||
				getConfig().get("portals." + portals[i] + ".spawn-group") == null) {
				getConfig().set("portals." + portals[i] + ".spawn-group", "default");
			}
			
			if (!getConfig().contains("portals." + portals[i] + ".X") ||
				getConfig().get("portals." + portals[i] + ".X") == null) {
				getConfig().set("portals." + portals[i] + ".X", coords[0]);
			}
			
			if (!getConfig().contains("portals." + portals[i] + ".Y") ||
				getConfig().get("portals." + portals[i] + ".Y") == null) {
				getConfig().set("portals." + portals[i] + ".Y", coords[1]);
			}
			
			if (!getConfig().contains("portals." + portals[i] + ".Z") ||
				getConfig().get("portals." + portals[i] + ".Z") == null) {
				getConfig().set("portals." + portals[i] + ".Z", coords[2]);
			}
		}
		
		if (validFirstGroup() == false) {
			if (getConfig().getBoolean("options.random-spawn-on-join") == true) {
				Bukkit.getLogger().info("MultiSpawnPlus: The spawn group you have chosen under 'first-join-spawn-group' "
						+ "doesn't have any random spawn points to use! Disabling random spawning on first join...");
				getConfig().set("options.random-spawn-on-join", new Boolean(false));
			} else {
				Bukkit.getLogger().info("MultiSpawnPlus: The spawn group you have chosen under 'first-join-spawn-group' "
						+ "doesn't have any random spawn points to use!");
			}
		}
		
		saveConfig();
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
	
	public void reloadPortals() {
		Set<String> portalList = getConfig().getConfigurationSection("portals").getKeys(false);
		portals = portalList.toArray(new String[portalList.size()]);
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("multispawnplus")) {
			if (args.length < 1) {
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&b#####################"));
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eMultiSpawnPlus " + version));
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
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlease enter a name for this "
										+ "spawnpoint, whether it can be used as a random spawn, and the spawn group it should "
										+ "be added to."));
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eLeave &f<spawn group> "
										+ "&eblank to set the spawn group to 'default')"));
								player.sendMessage("Usage: /msp add <name> [true|false] <spawn group>");
							} else if (args.length > 4) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cToo many arguments!"));
								player.sendMessage("Usage: /msp add <name> [true|false] <spawn group>");
							} else {
								if (args[1] == "random") {
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cSpawnpoints must not be "
											+ "named 'random'!"));
								} else if (getConfig().contains("spawns." + args[1])) {
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cA spawnpoint with that name "
											+ "already exists!"));
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bUse &f/msp delete <name> &bto "
											+ "delete existing spawnpoints."));
								} else {
									if (args[2].equalsIgnoreCase("true") || args[2].equalsIgnoreCase("false")) {
										getConfig().set("spawns." + args[1] + ".world", player.getWorld().getName());
										getConfig().set("spawns." + args[1] + ".allow-random-spawn", new Boolean(args[2]));
										
										if (args.length > 3) {
											getConfig().set("spawns." + args[1] + ".spawn-group", args[3]);
										} else {
											getConfig().set("spawns." + args[1] + ".spawn-group", "default");
										}
										
										getConfig().set("spawns." + args[1] + ".X", player.getLocation().getBlockX());
										getConfig().set("spawns." + args[1] + ".Y", player.getLocation().getBlockY());
										getConfig().set("spawns." + args[1] + ".Z", player.getLocation().getBlockZ());
										getConfig().set("spawns." + args[1] + ".yaw", player.getLocation().getYaw());
										getConfig().set("spawns." + args[1] + ".pitch", player.getLocation().getPitch());
										
										saveConfig();
										player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bSpawnpoint &f" + args[1]
												+ " &bhas been created!"));
										
										reloadPlugin();
										reloadPortals();
										moveListener.getConfiguration(this);
									} else if (!args[2].equalsIgnoreCase("true") && !args[2].equalsIgnoreCase("false")) {
										player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cSyntax Error!"));
										player.sendMessage("Usage: /msp add <name> [true|false] <spawn group>");
									} else {
										player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cDerp. "
												+ "Something went wrong!"));
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
							if (args.length < 3) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlease enter a name for this "
										+ "portal block, the destination spawnpoint, and the spawn group it should use for "
										+ "random spawning."));
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&eSet &f<destination> &eto "
										+ "'random' if you want the destination be random, and leave &f<random group> "
										+ "&eblank to set the spawn group to 'default')"));
								player.sendMessage("Usage: /msp addportal <name> <destination> <spawn-group>");
							} else if (args.length > 4) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cToo many arguments!"));
								player.sendMessage("Usage: /msp addportal <name> <destination> <spawn-group>");
							} else {
								Block targetBlock = player.getTargetBlock(null, 8);
								Location loc = targetBlock.getLocation();
								
								if (getConfig().contains("portals." + args[1])) {
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cA portal with that name "
											+ "already exists!"));
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bUse &f/msp deleteportal "
											+ "<name> &bto delete existing portal blocks."));
								} else if (targetBlock.getType().equals(Material.AIR)) {
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlease select a block "
											+ "to use as the portal block!"));
								} else if (!args[2].equalsIgnoreCase("random") && 
										   !getConfig().contains("spawns." + args[2])) {
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThe "
											+ "destination spawn point doesn't exist!"));
								} else {
									getConfig().set("portals." + args[1] + ".world", loc.getWorld().getName());
									
									if (args[2].equalsIgnoreCase("random")) {
										getConfig().set("portals." + args[1] + ".destination", "random");
									} else {
										getConfig().set("portals." + args[1] + ".destination", args[2]);
									}
									
									if (args.length > 3) {
										getConfig().set("portals." + args[1] + ".spawn-group", args[3]);
									} else {
										getConfig().set("portals." + args[1] + ".spawn-group", "default");
									}
									
									getConfig().set("portals." + args[1] + ".X", loc.getBlockX());
									getConfig().set("portals." + args[1] + ".Y", loc.getBlockY());
									getConfig().set("portals." + args[1] + ".Z", loc.getBlockZ());
									
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bPortal &f" + args[1] 
											+ " &bhas been created at " + loc.getBlockX() + ", " 
											+ loc.getBlockY() + ", " + loc.getBlockZ() + "."));
									
									saveConfig();
									reloadPlugin();
									reloadPortals();
									moveListener.getConfiguration(this);
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
									reloadPortals();
									moveListener.getConfiguration(this);
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bSpawnpoint &f" + args[1] 
											+ " &bdeleted."));
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
								reloadPortals();
								moveListener.getConfiguration(this);
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
									reloadPlugin();
									reloadPortals();
									moveListener.getConfiguration(this);
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bPortal block &f" + args[1] 
											+ " &bdeleted."));
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
								reloadPortals();
								moveListener.getConfiguration(this);
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
						String destination = "";
						Boolean canTeleport = true;
						
						if (player.hasPermission("multispawnplus.random")) {
							if (args.length < 2) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlease enter a spawn group."));
								player.sendMessage("Usage: /msp random <spawn group>");
							} else if (args.length > 2) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cToo many arguments!"));
								player.sendMessage("Usage: /msp random <spawn group>");
							} else {
								ArrayList<String> groupList = new ArrayList<String>();
								for (int i = 0; i < allowed.length; i++) {
									if (getConfig().getString("spawns." + allowed[i] + ".spawn-group").equalsIgnoreCase(args[1])) {
										groupList.add(allowed[i]);
									}
								}
								String[] group = groupList.toArray(new String[groupList.size()]);
								
								try {
									Random rand = new Random();
									destination = group[rand.nextInt(group.length)];
								} catch (IllegalArgumentException err) {
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThere are no spawn points "
											+ "in that group!"));
									canTeleport = false;
								}
								
								if (canTeleport == true) {
									World world = Bukkit.getWorld(getConfig().getString("spawns." + destination + ".world"));
									double x = getConfig().getInt("spawns." + destination + ".X") + 0.5;
									double y = getConfig().getInt("spawns." + destination + ".Y");
									double z = getConfig().getInt("spawns." + destination + ".Z") + 0.5;
									int yaw = getConfig().getInt("spawns." + destination + ".yaw");
									int pitch = getConfig().getInt("spawns." + destination + ".pitch");
									
									getLogger().info("MultiSpawnPlus: - Teleporting " + player.getName() + " to " + destination
											+ "(" + world + ", " + x + ", " + y + ", " + z + ", " + yaw + ", " + pitch + ")");
									
									if (world == null) {
										player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThat world does not exist!"));
									} else {
										Location loc = new Location(world, x, y, z, yaw, pitch);
										player.teleport(loc);
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
				
				//region List
				} else if (args[0].equalsIgnoreCase("list")) {
					if (sender instanceof Player) {
						Player player = (Player) sender;
						
						if (player.hasPermission("multispawnplus.list")) {
							if (args.length < 2) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cPlease enter something to list!"));
								player.sendMessage("Usage: /msp list [spawns|portals] [traits]");
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&bExamples:"));
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "/msp list spawns &8- Lists all "
										+ "spawn points."));
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "/msp list spawns random &8- Lists "
										+ "all spawn points that can be teleported to randomly."));
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "/msp list spawns random default "
										+ "&8- Lists all random spawn points in the 'default' spawn group."));
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "/msp list portals &8- Lists all "
										+ "portal blocks."));
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "/msp list portals random &8- Lists "
										+ "all portal blocks that teleport the player to a random spawn point."));
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "/msp list portals random default "
										+ "&8- Lists all random portal blocks that teleport the player to spawn points in the "
										+ "'default' spawn group."));
							} else if (args.length > 4) {
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cToo many arguments!"));
								player.sendMessage("Usage: /msp list [spawns|portals] [traits]");
							} else {
								if (args[1].equalsIgnoreCase("spawns")) {
									if (args.length > 2) {
										if (args[2].equalsIgnoreCase("random")) {
											if (args.length > 3) {
												player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6All spawn points in the &f"
														+ args[3] + " &6spawn group:"));
												
												for (int i = 0; i < spawns.length; i++) {
													if (getConfig().getString("spawns." + spawns[i] 
															+ ".spawn-group").equalsIgnoreCase(args[3])) {
														player.sendMessage(ChatColor.translateAlternateColorCodes('&', "- &b" + spawns[i]));
													}
												}
											} else {
												player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6All random spawn points:"));
												
												for (int i = 0; i < spawns.length; i++) {
													if (getConfig().getBoolean("spawns." + spawns[i] 
															+ ".allow-random-spawn") == true) {
														player.sendMessage(ChatColor.translateAlternateColorCodes('&', "- &b" + spawns[i]));
													}
												}
											} 
										} else {
											player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInvalid Syntax!"));
										}
									} else {
										player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6All defined spawn points:"));
										
										for (int i = 0; i < spawns.length; i++) {
											player.sendMessage(ChatColor.translateAlternateColorCodes('&', "- &b" + spawns[i]));
										}
									}
								} else if (args[1].equalsIgnoreCase("portals")) {
									Set<String> portalList = getConfig().getConfigurationSection("portals").getKeys(false);
									portals = portalList.toArray(new String[portalList.size()]);
									
									if (args.length > 2) {
										if (args[2].equalsIgnoreCase("random")) {
											if (args.length > 3) {
												player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6All portals in the &f"
														+ args[3] + " &6spawn group:"));
												
												for (int i = 0; i < portals.length; i++) {
													if (getConfig().getString("portals." + portals[i] 
															+ ".spawn-group").equalsIgnoreCase(args[3])) {
														player.sendMessage(ChatColor.translateAlternateColorCodes('&', "- &b" + portals[i]));
													}
												}
											} else {
												player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6All random portals:"));
												
												for (int i = 0; i < portals.length; i++) {
													if (getConfig().getString("portals." + portals[i] 
															+ ".destination").equalsIgnoreCase("random")) {
														player.sendMessage(ChatColor.translateAlternateColorCodes('&', "- &b" + portals[i]));
													}
												}
											} 
										} else {
											player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInvalid Syntax!"));
										}
									} else {
										player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6All defined portals:"));
										
										for (int i = 0; i < portals.length; i++) {
											player.sendMessage(ChatColor.translateAlternateColorCodes('&', "- &b" + portals[i]));
										}
									}
								} else {
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cInvalid Arguments!"));
									player.sendMessage("Usage: /msp list [spawns|portals] [traits]");
								}
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
							reloadPlugin();
							reloadPortals();
							moveListener.getConfiguration(this);
							joinListener.playerConfigClass.reloadConfig();
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&6MultiSpawnPlus config reloaded!"));
						} else {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', "&4You do not have permission to do this"));
						}
					} else {
						reloadPlugin();
						reloadPortals();
						moveListener.getConfiguration(this);
						joinListener.playerConfigClass.reloadConfig();
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
