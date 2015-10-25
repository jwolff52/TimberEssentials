package io.github.jwolff52.timberessentials.commands;

import io.github.jwolff52.timberessentials.TimberEssentials;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Moderation {
	
	private TimberEssentials te;
	
	public Moderation(TimberEssentials te){
		this.te=te;
	}
	
	@SuppressWarnings("deprecation")
	public void kick(CommandSender sender, String[] args){
		Player target=te.getServer().getPlayer(args[0]);
		if(target==null){
			sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"That player does not exsist or is not online!");
		}
		StringBuilder kickMsgSB=new StringBuilder();
		for(int i=1;i<args.length;i++){
			kickMsgSB.append(args[i]);
		}
		target.kickPlayer(kickMsgSB.toString());
		te.getServer().dispatchCommand(sender, "lbbroadcast "+ChatColor.DARK_RED+"Player "+ChatColor.YELLOW+target.getName()+ChatColor.DARK_RED+" was kicked by "+ChatColor.YELLOW+sender.getName()+ChatColor.DARK_RED+" for "+ChatColor.YELLOW+kickMsgSB.toString()+ChatColor.DARK_RED+"!");
	}

	@SuppressWarnings("deprecation")
	public void ban(CommandSender sender, String[] args){
		Player target=te.getServer().getPlayer(args[0]);
		if(target==null){
			sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"That player does not exsist or is not online!");
		}
		StringBuilder kickMsgSB=new StringBuilder();
		for(int i=1;i<args.length;i++){
			kickMsgSB.append(args[i]);
		}
		target.kickPlayer(kickMsgSB.toString());
		target.setBanned(true);
		te.getServer().dispatchCommand(sender, "lbbroadcast "+ChatColor.DARK_RED+"Player "+ChatColor.YELLOW+target.getName()+ChatColor.DARK_RED+" was banned by "+ChatColor.YELLOW+sender.getName()+ChatColor.DARK_RED+" for "+ChatColor.YELLOW+kickMsgSB.toString()+ChatColor.DARK_RED+"!");
	}
}
