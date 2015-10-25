package io.github.jwolff52.timberessentials.minigames.arenas;

import io.github.jwolff52.timberessentials.TimberEssentials;
import io.github.jwolff52.timberessentials.minigames.OneInTheQuiver;

import java.util.ArrayList;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

public class OIQArena extends DefaultArena{
	
	private ArrayList<Location> playerSpawns;
	private OneInTheQuiver oiq;
	
	public OIQArena(TimberEssentials te, String name){
		super(te, name, ArenaType.OIQ);
		setOnGameStartMsg("You have one shot, make it count!");
		playerSpawns=new ArrayList<>();
		oiq=new OneInTheQuiver(name);
		Bukkit.getServer().getPluginManager().registerEvents(oiq, te);
	}

	public OIQArena(TimberEssentials te, String name, String onGameStartMsg, Location al1, Location al2, Location sl1, Location sl2, Location lw, ArrayList<Location> playerSpawns) {
		super(te, name, onGameStartMsg, ArenaType.OIQ, al1, al2, sl1, sl2, lw);
		this.playerSpawns=playerSpawns;
	}
	
	public void addSpawn(Location spawn){
		playerSpawns.add(spawn);
	}
	
	public ArrayList<Location> getPlayerSpawns(){
		return playerSpawns;
	}
	
	public void respawn(Player p){
		int i=playerSpawns.size();
		System.out.println(i);
		for(Location l:playerSpawns){
			System.out.println(l.toString());
		}
		p.teleport(playerSpawns.get(new Random().nextInt(i)), TeleportCause.PLUGIN);
	}
	
	public OneInTheQuiver getOIQ(){
		return oiq;
	}
	
	public ArrayList<String> getMissingItems(){
		ArrayList<String>missing=new ArrayList<>();
		if(playerSpawns.size()==0){
			missing.add("playerSpawns");
		}
		missing.addAll(super.getMissingItems());
		return missing;
	}
	
}
