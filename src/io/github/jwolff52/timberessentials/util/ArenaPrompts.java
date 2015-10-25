package io.github.jwolff52.timberessentials.util;

import io.github.jwolff52.timberessentials.TimberEssentials;
import io.github.jwolff52.timberessentials.minigames.arenas.DefaultArena;
import io.github.jwolff52.timberessentials.minigames.arenas.OIQArena;
import io.github.jwolff52.timberessentials.minigames.events.ArenaSetupEvent;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.FixedSetPrompt;
import org.bukkit.conversations.MessagePrompt;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

public class ArenaPrompts {
	
	@SuppressWarnings("unused")
	private TimberEssentials te;
	
	public ArenaPrompts(TimberEssentials plugin){
		te=plugin;
	}
	
	public class SetupPrompt extends FixedSetPrompt {
		
		private DefaultArena arena;
		
		public SetupPrompt(DefaultArena a){
			super("?", "miss", "done");
			arena=a;
		}

		@Override
		public String getPromptText(ConversationContext context) {
			return "Setup mode for arena "+ChatColor.GREEN+arena.getName()+ChatColor.AQUA+" of type "+ChatColor.GREEN+arena.getType()+ChatColor.AQUA+". Type "+ChatColor.GOLD+"?"+ChatColor.AQUA+" for more information!";
		}

		@Override
		protected Prompt acceptValidatedInput(ConversationContext c, String in) {
			if(in.equalsIgnoreCase("?")){
				return new SetupHelpPrompt(arena);
			}else if(in.equalsIgnoreCase("miss")){
				return new SetupMissingPrompt(arena);
			}else if(in.equalsIgnoreCase("done")){
				return new SetupDonePrompt(arena);
			}
			return this;
		}
	}
	
	public class SetupHelpPrompt extends MessagePrompt{
		
		private DefaultArena arena;
		
		public SetupHelpPrompt(DefaultArena a){
			arena=a;
		}

		@Override
		public String getPromptText(ConversationContext context) {
			return 
					"\nmiss: "+ChatColor.GRAY+"Lists the missing positions for the current arena\n"+
					ChatColor.AQUA+"done: "+ChatColor.GRAY+"Exits setup mode";
		}

		@Override
		protected Prompt getNextPrompt(ConversationContext arg0) {
			return new SetupPrompt(arena);
		}
		
	}
	
	public class SetupMissingPrompt extends MessagePrompt{
		
		private DefaultArena arena;
		
		public SetupMissingPrompt(DefaultArena a){
			arena=a;
		}

		@Override
		public String getPromptText(ConversationContext context) {
			StringBuilder missing=new StringBuilder();
			missing.append("The following items are missing from this arena!\n");
			for(String s:((OIQArena)arena).getMissingItems()){
				missing.append(ChatColor.DARK_RED+s+ChatColor.AQUA+",");
			}
			try{
				return missing.toString().substring(0, missing.toString().lastIndexOf(','))+"!";
			}catch(StringIndexOutOfBoundsException e){
				return "There are no items missing!";
			}
		}

		@Override
		protected Prompt getNextPrompt(ConversationContext arg0) {
			return new SetupPrompt(arena);
		}
		
	}
	
	public class SetupDonePrompt extends MessagePrompt{
		
		private DefaultArena arena;
		
		public SetupDonePrompt(DefaultArena a){
			arena=a;
		}

		@Override
		public String getPromptText(ConversationContext context) {
			arena.toggleSetupMode((Player) context.getForWhom());
			Bukkit.getServer().getPluginManager().callEvent(new ArenaSetupEvent((Player)context.getForWhom(),arena.getName()));
			return "Exiting setup mode for "+ChatColor.GREEN+arena.getName()+ChatColor.AQUA+".";
		}

		@Override
		protected Prompt getNextPrompt(ConversationContext arg0) {
			return END_OF_CONVERSATION;
		}
		
	}
}
