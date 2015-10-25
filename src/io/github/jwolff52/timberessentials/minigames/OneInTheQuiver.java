package io.github.jwolff52.timberessentials.minigames;

import io.github.jwolff52.timberessentials.minigames.arenas.DefaultArena.Arenas;
import io.github.jwolff52.timberessentials.minigames.arenas.OIQArena;
import io.github.jwolff52.timberessentials.minigames.events.ArenaDeathEvent;
import io.github.jwolff52.timberessentials.util.SettingsManager;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class OneInTheQuiver implements Listener{
	
	private String arenaName;
	private ArrayList<Player> currentPlayers;
	private ArrayList<Player> readyPlayers;
	private HashMap<Player,Integer> scores;
	private boolean inProgress;
	
	public static final int MAX_SCORE=SettingsManager.getInstance().getConfig().getInt("arena.oiq.MAX_SCORE");
	
	public OneInTheQuiver(String arenaName){
		this.arenaName=arenaName;
		currentPlayers=new ArrayList<>();
		readyPlayers=new ArrayList<>();
		scores=new HashMap<>();
		startReadyUpMessage();
		inProgress=false;
	}
	
	private void startReadyUpMessage(){
		Bukkit.getScheduler().scheduleSyncRepeatingTask(SettingsManager.getInstance().getPlugin(), new Runnable(){

			@Override
			public void run() {
				if(!inProgress){
					for(Player p:currentPlayers){
						p.sendMessage(SettingsManager.getInstance().getPlugin().getTitle()+"Do /readyup to start the game!");
					}
					if(currentPlayers.size()>=5&&currentPlayers.size()==readyPlayers.size()){
						start();
					}
				}else{
					for(Player p:readyPlayers){
						if(scores.get(p)>=MAX_SCORE){
							gameOver(p);
						}
					}
					if(readyPlayers.size()<2){
						gameOver();
					}
				}
			}
			
		}, 20, 2400);
	}
	
	public void start(){
		inProgress=true;
		for(Player p:readyPlayers){
			((OIQArena)Arenas.getArenaFromPlayer(p)).respawn(p);
			p.sendMessage(SettingsManager.getInstance().getPlugin().getTitle()+"Game Starting!");
		}
	}
	
	public void gameOver(){
		inProgress=false;
		for(Player p:readyPlayers){
			p.sendMessage(SettingsManager.getInstance().getPlugin().getTitle()+"All of the other players left! Game over!");
		}
	}
	
	public void gameOver(Player p){
		inProgress=false;
		p.sendMessage(SettingsManager.getInstance().getPlugin().getTitle()+"Congratulations you won!");
	}
	
	public void addPlayer(Player p){
		currentPlayers.add(p);
	}
	
	public void removePlayer(Player p){
		currentPlayers.remove(p);
		readyPlayers.remove(p);
		scores.remove(p);
	}
	
	public void readyUp(Player p){
		readyPlayers.add(p);
		scores.put(p, 0);
	}
	
	public ArrayList<Player> getReadyPlayers(){
		return readyPlayers;
	}
	
	public int getScore(Player p){
		return scores.get(p);
	}
	
	public void notifyPlayers(String message) {
		for(Player p:currentPlayers){
			p.sendMessage(message);
		}
	}

	public void notifyPlayers(String message, Player damager, Player target) {
		for(Player p:currentPlayers){
			if(!p.getUniqueId().toString().equals(damager.getUniqueId().toString())||!p.getUniqueId().toString().equals(damager.getUniqueId().toString())){
				p.sendMessage(message);
			}
		}
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent e){
		Player target;
		if(!(e.getEntity() instanceof Player)){
			return;
		}
		target=(Player)e.getEntity();
		boolean inGame=false;
		for(int i=0;i<currentPlayers.size();i++){ //Check if the player is in the current game
			if(target.equals(currentPlayers.get(i))){
				inGame=true;
				break;
			}
		}
		if(!inGame){
			return;
		}
		if(e.getDamager() instanceof Player){
			Bukkit.getServer().getPluginManager().callEvent(new ArenaDeathEvent((Player)e.getDamager(), target, arenaName));
		}else if(e.getDamager() instanceof Arrow){
			Bukkit.getServer().getPluginManager().callEvent(new ArenaDeathEvent((Arrow)e.getDamager(), target, arenaName));
		}else{
			return;
		}
	}
}
