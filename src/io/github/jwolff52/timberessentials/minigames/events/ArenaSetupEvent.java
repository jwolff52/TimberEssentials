package io.github.jwolff52.timberessentials.minigames.events;

import io.github.jwolff52.timberessentials.minigames.arenas.DefaultArena;
import io.github.jwolff52.timberessentials.minigames.arenas.DefaultArena.Arenas;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArenaSetupEvent extends Event{
	
	private Player player;
	private DefaultArena arena;
	
	public ArenaSetupEvent(Player p, String a){
		player=p;
		arena=Arenas.getArenaFromName(a);
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public DefaultArena getArena(){
		return arena;
	}

	private static final HandlerList handlers=new HandlerList();
	
	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}
}
