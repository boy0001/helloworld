// TODO force default (currently it just rejects)

package io.github.boy0001.plugin;
import java.util.HashMap;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
public final class HelloWorld extends JavaPlugin implements Listener {
	Timer timer = new Timer ();
	TimerTask mytask = new TimerTask () {
		String mymode;
		@Override
	    public void run () {
	    	for(Player player:getServer().getOnlinePlayers()){
	    		try {
	    		try {
				if (getConfig().getString("multiworld."+player.getWorld().getName()+".override").equalsIgnoreCase("true")) {
					mymode = getConfig().getString("multiworld."+player.getWorld().getName()+".mode");
				}
				else {
					mymode = "false";
				}
	    		}
	    		catch (Exception e) {
	    			mymode = "false";
	    		}
	    		if ( ((getConfig().getString("Players."+player.getName()).equalsIgnoreCase("NEAR"))&&(player.hasPermission("compassmodes.near")) && (mymode == "false")) || (mymode.equalsIgnoreCase("NEAR"))){
	    			double last = 0;
	        		Player sp = null;
	        		
	        		for(Player all:getServer().getOnlinePlayers()){
						if ((all.getLocation().getWorld() == player.getLocation().getWorld()) && (all.getName() != player.getName())) {
							double dist = player.getLocation().distance(all.getLocation());
							if ((((sp == null) || (dist < last))&&((dist < Integer.parseInt(getConfig().getString("range")))||(getConfig().getString("range") == "0")))) {
								sp = all;
								last = dist;
							}
						}
					}
					if (sp != null) {
						player.setCompassTarget(sp.getLocation());
					}
	        	}
	    		if ( ((getConfig().getString("Players."+player.getName()).equalsIgnoreCase("RANDOM"))&&(player.hasPermission("compassmodes.random")) && (mymode == "false")) || (mymode.equalsIgnoreCase("RANDOM"))) {

	    			Random rand1 = new Random();
	    			int random1 = rand1.nextInt(500)-250;
	    			int random2 = rand1.nextInt(500)-250;
	    			Location myloc = new Location(player.getWorld(),player.getLocation().getX()+random1,64,player.getLocation().getZ()+random2);
	    			player.setCompassTarget(myloc);
	    		}
	    		if (((Bukkit.getPlayer(getConfig().getString("Players."+player.getName())) != null)&&(player.hasPermission("compassmodes.player"))) || (mymode.equalsIgnoreCase(getConfig().getString("Players."+player.getName())))) {
	    			if (Bukkit.getPlayer(getConfig().getString("Players."+player.getName())).getWorld() == player.getWorld()) {
	    				double dist = player.getLocation().distance(Bukkit.getPlayer(getConfig().getString("Players."+player.getName())).getLocation());
	    				if ((getConfig().getString("range")=="0")||(dist < Integer.parseInt(getConfig().getString("range")))) {
	    					player.setCompassTarget(Bukkit.getPlayer(getConfig().getString("Players."+player.getName())).getLocation());
	    				}
	    				
	    			}
	    		}
	    		}
	
		catch (Exception e) {		
		}
	    }
		}
	};
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) { 	
		if (event instanceof PlayerDeathEvent) 	{
        Player player = (Player) event.getEntity();
        Location loc = player.getLocation();
        deathpoints.put(player.getName(),loc.getX()+","+loc.getZ()+","+player.getWorld().getName());
        
        //save location
	}
	}
	
	HashMap<String, String> deathpoints = new HashMap<String, String>();
	@Override
    public void onEnable(){
		deathpoints = new HashMap<String, String>();
		try {
			getConfig().getString("range");
		}
		catch (Exception e) {
			getConfig().set("range","-1");
		}
		timer.schedule (mytask, 0l, 1000);
    	this.saveDefaultConfig();
    	getServer().getPluginManager().registerEvents(this, this);
    	
        // TODO get list of players and read their compass mode
    }
    // Command handling
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	// TODO permissions
    	if(cmd.getName().equalsIgnoreCase("compasstest")){
    		sender.sendMessage("This is a totally useless test command");
    	}
    	else if (cmd.getName().equalsIgnoreCase("compass")) {
    		if (args.length > 0){
	    		if (args[0].equalsIgnoreCase("list")){
	    			sender.sendMessage(ChatColor.GOLD+"Modes:");
	    			
	    			String mycolor;
	    			String mode = "near";
	    			if ((this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".override").equalsIgnoreCase("true")) && (this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".mode").equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if ((this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".override").equalsIgnoreCase("true"))==false) { if ((getConfig().getString("Players."+((Player) sender).getName()).equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if (((Player) sender).hasPermission("compassmodes."+mode)) { mycolor = ChatColor.GREEN + "";}else {mycolor = ChatColor.RED + "";}}else { mycolor = ChatColor.GRAY + "";}
	    			sender.sendMessage(mycolor + " - Near");
	    			
	    			mode = "random";
	    			if ((this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".override").equalsIgnoreCase("true")) && (this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".mode").equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if ((this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".override").equalsIgnoreCase("true"))==false) { if ((getConfig().getString("Players."+((Player) sender).getName()).equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if (((Player) sender).hasPermission("compassmodes."+mode)) { mycolor = ChatColor.GREEN + "";}else {mycolor = ChatColor.RED + "";}}else { mycolor = ChatColor.GRAY + "";}
	    			sender.sendMessage(mycolor + " - Random");

	    			mode = "current";
	    			if ((this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".override").equalsIgnoreCase("true")) && (this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".mode").equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if ((this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".override").equalsIgnoreCase("true"))==false) { if ((getConfig().getString("Players."+((Player) sender).getName()).equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if (((Player) sender).hasPermission("compassmodes."+mode)) { mycolor = ChatColor.GREEN + "";}else {mycolor = ChatColor.RED + "";}}else { mycolor = ChatColor.GRAY + "";}
	    			sender.sendMessage(mycolor + " - Current");
	    			
	    			mode = "location";
	    			if ((this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".override").equalsIgnoreCase("true")) && (this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".mode").equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if ((this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".override").equalsIgnoreCase("true"))==false) { if ((StringUtils.countMatches(String.valueOf(this.getConfig().getString("Players."+((Player) sender).getName())), ",") == 1)) {mycolor = ChatColor.BLUE + "";}else if (((Player) sender).hasPermission("compassmodes."+mode)) { mycolor = ChatColor.GREEN + "";}else {mycolor = ChatColor.RED + "";}}else { mycolor = ChatColor.GRAY + "";}
	    			sender.sendMessage(mycolor + " - X,Y");
	    			
	    			mode = "player";
	    			if ((this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".override").equalsIgnoreCase("true")) && (this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".mode").equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if ((this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".override").equalsIgnoreCase("true"))==false) { if ((Bukkit.getPlayer(this.getConfig().getString("Players."+((Player) sender).getName()))!=null)) {mycolor = ChatColor.BLUE + "";}else if (((Player) sender).hasPermission("compassmodes."+mode)) { mycolor = ChatColor.GREEN + "";}else {mycolor = ChatColor.RED + "";}}else { mycolor = ChatColor.GRAY + "";}
	    			sender.sendMessage(mycolor + " - <Player>");
	    			
	    			mode = "default";
	    			if ((this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".override").equalsIgnoreCase("true")) && (this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".mode").equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if ((this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".override").equalsIgnoreCase("true"))==false) { if ((getConfig().getString("Players."+((Player) sender).getName()).equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if (((Player) sender).hasPermission("compassmodes."+mode)) { mycolor = ChatColor.GREEN + "";}else {mycolor = ChatColor.RED + "";}}else { mycolor = ChatColor.GRAY + "";}
	    			sender.sendMessage(mycolor + " - Default");
	    			
	    			mode = "bed";
	    			if ((this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".override").equalsIgnoreCase("true")) && (this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".mode").equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if ((this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".override").equalsIgnoreCase("true"))==false) { if ((getConfig().getString("Players."+((Player) sender).getName()).equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if (((Player) sender).hasPermission("compassmodes."+mode)) { mycolor = ChatColor.GREEN + "";}else {mycolor = ChatColor.RED + "";}}else { mycolor = ChatColor.GRAY + "";}
	    			sender.sendMessage(mycolor + " - Bed");
	    			
	    			mode = "death";
	    			if ((this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".override").equalsIgnoreCase("true")) && (this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".mode").equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if ((this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".override").equalsIgnoreCase("true"))==false) { if ((getConfig().getString("Players."+((Player) sender).getName()).equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if (((Player) sender).hasPermission("compassmodes.deathpoint")) { mycolor = ChatColor.GREEN + "";}else {mycolor = ChatColor.RED + "";}}else { mycolor = ChatColor.GRAY + "";}
	    			sender.sendMessage(mycolor + " - Death");
	    			
	    			mode = "north";
	    			if ((this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".override").equalsIgnoreCase("true")) && (this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".mode").equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if ((this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".override").equalsIgnoreCase("true"))==false) { if ((getConfig().getString("Players."+((Player) sender).getName()).equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if (((Player) sender).hasPermission("compassmodes.location")) { mycolor = ChatColor.GREEN + "";}else {mycolor = ChatColor.RED + "";}}else { mycolor = ChatColor.GRAY + "";}
	    			sender.sendMessage(mycolor + " - North");
	    			
	    			mode = "east";
	    			if ((this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".override").equalsIgnoreCase("true")) && (this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".mode").equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if ((this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".override").equalsIgnoreCase("true"))==false) { if ((getConfig().getString("Players."+((Player) sender).getName()).equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if (((Player) sender).hasPermission("compassmodes.location")) { mycolor = ChatColor.GREEN + "";}else {mycolor = ChatColor.RED + "";}}else { mycolor = ChatColor.GRAY + "";}
	    			sender.sendMessage(mycolor + " - East");
	    			
	    			mode = "south";
	    			if ((this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".override").equalsIgnoreCase("true")) && (this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".mode").equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if ((this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".override").equalsIgnoreCase("true"))==false) { if ((getConfig().getString("Players."+((Player) sender).getName()).equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if (((Player) sender).hasPermission("compassmodes.location")) { mycolor = ChatColor.GREEN + "";}else {mycolor = ChatColor.RED + "";}}else { mycolor = ChatColor.GRAY + "";}
	    			sender.sendMessage(mycolor + " - South");
	    			
	    			mode = "west";
	    			if ((this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".override").equalsIgnoreCase("true")) && (this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".mode").equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if ((this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".override").equalsIgnoreCase("true"))==false) { if ((getConfig().getString("Players."+((Player) sender).getName()).equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if (((Player) sender).hasPermission("compassmodes.location")) { mycolor = ChatColor.GREEN + "";}else {mycolor = ChatColor.RED + "";}}else { mycolor = ChatColor.GRAY + "";}
	    			sender.sendMessage(mycolor + " - West");
	    			
//	    			compassmodes.near 
//	    			compassmodes.random
//	    			compassmodes.current
//	    			compassmodes.location
//	    			compassmodes.default
//	    			compassmodes.other
	    			
	    			
	    		}
	    		else if (args[0].equalsIgnoreCase("help")){
	    			sender.sendMessage(ChatColor.GOLD+"Commands:");
	    			sender.sendMessage(ChatColor.GREEN+" - /compass <mode> - sets your compass mode");
	    			sender.sendMessage(ChatColor.GREEN+" - /compass <mode> <player> - sets a player's mode");
	    			sender.sendMessage(ChatColor.GREEN+" - /compass list - a list of all the modes");
	    			sender.sendMessage(ChatColor.GREEN+" - /compass reload - reloads the config file");
	    			sender.sendMessage(ChatColor.GREEN+" - /compass help - shows this page");
	    		}
	    		else if ((args[0].equalsIgnoreCase("reload"))){
	    			this.reloadConfig();
	    			this.saveDefaultConfig();
	    			if (sender instanceof Player) {
	    				if (((Player) sender).hasPermission("compassmodes.reload")) {
	    					sender.sendMessage(ChatColor.GRAY + "Successfully reloaded" + ChatColor.RED + "CompassModes"+ChatColor.WHITE + ".");
	    				}
	    				else {
	    					sender.sendMessage(ChatColor.RED + "Sorry, you do not have permission to perform this action.");
	    				}
	    			}
	    			else {
	    				System.out.println("Successfully reloaded CompassModes");
	    			}
	    			// RELOAD CONFIG
	    		}
	    		else if (args.length == 1){
	    			if (sender instanceof Player) {
	    				if ((args[0].equalsIgnoreCase("CURRENT"))&&(((Player) sender).hasPermission("compassmodes.current"))){
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+sender.getName(),((Player) sender).getLocation().getX()+","+((Player) sender).getLocation().getZ());
	    					if (this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".override").equalsIgnoreCase("true")) {
	    						sender.sendMessage("Your preference is currently being overridden");
	    					}
	    					else {
	    						((Player) sender).setCompassTarget(((Player) sender).getLocation());
	    						sender.sendMessage("Compass set to current location");
	    					}
	    					this.saveConfig();
	    					
	    				}
	    				else if ((args[0].equalsIgnoreCase("NEAR"))&&(((Player) sender).hasPermission("compassmodes.near"))){
	    					this.reloadConfig();
	    	        		double last = 0;
	    	        		Player sp = null;
	    	        		
	    	        		for(Player all:getServer().getOnlinePlayers()){
	    						if ((all.getLocation().getWorld() == ((Player) sender).getLocation().getWorld()) && (all.getName() != ((Player) sender).getName())) {
	    							double dist = ((Player) sender).getLocation().distance(all.getLocation());
	    							if (((sp == null) || (dist < last))&&((dist < Integer.parseInt(getConfig().getString("range")))||(getConfig().getString("range") == "0"))) {
	    								sp = all;
	    								last = dist;
	    							}
	    						}
	    	        		}
	    					if (sp != null) {
		    					if (this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".override").equalsIgnoreCase("true")) {
		    						sender.sendMessage("Your preference is currently being overridden");
		    					}
		    					else {
		    						((Player) sender).setCompassTarget(sp.getLocation());
		    						sender.sendMessage("Currently tracking "+sp.getName());
		    					}
	    						this.getConfig().set("Players."+sender.getName(),"NEAR");
	    						this.saveConfig();
	    					}
	    					
	    					else {
	    						sender.sendMessage("[Error] There are no players nearby");
	    					}
	    					
	    				}
	    				else if ((args[0].equalsIgnoreCase("DEFAULT"))&&(((Player) sender).hasPermission("compassmodes.default"))){
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+sender.getName(),"DEFAULT");
	    					((Player) sender).setCompassTarget(((Player) sender).getWorld().getSpawnLocation());
	    					this.saveConfig();
	    					sender.sendMessage("Compass set to spawnpoint");
	    				
	    				}
	    				else if ((args[0].equalsIgnoreCase("BED"))&&(((Player) sender).hasPermission("compassmodes.bed"))){
	    					this.reloadConfig();
	    					if (((Player) sender).getBedSpawnLocation() != null) {
	    						this.getConfig().set("Players."+sender.getName(),"BED");
	    						((Player) sender).setCompassTarget(((Player) sender).getBedSpawnLocation());
		    					this.saveConfig();
		    					sender.sendMessage("Compass set to your bed");
	    					}
	    					else {
	    						sender.sendMessage("You don't have a bed :(");
	    					}
	    				}
	    				else if ((args[0].equalsIgnoreCase("RANDOM"))&&(((Player) sender).hasPermission("compassmodes.random"))){
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+sender.getName(),"RANDOM");
	    					((Player) sender).setCompassTarget(((Player) sender).getLocation());
	    					this.saveConfig();
	    					sender.sendMessage("Compass will now point towards a random location");
	    				}
	    				else if ((args[0].equalsIgnoreCase("EAST"))&&(((Player) sender).hasPermission("compassmodes.location"))){
	    					Location myloc = new Location(((Player) sender).getWorld(),-Double.MAX_VALUE,64,0);
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+sender.getName(),-Double.MAX_VALUE+",0");
	    					((Player) sender).setCompassTarget(myloc);
	    					this.saveConfig();
	    					sender.sendMessage("Compass will now point east");
	    				}
	    				else if ((args[0].equalsIgnoreCase("WEST"))&&(((Player) sender).hasPermission("compassmodes.location"))){
	    					Location myloc = new Location(((Player) sender).getWorld(),Double.MAX_VALUE,64,0);
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+sender.getName(),Double.MIN_VALUE+",0");
	    					((Player) sender).setCompassTarget(myloc);
	    					this.saveConfig();
	    					sender.sendMessage("Compass will now point west");
	    				}
	    				else if ((args[0].equalsIgnoreCase("SOUTH"))&&(((Player) sender).hasPermission("compassmodes.location"))){
	    					Location myloc = new Location(((Player) sender).getWorld(),0,64,-Double.MAX_VALUE);
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+sender.getName(),"0,"+(-Double.MAX_VALUE));
	    					((Player) sender).setCompassTarget(myloc);
	    					this.saveConfig();
	    					sender.sendMessage("Compass will now point south");
	    				}
	    				else if ((args[0].equalsIgnoreCase("NORTH"))&&(((Player) sender).hasPermission("compassmodes.location"))){ //TODO
	    					Location myloc = new Location(((Player) sender).getWorld(),0,64,Double.MIN_VALUE);
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+sender.getName(),"0,"+(Double.MAX_VALUE));
	    					((Player) sender).setCompassTarget(myloc);
	    					this.saveConfig();
	    					sender.sendMessage("Compass will now point north");
	    				}
	    				else if ((args[0].equalsIgnoreCase("DEATH"))&&(((Player) sender).hasPermission("compassmodes.deathpoint"))){
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+sender.getName(),"death");
	    					try {
	    						String[] last =  deathpoints.get(sender.getName()).split(",");
	    						Location myloc = new Location(Bukkit.getWorld(last[2]),Double.valueOf(last[0]).intValue(),64,Double.valueOf(last[1]).intValue());
	    						if (Bukkit.getWorld(last[2]) == ((Player) sender).getWorld()) {
	    							((Player) sender).setCompassTarget(myloc);
	    							this.getConfig().set("Players."+sender.getName(),"DEATH");
	    						}
	    						else {
	    							sender.sendMessage("You did not die in this map.");
	    						}
	    					}
	    					catch (Exception e) {
	    						sender.sendMessage("You have not died recently.");
	    					}
	    					this.saveConfig();
	    					
	    				}
	    				else if (((StringUtils.countMatches(String.valueOf(args[0]), ",") == 1))&&(((Player) sender).hasPermission("compassmodes.location"))) {
	    					try {
	    						this.reloadConfig();
	    						String[] parts = args[0].split(",");
	    						Location myloc = new Location(((Player) sender).getWorld(),Double.valueOf(parts[0]).intValue(),64,Double.valueOf(parts[1]).intValue());
	    						
	    						if (this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".override").equalsIgnoreCase("true")) {
		    						sender.sendMessage("Your preference is currently being overridden");
		    					}
		    					else {
		    						((Player) sender).setCompassTarget(myloc);
		    					}
	    						sender.sendMessage("Compass set to "+args[0]);
	    						this.getConfig().set("Players."+sender.getName(),args[0]);
	    						this.saveConfig();
	    					}
	    					catch (Exception e) {
	    						sender.sendMessage("Invalid syntax, please use /compass X,Z");
	    			        }
	    				}
	    				else if (((Bukkit.getPlayer(args[0])!=null))&&(((Player) sender).hasPermission("compassmodes.player"))){
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+((Player) sender).getName(),args[0]);
	    					this.saveConfig();
	    					sender.sendMessage("You are now tracking " + args[0]);
    						if (this.getConfig().getString("multiworld."+((Player) sender).getWorld().getName()+".override").equalsIgnoreCase("true")) {
	    						sender.sendMessage("Your preference is currently being overridden");
	    					}
	    				}
	    				else {
	    					sender.sendMessage("The mode you entered doesn't exist or is denied: "+ args[0]+". try /compass help");
	    				}
	    				
	    			}
	    			else {
	    				sender.sendMessage("Sorry, you do not have an inventory.");
	    			}
	    			this.saveConfig();
	    		}
	    			else {
    				
	    			if (((Player) sender).hasPermission("compassmodes.other")) {
	    			if (Bukkit.getPlayer(args[1])!=null) {
	    				Player player = Bukkit.getPlayer(args[1]);
	    				if (args[0].equalsIgnoreCase("DEFAULT")) {
	    					player.setCompassTarget(player.getWorld().getSpawnLocation());
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+args[1],"DEFAULT");
	    					this.saveConfig();
	    					sender.sendMessage("Compass set to DEFAULT for "+args[1]);
	    				}
	    				else if (args[0].equalsIgnoreCase("CURRENT")) {
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+args[1],((Player) sender).getLocation().getX()+","+((Player) sender).getLocation().getZ());
	    					this.saveConfig();
	    					Location myloc = new Location(((Player) sender).getWorld(),((Player) sender).getLocation().getX(),64,((Player) sender).getLocation().getZ());
	    					player.setCompassTarget(myloc);
	    					sender.sendMessage("Compass set to "+((Player) sender).getLocation().getX()+", "+((Player) sender).getLocation().getZ()+" for "+args[1]);
	    				}
	    				else if (args[0].equalsIgnoreCase("RANDOM")) {
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+args[1],"RANDOM");
	    					this.saveConfig();
	    					sender.sendMessage("Compass set to RANDOM for "+args[1]);
	    				}
	    				else if (args[0].equalsIgnoreCase("DEATH")) {
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+args[1],"DEATH");
	    					this.saveConfig();
	    					
	    					
	    	        		try {
	    	    				String[] last =  deathpoints.get(player.getName()).split(",");
	    	    				Location myloc = new Location(Bukkit.getWorld(last[2]),Double.valueOf(last[0]).intValue(),64,Double.valueOf(last[1]).intValue());
	    	    				if (Bukkit.getWorld(last[2]) == player.getWorld()) {
	    	    					((Player) player).setCompassTarget(myloc);
	    	    				}
	    	    				sender.sendMessage("Compass set to DEATH for "+args[1]);
	    	    			}
	    	    			catch (Exception e) {
	    	    				sender.sendMessage(args[1]+" does not have a death point.");
	    	    			}
	    					
	    					
	    				}
	    				else if (args[0].equalsIgnoreCase("NEAR")) {
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+args[1],"NEAR");
	    					this.saveConfig();
	    					sender.sendMessage("Compass set to NEAR for "+args[1]);
	    				}
	    				else if ((StringUtils.countMatches(String.valueOf(args[0]), ",") == 1)) {
	    					try {
	    						this.reloadConfig();
	    						String[] parts = args[0].split(",");
	    						Location myloc = new Location(((Player) sender).getWorld(),Double.valueOf(parts[0]).intValue(),64,Double.valueOf(parts[1]).intValue());
	    						player.setCompassTarget(myloc);
	    						this.getConfig().set("Players."+args[1],args[0]);
	    						this.saveConfig();
	    						sender.sendMessage("Compass set to "+args[0]+ "for "+args[1]);
	    					}
	    					catch (Exception e) {
	    						sender.sendMessage("Invalid syntax, please use /compass X,Z <playername>");
	    			        }
	    				}
	    				else if (args[0].equalsIgnoreCase("BED")) {
	    					if (player.getBedSpawnLocation() != null) {
	    						this.getConfig().set("Players."+player.getName(),"BED");
	    						player.setCompassTarget(player.getBedSpawnLocation());
	    						this.saveConfig();
	    						sender.sendMessage("Compass set for "+args[1]);
	    					}
	    					else {
	    						sender.sendMessage("Player did not have a bed");
	    					}
	    				}
	    				else if (args[0].equalsIgnoreCase("NORTH")) {
	    					//TODO
	    					Location myloc = new Location(((Player) sender).getWorld(),0,64,Double.MIN_VALUE);
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+sender.getName(),"0,"+(Double.MAX_VALUE));
	    					((Player) sender).setCompassTarget(myloc);
	    					this.saveConfig();
	    					sender.sendMessage("Compass set for "+args[1]);
	    				}
	    				else if (args[0].equalsIgnoreCase("EAST")) {
	    					Location myloc = new Location(((Player) sender).getWorld(),-Double.MAX_VALUE,64,0);
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+sender.getName(),-Double.MAX_VALUE+",0");
	    					((Player) sender).setCompassTarget(myloc);
	    					this.saveConfig();
	    					sender.sendMessage("Compass set for "+args[1]);
	    				}
	    				else if (args[0].equalsIgnoreCase("SOUTH")) {
	    					Location myloc = new Location(((Player) sender).getWorld(),0,64,-Double.MAX_VALUE);
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+sender.getName(),"0,"+(-Double.MAX_VALUE));
	    					((Player) sender).setCompassTarget(myloc);
	    					this.saveConfig();
	    					sender.sendMessage("Compass set for "+args[1]);
	    				}
	    				else if (args[0].equalsIgnoreCase("WEST")) {
	    					Location myloc = new Location(((Player) sender).getWorld(),Double.MAX_VALUE,64,0);
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+sender.getName(),Double.MIN_VALUE+",0");
	    					((Player) sender).setCompassTarget(myloc);
	    					this.saveConfig();
	    					sender.sendMessage("Compass set for "+args[1]);
	    				}
	    				else if ((Bukkit.getPlayer(args[0])!=null)){
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+args[1],args[0]);
	    					this.saveConfig();
	    					sender.sendMessage(args[1] + " is now tracking " + args[0]);
	    				}
	    				else {
	    					sender.sendMessage("Unknown mode" + args[0]);
	    				}
	    			}
	    			else {
	    				sender.sendMessage("Cannot find PLAYER "+args[1]);
	    			}
	    			}
	    			else {
	    				sender.sendMessage("Too many parameters. For help use /compass help");
	    			}
    			}
    		}
    		else {
    			sender.sendMessage(ChatColor.GOLD+"Commands:");
    			sender.sendMessage(ChatColor.GREEN+" - /compass <mode> - sets your compass mode");
    			sender.sendMessage(ChatColor.GREEN+" - /compass <mode> <player> - sets a player's mode");
    			sender.sendMessage(ChatColor.GREEN+" - /compass list - a list of all the modes");
    		}
    	}
    	return false; 
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent evt) {
    	

    	
        Player player = evt.getPlayer(); // The player who joined

		if (this.getConfig().getString("multiworld."+player.getWorld().getName()+".override").equalsIgnoreCase("true")) {
			player.sendMessage("Your compass preference is currently being overridden");
		}
		else {
		
        //Location myloc = new Location(player.getWorld(),191,18,-1023);
        //player.setCompassTarget(player.getLocation());

        try {
        	String current = this.getConfig().getString("Players."+player.getName());
    		try {
			if (getConfig().getString("multiworld."+player.getWorld().getName()+".override").equalsIgnoreCase("true")) {
				current = getConfig().getString("multiworld."+player.getWorld().getName()+".mode");
			}
    		}
    		catch (Exception e) {
    			
    		}

        	if (((StringUtils.countMatches(String.valueOf(current), ",") == 1)&&(player.hasPermission("compassmodes.location")))) {
        		String[] parts = current.split(",");
        		Location myloc = new Location(player.getWorld(),Double.valueOf(parts[0]).intValue(),64,Double.valueOf(parts[1]).intValue());
        		player.setCompassTarget(myloc);
        	}
        	else if ((current.equalsIgnoreCase("DEATH"))&&(player.hasPermission("compassmodes.deathpoint"))) { 
        		try {
    				String[] last =  deathpoints.get(player.getName()).split(",");
    				Location myloc = new Location(Bukkit.getWorld(last[2]),Double.valueOf(last[0]).intValue(),64,Double.valueOf(last[1]).intValue());
    				if (Bukkit.getWorld(last[2]) == player.getWorld()) {
    					((Player) player).setCompassTarget(myloc);
    				}
    			}
    			catch (Exception e) {
    			}
        	}
        	else if ((current.equalsIgnoreCase("BED"))&&(player.hasPermission("compassmodes.bed"))) { 
				this.reloadConfig();
				if (player.getBedSpawnLocation() != null) {
					this.getConfig().set("Players."+player.getName(),"BED");
					player.setCompassTarget(player.getBedSpawnLocation());
					this.saveConfig();
				}       	
        	} 	
        }
        catch (Exception e) {
        	getLogger().info("ERROR "+e);
        	player.setCompassTarget(player.getWorld().getSpawnLocation());
        }
    }
}
 
    @Override
    public void onDisable() {
    	timer.cancel();
    	timer.purge();
    	this.reloadConfig();
    	this.saveConfig();
        // TODO Insert logic to be performed when the plugin is disabled
    }
         
    // Leave event
}
