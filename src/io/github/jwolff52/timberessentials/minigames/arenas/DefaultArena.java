package io.github.jwolff52.timberessentials.minigames.arenas;

import io.github.jwolff52.timberessentials.TimberEssentials;
import io.github.jwolff52.timberessentials.minigames.MinigameListener;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class DefaultArena implements Listener{
	
	private TimberEssentials te;
	
	private String name;
	private String onGameStartMsg;
	
	private boolean setupMode;
	
	private Location arenaPos1;
	private Location arenaPos2;
	private Location lobbyPos1;
	private Location lobbyPos2;
	private Location lobbyWarp;
	
	private ArenaType type;
	
	public DefaultArena(TimberEssentials te, String name, ArenaType t){
		this.te=te;
		this.name=name;
		type=t;
		setupMode=false;
		onGameStartMsg="Good luck!";
		Arenas.addArena(this);
	}
	
	public DefaultArena(TimberEssentials te, String name, String onGameStartMsg, ArenaType t, Location al1, Location al2, Location sl1, Location sl2, Location lw){
		this.te=te;
		this.name=name;
		this.onGameStartMsg=onGameStartMsg;
		setupMode=false;
		arenaPos1=al1;
		arenaPos2=al2;
		lobbyPos1=sl1;
		lobbyPos2=sl2;
		lobbyWarp=lw;
		type=t;
		Arenas.addArena(this);
	}
	
	public String getName(){
		return name;
	}
	
	public String getOnGameStartMsg(){
		return onGameStartMsg;
	}
	
	public boolean getSetupMode(){
		return setupMode;
	}
	
	public Location getArenaPos1(){
		return arenaPos1;
	}
	
	public Location getArenaPos2(){
		return arenaPos2;
	}
	
	public Location getLobbyPos1(){
		return lobbyPos1;
	}
	
	public Location getLobbyPos2(){
		return lobbyPos2;
	}
	
	public Location getLobbyWarp(){
		return lobbyWarp;
	}
	
	public ArenaType getType(){
		return type;
	}
	
	public void setOnGameStartMsg(String msg){
		onGameStartMsg=te.parseColors(msg);
	}
	
	public void setSetupMode(boolean s){
		setupMode=s;
	}
	
	public boolean toggleSetupMode(Player p){
		if(setupMode){
			setupMode=false;
			MinigameListener.removeArena(p);
		}else{
			setupMode=true;
			MinigameListener.addArena(this, p);
		}
		return setupMode;
	}
	
	public void setArenaPos1(Location l){
		arenaPos1=l;
	}
	
	public void setArenaPos2(Location l){
		arenaPos2=l;
	}
	
	public void setLobbyPos1(Location l){
		lobbyPos1=l;
	}
	
	public void setLobbyPos2(Location l){
		lobbyPos2=l;
	}
	
	public void setLobbyWarp(Location l){
		lobbyWarp=l;
	}
	
	public void setType(String t){
		type=ArenaType.valueOf(t);
	}
	
	public ArrayList<String> getMissingItems(){
		ArrayList<String> missing=new ArrayList<>();
		if(arenaPos1==null){
			missing.add("a1");
		}
		if(arenaPos2==null){
			missing.add("a2");
		}
		if(lobbyPos1==null){
			missing.add("l1");
		}
		if(lobbyPos2==null){
			missing.add("l2");
		}
		if(lobbyWarp==null){
			missing.add("lobbyWarp");
		}
		return missing;
	}
	
	public static class Arenas{
		
		private static ArrayList<DefaultArena> arenas=new ArrayList<>();
		private static HashMap<Player,DefaultArena> currentPlayers=new HashMap<>();
		
		public static void addArena(DefaultArena a){
			arenas.add(a);
		}
		
		public static void addPlayer(Player p, DefaultArena a){
			currentPlayers.put(p, a);
		}
		
		public static OIQArena removePlayer(Player p){
			if(currentPlayers.containsKey(p)){
				return (OIQArena) currentPlayers.remove(p);
			}
			return null;
		}
		
		public static DefaultArena getArenaFromName(String n){
			for(DefaultArena a:arenas){
				if(a.getName().equalsIgnoreCase(n)){
					return a;
				}
			}
			return null;
		}
		
		public static DefaultArena getArenaFromPlayer(Player p){
			if(currentPlayers.containsKey(p)){
				return currentPlayers.get(p);
			}
			return null;
		}
		
		public static boolean containsPlayer(Player p){
			return currentPlayers.containsKey(p);
		}
	}
}
