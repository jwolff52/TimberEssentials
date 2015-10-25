package io.github.jwolff52.timberessentials.commands;

import io.github.jwolff52.timberessentials.TimberEssentials;
import io.github.jwolff52.timberessentials.minigames.arenas.ArenaType;
import io.github.jwolff52.timberessentials.minigames.arenas.DefaultArena;
import io.github.jwolff52.timberessentials.minigames.arenas.DefaultArena.Arenas;
import io.github.jwolff52.timberessentials.minigames.arenas.OIQArena;
import io.github.jwolff52.timberessentials.minigames.events.ArenaJoinEvent;
import io.github.jwolff52.timberessentials.minigames.events.ArenaSetupEvent;
import io.github.jwolff52.timberessentials.util.ArenaPrompts;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.ConversationPrefix;
import org.bukkit.entity.Player;

public class Arena {

	private TimberEssentials te;
	private ConversationFactory convFactory;
	private ArenaPrompts ap;
	
	public Arena(TimberEssentials plugin){
		te=plugin;
		convFactory=new ConversationFactory(te);
		ap=new ArenaPrompts(te);
	}
	
	public void setup(Player player, final DefaultArena arena){
		if(arena==null){
			player.sendMessage(te.getTitle()+"That arena does not exist!");
			return;
		}
		arena.toggleSetupMode(player);
		if(arena.getSetupMode()){
			te.getServer().getPluginManager().callEvent(new ArenaSetupEvent(player, arena.getName()));
			convFactory.withFirstPrompt(ap.new SetupPrompt(arena)).withLocalEcho(false).withPrefix(new ConversationPrefix(){

				@Override
				public String getPrefix(ConversationContext c) {
					return te.getTitle();
				}
				
			}).addConversationAbandonedListener(new ConversationAbandonedListener(){

				@Override
				public void conversationAbandoned(ConversationAbandonedEvent arg0) {
					arena.setSetupMode(false);
				}
				
			}).buildConversation(player).begin();
		}else{
			player.sendMessage(te.getTitle()+"Setup mode is already enabled!\nIf this is an error reload TimberEssentials!");
		}
	}
	
	public void add(Player player, String[] args){
		if(args[0].equalsIgnoreCase("gui")){
			player.sendMessage(te.getTitle()+ChatColor.DARK_RED+"You cannot create an arena named "+ChatColor.GOLD+args[0]+ChatColor.DARK_RED+", sorry!");
			return;
		}
		boolean invalidType=true;
		DefaultArena arena=null;
		switch(ArenaType.valueOf(args[0])){
			case DEFAULT:
				break;
			case OIQ:
				invalidType=false;
				arena=new OIQArena(te, args[1]);
				break;
		}
		if(invalidType){
			player.sendMessage(te.getTitle()+ChatColor.DARK_RED+"The type "+ChatColor.GOLD+args[0]+ChatColor.DARK_RED+" does not exist!");
			return;
		}
		Arenas.addArena(arena);
		if(args.length==1){
			te.sm.addArena((OIQArena)arena, "DIAMOND_SWORD");
		}else{
			te.sm.addArena((OIQArena)arena, args[1]);
		}
		player.sendMessage(te.getTitle()+"Successfully created a "+ChatColor.GOLD+args[0]+ChatColor.AQUA+" arena called "+ChatColor.GOLD+args[1]+ChatColor.AQUA+"!");
	}
	
	public boolean join(Player player, Location loc, String name){
		if(!player.hasPermission("te.arenas.allow."+name.toLowerCase())){
			player.sendMessage(te.getTitle()+ChatColor.DARK_RED+"You do not have permission to join "+ChatColor.GOLD+name+ChatColor.DARK_RED+"!");
			return true;
		}
		DefaultArena a=Arenas.getArenaFromName(name.toLowerCase());
		if(a==null){
			player.sendMessage(te.getTitle()+ChatColor.DARK_RED+"The arena "+ChatColor.GOLD+name+ChatColor.DARK_RED+" does not exist!");
			return true;
		}
		if(Arenas.getArenaFromPlayer(player)!=null){
			player.sendMessage(te.getTitle()+ChatColor.DARK_RED+"You are already in an arena! Type /leave to join a different one!");
			return true;
		}
		Bukkit.getServer().getPluginManager().callEvent(new ArenaJoinEvent(player, name.toLowerCase()));
		te.sm.getPlayerData(player).set("lastlocation.world", loc.getWorld().getName());
		te.sm.getPlayerData(player).set("lastlocation.x", loc.getX());
		te.sm.getPlayerData(player).set("lastlocation.y", loc.getY());
		te.sm.getPlayerData(player).set("lastlocation.z", loc.getZ());
		te.sm.getPlayerData(player).set("lastlocation.yaw", loc.getYaw());
		te.sm.getPlayerData(player).set("lastlocation.pitch", loc.getPitch());
		return true;
	}
	
	public void readyUp(Player player){
		OIQArena arena=(OIQArena)Arenas.getArenaFromPlayer(player);
		arena.getOIQ().readyUp(player);
		player.sendMessage(te.getTitle()+"You readied up!");
	}
	
	public void start(Player player){
		OIQArena arena=(OIQArena)Arenas.getArenaFromPlayer(player);
		arena.getOIQ().start();
		player.sendMessage(te.getTitle()+"You readied up!");
	}
}
