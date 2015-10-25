package io.github.jwolff52.timberessentials.commands;

import io.github.jwolff52.timberessentials.TimberEssentials;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageOfTheDay {

	private TimberEssentials te;
	
	public MessageOfTheDay(TimberEssentials te){
		this.te=te;
	}
	
	public boolean setMotd(CommandSender sender, String[] args, boolean isInGame){
		StringBuilder motd=new StringBuilder();
		for(int i=1;i<args.length-1;i++){
			motd.append(args[i]+" ");
		}
		motd.append(args[args.length-1]);
		if(isInGame){
			if(!sender.hasPermission("te.setmotd.ingame")&&sender instanceof Player){
				te.setInGameMotd(te.parseColors(te.parseNewLines(motd.toString())));
				te.sm.getConfig().set("motd.ingame", motd.toString());
				te.saveDefaultConfig();
				sender.sendMessage(te.getTitle()+"In-Game MOTD set to: \"" + ChatColor.WHITE + motd.toString() + ChatColor.WHITE + "\" and will be displayed as \""+ChatColor.RESET+te.getInGameMotd()+"\"!");
				return true;
			}else{
				sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"You do not have permission to preform this command!");
				return true;
			}
		}
		if(!sender.hasPermission("te.setmotd.server")&&sender instanceof Player){
			if(te.isUsingServerMotd()){
				te.setServerMotd(te.parseColors(motd.toString()));
				te.sm.getConfig().set("motd.server", motd.toString());
				te.saveDefaultConfig();
				sender.sendMessage(te.getTitle()+"Server MOTD set to: \"" + ChatColor.WHITE + motd.toString() + ChatColor.WHITE + "\" and will be displayed as \""+ChatColor.RESET+te.getServerMotd()+"\"!");
				return true;
			}else{
				sender.sendMessage(te.parseNewLines(te.getTitle()+ChatColor.DARK_RED+"Config option "+ChatColor.GOLD+"\"useServerMotd\""+ChatColor.DARK_RED+" is set to "+ChatColor.GOLD+"\"false\""+ChatColor.DARK_RED+" in the "+ChatColor.GOLD+"config.yml!"+ChatColor.DARK_RED+"\nSet it to "+ChatColor.GOLD+"\"true\""+ChatColor.DARK_RED+" then do "+ChatColor.GOLD+"/tereload"+ChatColor.DARK_RED+" to use this command!"));
				return true;		}
		}else{
			sender.sendMessage(te.getTitle()+ChatColor.DARK_RED+"You do not have permission to preform this command!");
			return true;
		}	
	}
	
	public void getMotd(CommandSender sender){
		sender.sendMessage(te.parseVariables(te.getInGameMotd(), sender));
	}
}
