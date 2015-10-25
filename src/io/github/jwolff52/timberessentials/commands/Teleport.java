package io.github.jwolff52.timberessentials.commands;

import io.github.jwolff52.timberessentials.TimberEssentials;

import java.util.Calendar;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class Teleport{

	private TimberEssentials te;
	
//	private ArrayList<Player> teleportCooldown;
	
	public Teleport(TimberEssentials te){
		this.te=te;
//		teleportCooldown=new ArrayList<>();
	}
	
	public void tp(Player player, Player target){
//		if(!teleportCooldown.contains(player)){
			Location loc=player.getLocation();
			player.teleport(target, TeleportCause.COMMAND);
			te.sm.getPlayerData(player).set("timestamps.lastteleport", Calendar.getInstance().getTimeInMillis());
			player.sendMessage(te.getTitle()+"Successfully teleported to "+target.getName());
			te.sm.getPlayerData(player).set("lastlocation.world", loc.getWorld().getName());
			te.sm.getPlayerData(player).set("lastlocation.x", loc.getX());
			te.sm.getPlayerData(player).set("lastlocation.y", loc.getY());
			te.sm.getPlayerData(player).set("lastlocation.z", loc.getZ());
			te.sm.getPlayerData(player).set("lastlocation.yaw", loc.getYaw());
			te.sm.getPlayerData(player).set("lastlocation.pitch", loc.getPitch());
//			teleportCooldown.add(player);
			return;
//		}
//		player.sendMessage(te.getTitle()+"You just teleported! Your TARDIS needs to recharge!");
	}
	
	public void tppos(Player player, Location target){
//		if(!teleportCooldown.contains(player)){
			Location loc=player.getLocation();
			player.teleport(target, TeleportCause.COMMAND);
			te.sm.getPlayerData(player).set("timestamps.lastteleport", Calendar.getInstance().getTimeInMillis());
			player.sendMessage(te.getTitle()+"Successfully teleported to "+target.getX()+" "+target.getY()+" "+target.getZ()+"!");
			te.sm.getPlayerData(player).set("lastlocation.world", loc.getWorld().getName());
			te.sm.getPlayerData(player).set("lastlocation.x", loc.getX());
			te.sm.getPlayerData(player).set("lastlocation.y", loc.getY());
			te.sm.getPlayerData(player).set("lastlocation.z", loc.getZ());
			te.sm.getPlayerData(player).set("lastlocation.yaw", loc.getYaw());
			te.sm.getPlayerData(player).set("lastlocation.pitch", loc.getPitch());
//			teleportCooldown.add(player);
			return;
//		}
//		player.sendMessage(te.getTitle()+"You just teleported! Your TARDIS needs to recharge!");
	}
	
	public void setWarp(Player player, String[] args){
		if(args[0].equalsIgnoreCase("gui")){
			player.sendMessage(te.getTitle()+ChatColor.DARK_RED+"You cannot create a warp named "+ChatColor.GOLD+args[0]+ChatColor.DARK_RED+", sorry!");
			return;
		}
		Location loc=player.getLocation();
		if(args.length == 1){
			te.sm.addWarp(args[0], loc, "FEATHER");
			te.addWarp(args[0], loc);
			player.sendMessage(te.getTitle()+"Successfully created the warp "+args[0]);
		}else{
			te.sm.addWarp(args[0], loc, args[1]);
			te.addWarp(args[0], loc);
			player.sendMessage(te.getTitle()+"Successfully created the warp "+args[0]);
		}
	}
	
	public void delWarp(String[] args){
		te.sm.delWarp(args[0]);
	}
	
	public boolean warp(Player player, String name, Location loc){
		if(!player.hasPermission("te.warps.allow."+name)){
			player.sendMessage(te.getTitle()+ChatColor.DARK_RED+"You do not have permission to warp to "+ChatColor.GOLD+name+ChatColor.DARK_RED+"!");
			return true;
		}
		if(loc==null){
			player.sendMessage(te.getTitle()+ChatColor.DARK_RED+"The warp "+ChatColor.GOLD+name+ChatColor.DARK_RED+" does not exist!");
			return true;
		}
		te.getLogger().info(loc.toString());
		player.teleport(loc, TeleportCause.COMMAND);
		player.sendMessage(te.getTitle()+"You were successfully teleported to "+ChatColor.GOLD+name+ChatColor.AQUA+"!");
		return true;
	}
}
