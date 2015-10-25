package io.github.jwolff52.timberessentials.commands;

import io.github.jwolff52.timberessentials.TimberEssentials;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Chat {

	private TimberEssentials te;
	
	public Chat(TimberEssentials te){
		this.te=te;
	}
	
	@SuppressWarnings("deprecation")
	public boolean setNick(Player player, String[] args){
		if(args[0].length()>te.getNickMaxLength()){
			player.sendMessage(te.getTitle()+"Your nickname must be less than "+te.getNickMaxLength()+" characters!");
			return true;
		}
		if(args.length>1){
			if(!player.hasPermission("te.nick.other")){
				player.sendMessage(te.getTitle()+ChatColor.DARK_RED+"You do not have permission to preform this command!");
				return true;
			}
			Player target=Bukkit.getServer().getPlayer(args[1]);
			if(target == null){
				player.sendMessage(te.getTitle()+ChatColor.DARK_RED+args[1]+" is not online or does not exsist!");
				return true;
			}
			if(args[0].equals("off")){
				te.sm.getPlayerData(target).set("nickname", null);
				player.sendMessage(te.getTitle()+args[1]+"\'s nickname was turned off!");
				return true;
			}else{
				te.sm.getPlayerData(target).set("nickname", te.getNickPrefix()+args[0]);
			}
			te.sm.savePlayers();
			if(!target.hasPermission("te.nick.color")){
				player.sendMessage(te.getTitle()+args[1]+"\'s nickname was set to "+te.getNickPrefix()+args[0]+"!");
			}else{
				player.sendMessage(te.getTitle()+args[1]+"\'s nickname was set to "+te.parseColors(te.getNickPrefix()+args[0])+"!");
			}
			return true;
		}
		if(!player.hasPermission("te.nick.self")){
			player.sendMessage(te.getTitle()+ChatColor.DARK_RED+"You do not have permission to preform this command!");
			return true;
		}
		if(args[0].equals("off")){
			te.sm.getPlayerData(player).set("nickname", null);
			player.sendMessage(te.getTitle()+"Your nickname was turned off!");
			return true;
		}else{
			te.sm.getPlayerData(player).set("nickname", te.getNickPrefix()+args[0]);
		}
		te.sm.savePlayers();
		if(!player.hasPermission("te.nick.color")){
			player.sendMessage(te.getTitle()+"Your nickname was set to "+te.getNickPrefix()+args[0]+"!");
		}else{
			player.sendMessage(te.getTitle()+"Your nickname was set to "+te.parseColors(te.getNickPrefix()+args[0])+"!");
		}
		return true;
	}
}
