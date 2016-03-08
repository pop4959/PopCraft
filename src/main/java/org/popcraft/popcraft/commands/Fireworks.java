package org.popcraft.popcraft.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitScheduler;
import org.popcraft.popcraft.PopCraft;
import org.popcraft.popcraft.utils.Message;

public class Fireworks implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		Player PLAYER = (Player) sender;
		if (cmd.getName().equalsIgnoreCase("fireworks"))
		{
			Location LOCATION = PLAYER.getLocation();
			if (args.length == 0) {
				Message.usage(PLAYER, "flare random/list/color [fade] [effect] [type] [height]");
			}
			else if (args.length == 1) {
				if (PLAYER.hasPermission("popcraft.fireworks.others")) {
					if (args[0].equalsIgnoreCase("spawn")) {
						onPlayerJoinFirework(PLAYER);
					}
					else if (args[0].equalsIgnoreCase("here")) {
						int n = 1;
						while (n<=10) {
							LOCATION = commandLocationFirework(PLAYER);
							Color COLOR = colorFirework();
							Color FADE = fadeFirework(COLOR);
							spawnFirework(PLAYER, LOCATION, COLOR, FADE, Type.BALL_LARGE, true, false, 0);
							n++;
						}
					}
					else if (args[0].equalsIgnoreCase("list")) {
						Message.usage(PLAYER, "flare list colors/fades/effects/types/heights");
					}
					else if (args[0].equalsIgnoreCase("random") || args[0].equalsIgnoreCase("r")) {
						Color COLOR = colorFirework();
						Color FADE = fadeFirework(COLOR);
						spawnFirework(PLAYER, LOCATION, COLOR, FADE, Type.BALL, true, false, 0);
					}
					else if (isColor(args[0]) && !args[0].equalsIgnoreCase("none")) {
						if (args[0].equalsIgnoreCase("random")) {
							Color COLOR = randomColor();
							spawnFirework(PLAYER, LOCATION, COLOR, Type.BALL, false, false, 0);	
						}
						else {
							Color COLOR = createColor(args[0]);
							spawnFirework(PLAYER, LOCATION, COLOR, Type.BALL, false, false, 0);	
						}
					}
					else {
						Message.usage(PLAYER, "flare random/list/color [fade] [effect] [type] [height]");
					}
				}
				else if (args[0].equalsIgnoreCase("list")) {
					Message.usage(PLAYER, "flare list colors/fades/effects/types/heights");
				}
				else if (args[0].equalsIgnoreCase("random") || args[0].equalsIgnoreCase("r")) {
					Color COLOR = colorFirework();
					Color FADE = fadeFirework(COLOR);
					spawnFirework(PLAYER, LOCATION, COLOR, FADE, Type.BALL, true, false, 0);
				}
				else if (isColor(args[0]) && !args[0].equalsIgnoreCase("none")) {
					if (args[0].equalsIgnoreCase("random")) {
						Color COLOR = randomColor();
						spawnFirework(PLAYER, LOCATION, COLOR, Type.BALL, false, false, 0);	
					}
					else {
						Color COLOR = createColor(args[0]);
						spawnFirework(PLAYER, LOCATION, COLOR, Type.BALL, false, false, 0);	
					}
				}
				else {
					Message.usage(PLAYER, "flare random/list/color [fade] [effect] [type] [height]");
				}
			}
			else if (args.length == 2) {
				if (args[0].equalsIgnoreCase("list")) {
					if (args[1].equalsIgnoreCase("colors") || args[1].equalsIgnoreCase("color")) {
						Message.normal(PLAYER, "Colors: " + ChatColor.RESET + "random, aqua, black, blue, fuchsia, gray, green, lime, maroon, navy, olive, orange, purple, red, silver, teal, white, yellow");
					}
					else if (args[1].equalsIgnoreCase("fades") || args[1].equalsIgnoreCase("fade")) {
						Message.normal(PLAYER, "Fades: " + ChatColor.RESET + "random, none, aqua, black, blue, fuchsia, gray, green, lime, maroon, navy, olive, orange, purple, red, silver, teal, white, yellow");
					}
					else if (args[1].equalsIgnoreCase("effects") || args[1].equalsIgnoreCase("effect")) {
						Message.normal(PLAYER, "Effects: " + ChatColor.RESET + "random, none, flicker, trail");
					}
					else if (args[1].equalsIgnoreCase("types") || args[1].equalsIgnoreCase("type")) {
						Message.normal(PLAYER, "Types: " + ChatColor.RESET + "random, small, large, burst, creeper, star");
					}
					else if (args[1].equalsIgnoreCase("heights") || args[1].equalsIgnoreCase("height")) {
						Message.normal(PLAYER, "Heights: " + ChatColor.RESET + "random, low, medium, high, extreme");
					}
					else {
						Message.usage(PLAYER, "flare list <colors/fades/effects/types/heights>");
					}
				}
				else if (isColor(args[0]) && isColor(args[1]) && !args[0].equalsIgnoreCase("none")) {
					Color COLOR = createColor(args[0]);
					if (args[1].equalsIgnoreCase("none")) {
						if (args[0].equalsIgnoreCase("random"))
							COLOR = randomColor();
						spawnFirework(PLAYER, LOCATION, COLOR, Type.BALL, false, false, 0);
					}
					else {
						Color FADE = createColor(args[1]);
						if (args[0].equalsIgnoreCase("random")) {
							COLOR = randomColor();
							if (args[1].equalsIgnoreCase("random")) {
								FADE = randomFade();
								spawnFirework(PLAYER, LOCATION, COLOR, FADE, Type.BALL, false, false, 0);
							}
							else {
								spawnFirework(PLAYER, LOCATION, COLOR, FADE, Type.BALL, false, false, 0);
							}
							
						}
						else {
							if (args[1].equalsIgnoreCase("random")) {
								FADE = randomFade();
								spawnFirework(PLAYER, LOCATION, COLOR, FADE, Type.BALL, false, false, 0);
							}
							else {
								spawnFirework(PLAYER, LOCATION, COLOR, FADE, Type.BALL, false, false, 0);
							}
						}
					}
				}
				else {
					Message.usage(PLAYER, "flare random/list/color [fade] [effect] [type] [height]");
				}
			}
			else if (args.length == 3) {
				String effect = args[2];
				if (effect.equalsIgnoreCase("random"))
					effect = randomEffect();
				if (isColor(args[0]) && isColor(args[1]) && isEffect(args[2]) && !args[0].equalsIgnoreCase("none")) {
					Color COLOR = createColor(args[0]);
					if (args[0].equalsIgnoreCase("random"))
						COLOR = randomColor();
					if (args[1].equalsIgnoreCase("none")) {
						if  (effect.equalsIgnoreCase("flicker")) {
							spawnFirework(PLAYER, LOCATION, COLOR, Type.BALL, true, false, 0);
						}
						else if (effect.equalsIgnoreCase("trail")) {
							spawnFirework(PLAYER, LOCATION, COLOR, Type.BALL, false, true, 0);
						}
						else {
							spawnFirework(PLAYER, LOCATION, COLOR, Type.BALL, false, false, 0);
						}
					}
					else {
						Color FADE = createColor(args[1]);
						if (args[1].equalsIgnoreCase("random"))
							FADE = randomFade();
						if  (effect.equalsIgnoreCase("flicker")) {
							spawnFirework(PLAYER, LOCATION, COLOR, FADE, Type.BALL, true, false, 0);
						}
						else if (effect.equalsIgnoreCase("trail")) {
							spawnFirework(PLAYER, LOCATION, COLOR, FADE, Type.BALL, false, true, 0);
						}
						else {
							spawnFirework(PLAYER, LOCATION, COLOR, FADE, Type.BALL, false, false, 0);
						}
					}
				}
				else {
					Message.usage(PLAYER, "flare random/list/color [fade] [effect] [type] [height]");
				}
			}
			else if (args.length == 4) {
				String effect = args[2];
				if (effect.equalsIgnoreCase("random"))
					effect = randomEffect();
				if (isColor(args[0]) && isColor(args[1]) && isEffect(args[2]) && isType(args[3]) && !args[0].equalsIgnoreCase("none")) {
					Color COLOR = createColor(args[0]);
					if (args[0].equalsIgnoreCase("random"))
						COLOR = randomColor();
					Type TYPE = createType(args[3]);
					if (args[3].equalsIgnoreCase("random"))
						TYPE = randomType();
					if (args[1].equalsIgnoreCase("none")) {
						if  (effect.equalsIgnoreCase("flicker")) {
							spawnFirework(PLAYER, LOCATION, COLOR, TYPE, true, false, 0);
						}
						else if (effect.equalsIgnoreCase("trail")) {
							spawnFirework(PLAYER, LOCATION, COLOR, TYPE, false, true, 0);
						}
						else {
							spawnFirework(PLAYER, LOCATION, COLOR, TYPE, false, false, 0);
						}
					}
					else {
						Color FADE = createColor(args[1]);
						if (args[1].equalsIgnoreCase("random"))
							FADE = randomFade();
						if  (effect.equalsIgnoreCase("flicker")) {
							spawnFirework(PLAYER, LOCATION, COLOR, FADE, TYPE, true, false, 0);
						}
						else if (effect.equalsIgnoreCase("trail")) {
							spawnFirework(PLAYER, LOCATION, COLOR, FADE, TYPE, false, true, 0);
						}
						else {
							spawnFirework(PLAYER, LOCATION, COLOR, FADE, TYPE, false, false, 0);
						}
					}
				}
				else {
					Message.usage(PLAYER, "flare random/list/color [fade] [effect] [type] [height]");
				}
			}
			else if (args.length == 5) {
				String effect = args[2];
				if (effect.equalsIgnoreCase("random"))
					effect = randomEffect();
				if (isColor(args[0]) && isColor(args[1]) && isEffect(args[2]) && isType(args[3]) && isHeight(args[4]) && !args[0].equalsIgnoreCase("none")) {
					Color COLOR = createColor(args[0]);
					if (args[0].equalsIgnoreCase("random"))
						COLOR = randomColor();
					Type TYPE = createType(args[3]);
					if (args[3].equalsIgnoreCase("random"))
						TYPE = randomType();
					int HEIGHT = createHeight(args[4]);
					if (args[4].equalsIgnoreCase("random"))
						HEIGHT = randomHeight();
					if (args[1].equalsIgnoreCase("none")) {
						if  (effect.equalsIgnoreCase("flicker")) {
							spawnFirework(PLAYER, LOCATION, COLOR, TYPE, true, false, HEIGHT);
						}
						else if (effect.equalsIgnoreCase("trail")) {
							spawnFirework(PLAYER, LOCATION, COLOR, TYPE, false, true, HEIGHT);
						}
						else {
							spawnFirework(PLAYER, LOCATION, COLOR, TYPE, false, false, HEIGHT);
						}
					}
					else {
						Color FADE = createColor(args[1]);
						if (args[1].equalsIgnoreCase("random"))
							FADE = randomFade();
						if  (effect.equalsIgnoreCase("flicker")) {
							spawnFirework(PLAYER, LOCATION, COLOR, FADE, TYPE, true, false, HEIGHT);
						}
						else if (effect.equalsIgnoreCase("trail")) {
							spawnFirework(PLAYER, LOCATION, COLOR, FADE, TYPE, false, true, HEIGHT);
						}
						else {
							spawnFirework(PLAYER, LOCATION, COLOR, FADE, TYPE, false, false, HEIGHT);
						}
					}
				}
				else {
					Message.usage(PLAYER, "flare random/list/color [fade] [effect] [type] [height]");
				}
			}
			else {
				Message.usage(PLAYER, "flare random/list/color [fade] [effect] [type] [height]");
			}
			return true;
		}
		return false;
	}
	
	public void onPlayerJoinFirework(final Player PLAYER) {
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(PopCraft.getPlugin(), new Runnable() {
            public void run() {
        		int n = 1;
        		while (n<=10) {
        			Location LOCATION = spawnLocationFirework(PLAYER);
        			Color COLOR = colorFirework();
        			Color FADE = fadeFirework(COLOR);
        			spawnFirework(PLAYER, LOCATION, COLOR, FADE, Type.BALL_LARGE, true, false, 0);
        			n++;
        		}
            }
        }, 30L);
	}
	
	public static void spawnFirework(Player PLAYER, Location LOCATION, Color COLOR, Color FADE, Type TYPE, boolean FLICKER, boolean TRAIL, int POWER) {
		Firework firework = (Firework) PLAYER.getWorld().spawnEntity(LOCATION , EntityType.FIREWORK);
		FireworkEffect effect = FireworkEffect.builder().withColor(COLOR).withFade(FADE).with(TYPE).flicker(FLICKER).trail(TRAIL).build();
		FireworkMeta meta = firework.getFireworkMeta();
		meta.addEffect(effect);
		meta.setPower(POWER);
		firework.setFireworkMeta(meta);
	}
	
	public void spawnFirework(Player PLAYER, Location LOCATION, Color COLOR, Type TYPE, boolean FLICKER, boolean TRAIL, int POWER) {
		Firework firework = (Firework) PLAYER.getWorld().spawnEntity(LOCATION , EntityType.FIREWORK);
		FireworkEffect effect = FireworkEffect.builder().withColor(COLOR).with(TYPE).flicker(FLICKER).trail(TRAIL).build();
		FireworkMeta meta = firework.getFireworkMeta();
		meta.addEffect(effect);
		meta.setPower(POWER);
		firework.setFireworkMeta(meta);
	}
	
	public static Location spawnLocationFirework(Player PLAYER) {
		World WORLD = PLAYER.getWorld();
		double xvalue = Math.random();
		double yvalue = Math.random();
		if (xvalue >= 0.5) {
			xvalue = 1;
		}
		else {
			xvalue = -1;
		}
		if (yvalue >= 0.5) {
			yvalue = 1;
		}
		else {
			yvalue = -1;
		}
		double fireworksX = PopCraft.config.getDouble("spawn.coordinate-x");
		double fireworksY = PopCraft.config.getDouble("spawn.coordinate-y");
		double fireworksZ = PopCraft.config.getDouble("spawn.coordinate-z");
		double fireworksR = PopCraft.config.getDouble("fireworks.radius");
		double fireworksVH = PopCraft.config.getDouble("fireworks.variedheight");
		double xPos = fireworksX + (xvalue * fireworksR * Math.random());
		double yPos = fireworksY + (fireworksVH * Math.random());
		double zPos = fireworksZ + (yvalue * fireworksR * Math.random());
		Location randomLocation = new Location(WORLD, xPos, yPos, zPos);
		return randomLocation;
	}
	
	public static Location commandLocationFirework(Player PLAYER) {
		World WORLD = PLAYER.getWorld();
		double xvalue = Math.random();
		double yvalue = Math.random();
		if (xvalue >= 0.5) {
			xvalue = 1;
		}
		else {
			xvalue = -1;
		}
		if (yvalue >= 0.5) {
			yvalue = 1;
		}
		else {
			yvalue = -1;
		}
		double xPos = PLAYER.getLocation().getX() + (xvalue * 20 * Math.random());
		double yPos = PLAYER.getLocation().getY() + (5 * Math.random());
		double zPos = PLAYER.getLocation().getZ() + (yvalue * 20 * Math.random());
		Location commandLocation = new Location(WORLD, xPos, yPos, zPos);
		return commandLocation;
	}
	
	public boolean isColor(String x) {
		if (x.equalsIgnoreCase("none") || x.equalsIgnoreCase("random") || x.equalsIgnoreCase("aqua") || x.equalsIgnoreCase("black") || x.equalsIgnoreCase("blue") || x.equalsIgnoreCase("fuchsia") || x.equalsIgnoreCase("gray") || x.equalsIgnoreCase("green") || x.equalsIgnoreCase("lime") || x.equalsIgnoreCase("maroon") || x.equalsIgnoreCase("navy") || x.equalsIgnoreCase("olive") || x.equalsIgnoreCase("orange") || x.equalsIgnoreCase("purple") || x.equalsIgnoreCase("red") || x.equalsIgnoreCase("silver") || x.equalsIgnoreCase("teal") || x.equalsIgnoreCase("white") || x.equalsIgnoreCase("yellow"))
		{
			return true;
		}
		else {
			return false;
		}
	}
	
	public Color createColor(String x) {
		switch(x.toLowerCase()) {
		case "aqua":
			return Color.AQUA;
		case "black":
			return Color.BLACK;
		case "blue":
			return Color.BLUE;
		case "fuchsia":
			return Color.FUCHSIA;
		case "gray":
			return Color.GRAY;
		case "green":
			return Color.GREEN;
		case "lime":
			return Color.LIME;
		case "maroon":
			return Color.MAROON;
		case "navy":
			return Color.NAVY;
		case "olive":
			return Color.OLIVE;
		case "orange":
			return Color.ORANGE;
		case "purple":
			return Color.PURPLE;
		case "red":
			return Color.RED;
		case "silver":
			return Color.SILVER;
		case "teal":
			return Color.TEAL;
		case "white":
			return Color.WHITE;
		case "yellow":
			return Color.YELLOW;
		default:
			return Color.AQUA;
		}
	}
	
	public Color randomColor() {
		switch((int)(Math.random() * 17)) {
		case 0:
			return Color.AQUA;
		case 1:
			return Color.BLACK;
		case 2:
			return Color.BLUE;
		case 3:
			return Color.FUCHSIA;
		case 4:
			return Color.GRAY;
		case 5:
			return Color.GREEN;
		case 6:
			return Color.LIME;
		case 7:
			return Color.MAROON;
		case 8:
			return Color.NAVY;
		case 9:
			return Color.OLIVE;
		case 10:
			return Color.ORANGE;
		case 11:
			return Color.PURPLE;
		case 12:
			return Color.RED;
		case 13:
			return Color.SILVER;
		case 14:
			return Color.TEAL;
		case 15:
			return Color.WHITE;
		case 16:
			return Color.YELLOW;
		default:
			return Color.AQUA;
		}
	}
	
	public Color randomFade() {
		switch((int)(Math.random() * 17)) {
		case 0:
			return Color.AQUA;
		case 1:
			return Color.BLACK;
		case 2:
			return Color.BLUE;
		case 3:
			return Color.FUCHSIA;
		case 4:
			return Color.GRAY;
		case 5:
			return Color.GREEN;
		case 6:
			return Color.LIME;
		case 7:
			return Color.MAROON;
		case 8:
			return Color.NAVY;
		case 9:
			return Color.OLIVE;
		case 10:
			return Color.ORANGE;
		case 11:
			return Color.PURPLE;
		case 12:
			return Color.RED;
		case 13:
			return Color.SILVER;
		case 14:
			return Color.TEAL;
		case 15:
			return Color.WHITE;
		case 16:
			return Color.YELLOW;
		default:
			return Color.AQUA;
		}
	}
	
	public boolean isEffect(String x) {
		switch(x.toLowerCase()) {
		case "none":
			return true;
		case "random":
			return true;
		case "flicker":
			return true;
		case "trail":
			return true;
		default:
			return false;
		}
	}
	
	public String randomEffect() {
		switch((int)(Math.random() * 3)) {
		case 0:
			return "flicker";
		case 1:
			return "trail";
		case 2:
			return "none";
		default:
			return "none";
		}
	}
	
	public boolean isType(String x) {
		switch(x.toLowerCase()) {
		case "random":
			return true;
		case "small":
			return true;
		case "large":
			return true;
		case "burst":
			return true;
		case "creeper":
			return true;
		case "star":
			return true;
		default:
			return false;
		}
	}
	
		public Type createType(String x) {
			switch(x.toLowerCase()) {
			case "small":
				return Type.BALL;
			case "large":
				return Type.BALL_LARGE;
			case "burst":
				return Type.BURST;
			case "creeper":
				return Type.CREEPER;
			case "star":
				return Type.STAR;
			default:
				return Type.BALL;
			}
		}
		
		public Type randomType() {
			switch((int)(Math.random() * 5)) {
			case 0:
				return Type.BALL;
			case 1:
				return Type.BALL_LARGE;
			case 2:
				return Type.BURST;
			case 3:
				return Type.CREEPER;
			case 4:
				return Type.STAR;
			default:
				return Type.BALL;
			}
		}
	
	public boolean isHeight(String x) {
		switch(x.toLowerCase()) {
		case "random":
			return true;
		case "low":
			return true;
		case "medium":
			return true;
		case "high":
			return true;
		case "extreme":
			return true;
		default:
			return false;
		}
	}
	
		public int createHeight(String x) {
			switch(x.toLowerCase()) {
			case "low":
				return 0;
			case "medium":
				return 1;
			case "high":
				return 2;
			case "extreme":
				return 3;
			default:
				return 1;
			}
		}
		
		public int randomHeight() {
			switch((int)(Math.random() * 4)) {
			case 0:
				return 0;
			case 1:
				return 1;
			case 2:
				return 2;
			case 3:
				return 3;
			default:
				return 0;
			}
		}
	
	public static Color colorFirework() {
		double x = 6 * Math.random();
		if (x >= 0 && x < 1) {
			 Color c = org.bukkit.Color.MAROON;
				return c;
		}
		if (x >= 1 && x < 2) {
			 Color c = org.bukkit.Color.ORANGE;
				return c;
		}
		if (x >= 2 && x < 3) {
			 Color c = org.bukkit.Color.GREEN;
				return c;
		}
		if (x >= 3 && x < 4) {
			 Color c = org.bukkit.Color.TEAL;
				return c;
		}
		if (x >= 4 && x < 5) {
			 Color c = org.bukkit.Color.BLUE;
				return c;
		}
		if (x >= 5 && x <= 6) {
			 Color c = org.bukkit.Color.PURPLE;
				return c;
		}
		return null;
	}
	
	public static Color fadeFirework(Color COLOR) {
		if (COLOR == org.bukkit.Color.MAROON) {
			 Color f = org.bukkit.Color.RED;
				return f;
		}
		if (COLOR == org.bukkit.Color.ORANGE) {
			 Color f = org.bukkit.Color.YELLOW;
				return f;
		}
		if (COLOR == org.bukkit.Color.GREEN) {
			 Color f = org.bukkit.Color.LIME;
				return f;
		}
		if (COLOR == org.bukkit.Color.TEAL) {
			 Color f = org.bukkit.Color.AQUA;
				return f;
		}
		if (COLOR == org.bukkit.Color.BLUE) {
			 Color f = org.bukkit.Color.NAVY;
				return f;
		}
		if (COLOR == org.bukkit.Color.PURPLE) {
			 Color f = org.bukkit.Color.FUCHSIA;
				return f;
		}
		return null;
	}
}