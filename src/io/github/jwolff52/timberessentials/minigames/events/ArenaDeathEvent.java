package io.github.jwolff52.timberessentials.minigames.events;

import io.github.jwolff52.timberessentials.minigames.arenas.DefaultArena.Arenas;
import io.github.jwolff52.timberessentials.minigames.arenas.OIQArena;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ArenaDeathEvent extends Event{
	
	private OIQArena arena;
	private Player target;
	private Player damager;
	
	public ArenaDeathEvent(Player damager, Player target, String arenaName){
		assert(damager!=null);
		setArena((OIQArena)Arenas.getArenaFromName(arenaName));
		this.setDamager(damager);
		this.setTarget(target);
	}
	
	public ArenaDeathEvent(Arrow arrow, Player target, String arenaName){
		setArena((OIQArena)Arenas.getArenaFromName(arenaName));
		if(arrow.getShooter() instanceof Player){
			this.setDamager((Player)arrow.getShooter());
		}
		this.setTarget(target);
	}

	public OIQArena getArena() {
		return arena;
	}

	public Player getTarget() {
		return target;
	}

	public Player getDamager() {
		return damager;
	}

	private void setArena(OIQArena arena) {
		this.arena = arena;
	}

	private void setTarget(Player target) {
		this.target = target;
	}

	private void setDamager(Player damager) {
		this.damager = damager;
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
