package io.github.jwolff52.timberessentials.minigames.arenas;

public enum ArenaType {

	DEFAULT("DEFAULT"),OIQ("OIQ");
	
	private String name;
	
	ArenaType(String n){
		name=n;
	}
	
	public String getName(){
		return name;
	}
}
