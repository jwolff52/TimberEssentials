package io.github.jwolff52.timberessentials.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import io.github.jwolff52.timberessentials.TimberEssentials;
import io.github.jwolff52.timberessentials.minigames.arenas.OIQArena;
import io.github.jwolff52.timberessentials.minigames.arenas.DefaultArena.Arenas;
import io.github.jwolff52.timberessentials.minigames.events.ArenaLeaveEvent;
import io.github.jwolff52.timberessentials.util.ArenasGUI;
import io.github.jwolff52.timberessentials.util.WarpsGUI;

public class CommandParser {

	private static TimberEssentials te;
	
	private static Arena aCommands;
	private static Chat cCommands;
	private static MessageOfTheDay motdCommands;
	private static Moderation mCommands;
	private static Teleport tCommands;
	
	public static void init(TimberEssentials te) {
		CommandParser.te=te;
		
		aCommands=new Arena(te);
		cCommands=new Chat(te);
		motdCommands=new MessageOfTheDay(te);
		mCommands=new Moderation(te);
		tCommands=new Teleport(te);
	}
	
	@SuppressWarnings("deprecation")
	public static boolean parseCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("te")){
			if (!sender.hasPermission("te.reload")&&sender instanceof Player) {
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"You do not have permission to preform this command!");
				return true;
			}
			te.sm.reloadConfig();
			te.initializeConfigOptions();
			sender.sendMessage(te.getTitle()+"TimberEssentials Version: "+te.getDescription().getVersion()+" configuration successfully reloaded!");
			return true;
		}else if(cmd.getName().equalsIgnoreCase("kick")){
			if (!sender.hasPermission("te.kick")&&sender instanceof Player) {
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"You do not have permission to preform this command!");
				return true;
			}
			if(args.length == 0){
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"Please specifiy a player!");
				return false;
			}
			mCommands.kick(sender, args);
			return true;
		}else if(cmd.getName().equalsIgnoreCase("ban")){
			if (!sender.hasPermission("te.ban")&&sender instanceof Player) {
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"You do not have permission to preform this command!");
				return true;
			}
			if(args.length == 0){
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"Please specifiy a player!");
				return false;
			}
			mCommands.ban(sender, args);
			return true;
		}else if(cmd.getName().equalsIgnoreCase("setmotd")){
			if(args.length == 0){
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"Please specify which MOTD!");
				return false;
			}
			if(args.length < 2){
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"MOTD can't be null!");
				return false;
			}
			if(args[0].equalsIgnoreCase("ingame")){
				return motdCommands.setMotd(sender, args, true);
			}else if(args[0].equalsIgnoreCase("server")){
				return motdCommands.setMotd(sender, args, false);
			}
			sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"Please specify which MOTD!");
			return false;
		}else if(cmd.getName().equalsIgnoreCase("motd")){
			if (!sender.hasPermission("te.motd")&&sender instanceof Player) {
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"You do not have permission to preform this command!");
				return true;
			}
			motdCommands.getMotd(sender);
			return true;
		}else if(cmd.getName().equalsIgnoreCase("nick")){
			if(!(sender instanceof Player)){
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"The Console cannot have a nickname!");
				return true;
			}
			if(args.length == 0){
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"You did not specify a nickname!");
				return false;
			}
			return cCommands.setNick((Player)sender, args);
		}else if(cmd.getName().equalsIgnoreCase("tp")){
			if(!(sender instanceof Player)){
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"The Console cannot teleport!");
				return true;
			}
			if(args.length == 0){
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"You did not specify a player!");
				return false;
			}
			if (args.length==1&&!sender.hasPermission("te.tp.self")) {
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"You do not have permission to preform this command!");
				return true;
			}else if(args.length == 1){
				tCommands.tp((Player)sender, Bukkit.getServer().getPlayer(args[0]));
				return true;
			}
			if (args.length==2&&!sender.hasPermission("te.tp.other")) {
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"You do not have permission to preform this command!");
				return true;
			}
			tCommands.tp(Bukkit.getServer().getPlayer(args[1]), Bukkit.getServer().getPlayer(args[0]));
			return true;
		}else if(cmd.getName().equalsIgnoreCase("tppos")){
			if(!(sender instanceof Player)){
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"The Console cannot teleport!");
				return true;
			}
			if(args.length < 3){
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"You did not specify a proper location!");
				return false;
			}
			double[] coords=new double[3];
			for(int i=0;i<coords.length;i++){
				try{
					coords[i]=Double.valueOf(args[i]);
				}catch(NumberFormatException e){
					sender.sendMessage(te.getTitle()+ChatColor.GOLD+args[i]+ChatColor.DARK_RED+" is not a valid coordinate!");
					return false;
				}
			}
			if (args.length==3&&!sender.hasPermission("te.tp.self")) {
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"You do not have permission to preform this command!");
				return true;
			}else if(args.length == 3){
				tCommands.tppos((Player)sender, new Location(((Player)sender).getWorld(), coords[0], coords[1], coords[2]));
				return true;
			}
			if (args.length==4&&!sender.hasPermission("te.tp.other")) {
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"You do not have permission to preform this command!");
				return true;
			}
			tCommands.tppos(Bukkit.getServer().getPlayer(args[4]), new Location(((Player)sender).getWorld(), coords[0], coords[1], coords[2]));
			return true;
		}else if(cmd.getName().equalsIgnoreCase("setwarp")){
			if(!(sender instanceof Player)){
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"The Console cannot set a warp!");
				return true;
			}
			if (!sender.hasPermission("te.setwarp")) {
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"You do not have permission to preform this command!");
				return true;
			}
			if(te.getWarps().containsKey(args[0])){
				tCommands.delWarp(args);
			}
			tCommands.setWarp((Player)sender, args);
			return true;
		}else if(cmd.getName().equalsIgnoreCase("warp")){
			if(!(sender instanceof Player)){
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"The Console cannot teleport!");
				return true;
			}
			if(args.length==0){
				if (!sender.hasPermission("te.warps.gui")) {
					sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"You do not have permission to access the Warps GUI!");
					return false;
				}
				WarpsGUI.show((Player)sender);
				return true;
			}
			return tCommands.warp((Player)sender, args[0], te.getWarps().get(args[0].toLowerCase()));
		}else if(cmd.getName().equalsIgnoreCase("back")){
			if(!(sender instanceof Player)){
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"The Console cannot teleport!");
				return true;
			}
			if (!sender.hasPermission("te.back")) {
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"You do not have permission to use that command!");
				return true;
			}
			tCommands.tppos((Player)sender, te.sm.getLastLocation((Player)sender));
			return true;
		}else if(cmd.getName().equalsIgnoreCase("setup")){
			if(!(sender instanceof Player)){
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"The Console cannot setup arenas!");
				return true;
			}
			if (!sender.hasPermission("te.arena.setup")) {
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"You do not have permission to use that command!");
				return true;
			}
			if(args.length == 1){
				aCommands.setup((Player)sender, Arenas.getArenaFromName(args[0]));
				return true;
			}
		}else if(cmd.getName().equalsIgnoreCase("add")){
			if(!(sender instanceof Player)){
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"The Console cannot create arenas!");
				return true;
			}
			if (!sender.hasPermission("te.arena.create")) {
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"You do not have permission to use that command!");
				return true;
			}
			if(args.length<2){
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"You must provide a type and a name!");
				return false;
			}
			aCommands.add((Player) sender, args);
			return true;
		}else if(cmd.getName().equalsIgnoreCase("join")){
			if(!(sender instanceof Player)){
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"The Console cannot join an arena!");
				return true;
			}
			if(args.length==0){
				if (!sender.hasPermission("te.arenas.gui")) {
					sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"You do not have permission to access the Arenas GUI!");
					return false;
				}
				ArenasGUI.show((Player)sender);
				return true;
			}
			return aCommands.join((Player)sender, ((Player)sender).getLocation(), args[0]);
		}else if(cmd.getName().equalsIgnoreCase("leave")){
			if(!(sender instanceof Player)){
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"The Console cannot join an arena!");
				return true;
			}
			Bukkit.getServer().getPluginManager().callEvent(new ArenaLeaveEvent((Player)sender, Arenas.removePlayer((Player)sender).getName()));
			return true;
		}else if(cmd.getName().equalsIgnoreCase("readyup")){
			if(!(sender instanceof Player)){
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"The Console cannot join an arena!");
				return true;
			}
			if(!Arenas.containsPlayer((Player)sender)){
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"You must join an arena first!");
				return true;
			}
			if (!sender.hasPermission("te.arena.readyup")) {
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"You do not have permission to Ready Up!");
				return false;
			}
			aCommands.readyUp((Player)sender);
			return true;
		}else if(cmd.getName().equalsIgnoreCase("sign")){
			if(!(sender instanceof Player)){
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"The Console cannot hold items!");
				return true;
			}
			if (!sender.hasPermission("te.sign")) {
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"You do not have permission to get a free sign!");
				return false;
			}
			((Player)sender).getInventory().addItem(new ItemStack(Material.SIGN_POST, 1));
			return true;
		}else if(cmd.getName().equalsIgnoreCase("start")){
			if(!(sender instanceof Player)){
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"The Console cannot join an arena!");
				return true;
			}
			if(!Arenas.containsPlayer((Player)sender)){
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"You must join an arena first!");
				return true;
			}
			if(!((OIQArena)Arenas.getArenaFromPlayer((Player)sender)).getOIQ().getReadyPlayers().contains((Player)sender)){
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"You must ready up first!");
				return true;
			}
			if (!sender.hasPermission("te.arena.start")) {
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"You do not have permission to Ready Up!");
				return false;
			}
			aCommands.start((Player)sender);
			return true;
		}
		return false;
	}
}
