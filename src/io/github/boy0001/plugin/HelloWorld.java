// TODO force default (currently it just rejects)

package io.github.boy0001.plugin;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInventoryEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.Location;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

public final class HelloWorld extends JavaPlugin implements Listener {
	Timer timer = new Timer ();
	
	
	
    public boolean checkperm(Player player,String perm) {
    	boolean hasperm = false;
    	String[] nodes = perm.split("\\.");
    	String n2 = "";
    	if (player==null) {
    		return true;
    	}
    	else if (player.hasPermission(perm)) {
    		hasperm = true;
    	}
    	else if (player.isOp()==true) {
    		hasperm = true;
    	}
    	else {
    		for(int i = 0; i < nodes.length-1; i++) {
    			n2+=nodes[i]+".";
            	if (player.hasPermission(n2+"*")) {
            		hasperm = true;
            	}
    		}
    	}
		return hasperm;
    }
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
				else if (getConfig().contains("Players."+player.getName()+"."+player.getWorld().getName())) {
					mymode = "false";
				}
				else {
					mymode = getConfig().getString("multiworld."+player.getWorld().getName()+".mode");
				}
	    		}
	    		catch (Exception e) {
	    			mymode = "false";
	    		}
	    		if ( ((getConfig().getString("Players."+player.getName()+"."+player.getWorld().getName()).equalsIgnoreCase("NEAR"))&&(checkperm(player,"compassmodes.near")) && (mymode == "false")) || (mymode.equalsIgnoreCase("NEAR"))){
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
	    		else if ( ((getConfig().getString("Players."+player.getName()+"."+player.getWorld().getName()).equalsIgnoreCase("RANDOM"))&&(checkperm(player,"compassmodes.random")) && (mymode == "false")) || (mymode.equalsIgnoreCase("RANDOM"))) {
	    			Random rand1 = new Random();
	    			int random1 = rand1.nextInt(500)-250;
	    			int random2 = rand1.nextInt(500)-250;
	    			Location myloc = new Location(player.getWorld(),player.getLocation().getX()+random1,64,player.getLocation().getZ()+random2);
	    			player.setCompassTarget(myloc);
	    		}
	    		else if (((Bukkit.getPlayer(getConfig().getString("Players."+player.getName()+"."+player.getWorld().getName())) != null)&&(checkperm(player,"compassmodes.player"))) || (mymode.equalsIgnoreCase(getConfig().getString("Players."+player.getName()+"."+player.getWorld().getName())))) {
	    			if (Bukkit.getPlayer(getConfig().getString("Players."+player.getName()+"."+player.getWorld().getName())).getWorld() == player.getWorld()) {
	    				double dist = player.getLocation().distance(Bukkit.getPlayer(getConfig().getString("Players."+player.getName()+"."+player.getWorld().getName())).getLocation());
	    				if ((getConfig().getString("range")=="0")||(dist < Integer.parseInt(getConfig().getString("range")))) {
	    					player.setCompassTarget(Bukkit.getPlayer(getConfig().getString("Players."+player.getName()+"."+player.getWorld().getName())).getLocation());
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
        final Player player = (Player) event.getEntity();
        Location loc = player.getLocation();
        respawnitems.remove(player.getName());
        deathpoints.put(player.getName(),loc.getX()+","+loc.getZ()+","+player.getWorld().getName());
        if (getConfig().getBoolean("keep-existing-compass")) {
        	List<ItemStack> mydrops = event.getDrops();
        	for (int i = 0; i < mydrops.size(); i++) {
        		if (mydrops.get(i).getTypeId()==345) {
        			respawnitems.put(player.getName(), mydrops.get(i));
        			mydrops.set(i,null);
        			return;
        		}
        	}
        }
        //save location
	}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		final Player player = event.getPlayer();

		if (respawnitems.get(player.getName()) != null) {
			if (getConfig().getString("force-compass-slot").equalsIgnoreCase("false")) {
				player.getInventory().addItem(new ItemStack(Material.COMPASS, 1));
			}
			else {
				player.getInventory().setItem(getConfig().getInt("force-compass-slot"), (ItemStack) respawnitems.get(player.getName()));
			}
			respawnitems.remove(player.getName());
		}
		if (getConfig().getBoolean("give-compass-on-respawn")) {
			if (player.getInventory().contains(345)==false) {
				if (getConfig().getString("force-compass-slot").equalsIgnoreCase("false")) {
					player.getInventory().addItem(new ItemStack(Material.COMPASS, 1));
				}
				else {
					player.getInventory().setItem(getConfig().getInt("force-compass-slot"), new ItemStack(Material.COMPASS, 1));
				}
			}
		}
		
		final Player myplayer = event.getPlayer();
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(this, new Runnable() {

			@Override
			public void run() {
				update(myplayer);				
			}
        }, 20L);
	}
	HashMap<String, String> deathpoints = new HashMap<String, String>();
	HashMap<String, Object> respawnitems = new HashMap<String, Object>();
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event){
		Player player = event.getPlayer();
		PlayerInventory inventory = player.getInventory();
		if (getConfig().getString("force-compass-slot").equalsIgnoreCase("false")==false) {
			if (event.getItemDrop().getItemStack().getTypeId()==345) {
				if(inventory.getHeldItemSlot() == getConfig().getInt("force-compass-slot")) {
					event.setCancelled(true);
					}
			}
		}
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event){
		try {
		ItemStack item = event.getCurrentItem();
		if (item.getTypeId()==345) {
			if (getConfig().getString("force-compass-slot").equalsIgnoreCase("false")==false) {
				if (event.getSlotType().equals(InventoryType.SlotType.QUICKBAR)) {
					if (event.getSlot()==getConfig().getInt("force-compass-slot")) {
						event.setCancelled(true);
					}
				}
			}
		}
		}
		catch (Exception e) {
			
		}
	}
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		final Player myplayer = event.getPlayer();
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(this, new Runnable() {

			@Override
			public void run() {
				update(myplayer);				
			}
        }, 20L);
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		try {
		Player player = event.getPlayer();
		if (getConfig().getString("force-compass-slot").equalsIgnoreCase("false")==false) {
			int index = (getConfig().getInt("force-compass-slot")-1);
			ItemStack item = player.getInventory().getItem(index);
			if (item==null) {
				player.getInventory().setItem(getConfig().getInt("force-compass-slot"), new ItemStack(Material.COMPASS));
			}
			else if (item.getType().equals(Material.COMPASS)==false) {
				player.getInventory().setItem(getConfig().getInt("force-compass-slot"), new ItemStack(Material.COMPASS));
			}
		}
		}
		catch (Exception e) {
			
		}
		
	}
	
	
	@Override
    public void onEnable(){	    
	    
	    
		getConfig().options().copyDefaults(true);
        final Map<String, Object> options = new HashMap<String, Object>();
        
        getConfig().set("version", "0.2.5");
        options.put("range", "512");
        options.put("keep-existing-compass", false);
        options.put("give-compass-on-respawn", false);
        options.put("force-compass-slot", false);
        //TODO give compass on death
        //TODO give compass only if they die with compass
        //TODO prevent dying with a compass
        options.put("Players.Notch","RANDOM");
        for(World world : getServer().getWorlds()) {
        	options.put("multiworld."+world.getName()+".mode","DEFAULT");
            options.put("multiworld."+world.getName()+".override",false);
        }
        
        
        for (final Entry<String, Object> node : options.entrySet()) {
        	 if (!getConfig().contains(node.getKey())) {
        		 getConfig().set(node.getKey(), node.getValue());
        	 }
        }
        saveConfig();
        
		
		
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
    public void msg(Player player,String mystring) {
    	if (mystring==null||mystring.equals("")) {
    		return;
    	}
    	if (player==null) {
    		getServer().getConsoleSender().sendMessage(colorise(mystring));
    	}
    	else if (player instanceof Player==false) {
    		getServer().getConsoleSender().sendMessage(colorise(mystring));
    	}
    	else {
    		player.sendMessage(colorise(mystring));
    	}

    }
    public String colorise(String mystring) {
    	String[] codes = {"&1","&2","&3","&4","&5","&6","&7","&8","&9","&0","&a","&b","&c","&d","&e","&f","&r","&l","&m","&n","&o","&k"};
    	for (String code:codes) {
    		mystring = mystring.replace(code, "§"+code.charAt(1));
    	}
    	return mystring;
    }
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
    	Player player;
    	if (sender instanceof Player) {
			player = (Player) sender;
    	}
    	else {
    		player = null;
    	}
    	if(cmd.getName().equalsIgnoreCase("compasstest")){
    		msg(player,"This is a totally useless test command");
    	}
    	else if (cmd.getName().equalsIgnoreCase("compass")) {
    		if (args.length > 0){
	    		if (args[0].equalsIgnoreCase("list")){
	    			if (player!=null) {
	    			msg(player,ChatColor.GOLD+"Modes:");
	    			String mycolor;
	    			String mode = "near";
	    			try {
	    					if ((this.getConfig().getString("multiworld."+player.getWorld().getName()+".override").equalsIgnoreCase("true")) && (this.getConfig().getString("multiworld."+player.getWorld().getName()+".mode").equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if ((this.getConfig().getString("multiworld."+player.getWorld().getName()+".override").equalsIgnoreCase("true"))==false) { if ((getConfig().getString("Players."+player.getName()+"."+player.getWorld().getName()).equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if (checkperm(player,"compassmodes."+mode)) { mycolor = ChatColor.GREEN + "";}else {mycolor = ChatColor.RED + "";}}else { mycolor = ChatColor.GRAY + "";}	
	    			}
	    			catch (Exception e) {
	    				if ((getConfig().getString("Players."+player.getName()+"."+player.getWorld().getName()).equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";} else if (checkperm(player,"compassmodes."+mode)) { mycolor = ChatColor.GREEN + "";} else { mycolor = ""+ChatColor.RED; }
	    			}
	    			msg(player,mycolor + " - Near");
	    			
	    			mode = "random";
	    			try {
    					if ((this.getConfig().getString("multiworld."+player.getWorld().getName()+".override").equalsIgnoreCase("true")) && (this.getConfig().getString("multiworld."+player.getWorld().getName()+".mode").equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if ((this.getConfig().getString("multiworld."+player.getWorld().getName()+".override").equalsIgnoreCase("true"))==false) { if ((getConfig().getString("Players."+player.getName()+"."+player.getWorld().getName()).equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if (checkperm(player,"compassmodes."+mode)) { mycolor = ChatColor.GREEN + "";}else {mycolor = ChatColor.RED + "";}}else { mycolor = ChatColor.GRAY + "";}	
	    			}
	    			catch (Exception e) {
	    				if ((getConfig().getString("Players."+player.getName()+"."+player.getWorld().getName()).equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";} else if (checkperm(player,"compassmodes."+mode)) { mycolor = ChatColor.GREEN + "";} else { mycolor = ""+ChatColor.RED; }
	    			}
	    			msg(player,mycolor + " - Random");

	    			mode = "current";
	    			try {
	    			if ((this.getConfig().getString("multiworld."+player.getWorld().getName()+".override").equalsIgnoreCase("true")) && (this.getConfig().getString("multiworld."+player.getWorld().getName()+".mode").equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if ((this.getConfig().getString("multiworld."+player.getWorld().getName()+".override").equalsIgnoreCase("true"))==false) { if ((getConfig().getString("Players."+player.getName()+"."+player.getWorld().getName()).equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if (checkperm(player,"compassmodes."+mode)) { mycolor = ChatColor.GREEN + "";}else {mycolor = ChatColor.RED + "";}}else { mycolor = ChatColor.GRAY + "";}
	    			}
	    			catch (Exception e) {
	    				if (checkperm(player,"compassmodes."+mode)) { mycolor = ChatColor.GREEN + "";} else { mycolor = ""+ChatColor.RED; }
	    			}
	    			msg(player,mycolor + " - Current");
	    			
	    			mode = "location";
	    			try {
	    			if ((this.getConfig().getString("multiworld."+player.getWorld().getName()+".override").equalsIgnoreCase("true")) && (this.getConfig().getString("multiworld."+player.getWorld().getName()+".mode").equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if ((this.getConfig().getString("multiworld."+player.getWorld().getName()+".override").equalsIgnoreCase("true"))==false) { if ((StringUtils.countMatches(String.valueOf(this.getConfig().getString("Players."+player.getName()+"."+player.getWorld().getName())), ",") == 1)) {mycolor = ChatColor.BLUE + "";}else if (checkperm(player,"compassmodes."+mode)) { mycolor = ChatColor.GREEN + "";}else {mycolor = ChatColor.RED + "";}}else { mycolor = ChatColor.GRAY + "";}
	    			}
	    			catch (Exception e) {
	    				if ((StringUtils.countMatches(String.valueOf(this.getConfig().getString("Players."+player.getName()+"."+player.getWorld().getName())), ",") == 1)) {mycolor = ChatColor.BLUE + "";} else if (checkperm(player,"compassmodes."+mode)) { mycolor = ChatColor.GREEN + "";} else { mycolor = ""+ChatColor.RED; }
	    			}
	    			msg(player,mycolor + " - X,Y");
	    			
	    			mode = "player";
	    			try {
	    			if ((this.getConfig().getString("multiworld."+player.getWorld().getName()+".override").equalsIgnoreCase("true")) && (this.getConfig().getString("multiworld."+player.getWorld().getName()+".mode").equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if ((this.getConfig().getString("multiworld."+player.getWorld().getName()+".override").equalsIgnoreCase("true"))==false) { if ((Bukkit.getPlayer(this.getConfig().getString("Players."+player.getName()+"."+player.getWorld().getName()))!=null)) {mycolor = ChatColor.BLUE + "";}else if (checkperm(player,"compassmodes."+mode)) { mycolor = ChatColor.GREEN + "";}else {mycolor = ChatColor.RED + "";}}else { mycolor = ChatColor.GRAY + "";}
	    			}
	    			catch (Exception e) {
	    				try {
	    				if ((Bukkit.getPlayer(this.getConfig().getString("Players."+player.getName()+"."+player.getWorld().getName()))!=null)) {mycolor = ChatColor.BLUE + "";} else if (checkperm(player,"compassmodes."+mode)) { mycolor = ChatColor.GREEN + "";} else { mycolor = ""+ChatColor.RED; }
	    				}
	    				catch (Exception e1) {
	    					mycolor = "" + ChatColor.GRAY;
	    				}
    				}
	    			msg(player,mycolor + " - <Player>");
	    			try {
	    			mode = "default";
	    			if ((this.getConfig().getString("multiworld."+player.getWorld().getName()+".override").equalsIgnoreCase("true")) && (this.getConfig().getString("multiworld."+player.getWorld().getName()+".mode").equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if ((this.getConfig().getString("multiworld."+player.getWorld().getName()+".override").equalsIgnoreCase("true"))==false) { if ((getConfig().getString("Players."+player.getName()+"."+player.getWorld().getName()).equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if (checkperm(player,"compassmodes."+mode)) { mycolor = ChatColor.GREEN + "";}else {mycolor = ChatColor.RED + "";}}else { mycolor = ChatColor.GRAY + "";}
	    			}
	    			catch (Exception e) {
	    				if (checkperm(player,"compassmodes."+mode)) { mycolor = ChatColor.GREEN + "";} else { mycolor = ""+ChatColor.RED; }
	    			}
	    			msg(player,mycolor + " - Default");
	    			
	    			mode = "bed";
	    			try {
	    			if ((this.getConfig().getString("multiworld."+player.getWorld().getName()+".override").equalsIgnoreCase("true")) && (this.getConfig().getString("multiworld."+player.getWorld().getName()+".mode").equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if ((this.getConfig().getString("multiworld."+player.getWorld().getName()+".override").equalsIgnoreCase("true"))==false) { if ((getConfig().getString("Players."+player.getName()+"."+player.getWorld().getName()).equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if (checkperm(player,"compassmodes."+mode)) { mycolor = ChatColor.GREEN + "";}else {mycolor = ChatColor.RED + "";}}else { mycolor = ChatColor.GRAY + "";}
	    			}
	    			catch (Exception e) {
	    				if (checkperm(player,"compassmodes."+mode)) { mycolor = ChatColor.GREEN + "";} else { mycolor = ""+ChatColor.RED; }
	    			}
	    			msg(player,mycolor + " - Bed");
	    			
	    			mode = "death";
	    			try {
	    			if ((this.getConfig().getString("multiworld."+player.getWorld().getName()+".override").equalsIgnoreCase("true")) && (this.getConfig().getString("multiworld."+player.getWorld().getName()+".mode").equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if ((this.getConfig().getString("multiworld."+player.getWorld().getName()+".override").equalsIgnoreCase("true"))==false) { if ((getConfig().getString("Players."+player.getName()+"."+player.getWorld().getName()).equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if (checkperm(player,"compassmodes.deathpoint")) { mycolor = ChatColor.GREEN + "";}else {mycolor = ChatColor.RED + "";}}else { mycolor = ChatColor.GRAY + "";}
	    			}
	    			catch (Exception e) {
	    				if (checkperm(player,"compassmodes.deathpoint")) { mycolor = ChatColor.GREEN + "";} else { mycolor = ""+ChatColor.RED; }
	    			}
	    			msg(player,mycolor + " - Death");
	    			
	    			mode = "north";
	    			try {
	    			if ((this.getConfig().getString("multiworld."+player.getWorld().getName()+".override").equalsIgnoreCase("true")) && (this.getConfig().getString("multiworld."+player.getWorld().getName()+".mode").equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if ((this.getConfig().getString("multiworld."+player.getWorld().getName()+".override").equalsIgnoreCase("true"))==false) { if ((getConfig().getString("Players."+player.getName()+"."+player.getWorld().getName()).equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if (checkperm(player,"compassmodes.location")) { mycolor = ChatColor.GREEN + "";}else {mycolor = ChatColor.RED + "";}}else { mycolor = ChatColor.GRAY + "";}
	    			}
	    			catch (Exception e) {
	    				if (checkperm(player,"compassmodes.location")) { mycolor = ChatColor.GREEN + "";} else { mycolor = ""+ChatColor.RED; }
	    			}
	    			msg(player,mycolor + " - North");
	    			
	    			mode = "east";
	    			try {
	    			if ((this.getConfig().getString("multiworld."+player.getWorld().getName()+".override").equalsIgnoreCase("true")) && (this.getConfig().getString("multiworld."+player.getWorld().getName()+".mode").equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if ((this.getConfig().getString("multiworld."+player.getWorld().getName()+".override").equalsIgnoreCase("true"))==false) { if ((getConfig().getString("Players."+player.getName()+"."+player.getWorld().getName()).equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if (checkperm(player,"compassmodes.location")) { mycolor = ChatColor.GREEN + "";}else {mycolor = ChatColor.RED + "";}}else { mycolor = ChatColor.GRAY + "";}
	    			}
	    			catch (Exception e) {
	    				if (checkperm(player,"compassmodes.location")) { mycolor = ChatColor.GREEN + "";} else { mycolor = ""+ChatColor.RED; }
	    			}
	    			msg(player,mycolor + " - East");
	    			
	    			mode = "south";
	    			try {
	    			if ((this.getConfig().getString("multiworld."+player.getWorld().getName()+".override").equalsIgnoreCase("true")) && (this.getConfig().getString("multiworld."+player.getWorld().getName()+".mode").equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if ((this.getConfig().getString("multiworld."+player.getWorld().getName()+".override").equalsIgnoreCase("true"))==false) { if ((getConfig().getString("Players."+player.getName()+"."+player.getWorld().getName()).equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if (checkperm(player,"compassmodes.location")) { mycolor = ChatColor.GREEN + "";}else {mycolor = ChatColor.RED + "";}}else { mycolor = ChatColor.GRAY + "";}
	    			}
	    			catch (Exception e) {
	    				if (checkperm(player,"compassmodes.location")) { mycolor = ChatColor.GREEN + "";} else { mycolor = ""+ChatColor.RED; }
	    			}
	    			msg(player,mycolor + " - South");
	    			
	    			mode = "west";
	    			try {
	    			if ((this.getConfig().getString("multiworld."+player.getWorld().getName()+".override").equalsIgnoreCase("true")) && (this.getConfig().getString("multiworld."+player.getWorld().getName()+".mode").equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if ((this.getConfig().getString("multiworld."+player.getWorld().getName()+".override").equalsIgnoreCase("true"))==false) { if ((getConfig().getString("Players."+player.getName()+"."+player.getWorld().getName()).equalsIgnoreCase(mode))) {mycolor = ChatColor.BLUE + "";}else if (checkperm(player,"compassmodes.location")) { mycolor = ChatColor.GREEN + "";}else {mycolor = ChatColor.RED + "";}}else { mycolor = ChatColor.GRAY + "";}
	    			}
	    			catch (Exception e) {
	    				if (checkperm(player,"compassmodes.location")) { mycolor = ChatColor.GREEN + "";} else { mycolor = ""+ChatColor.RED; }
	    			}
	    			msg(player,mycolor + " - West");
	    			msg(player,"&f - None");
	    			
//	    			compassmodes.near 
//	    			compassmodes.random
//	    			compassmodes.current
//	    			compassmodes.location
//	    			compassmodes.default
//	    			compassmodes.other
	    		}
	    			else {
	    				System.out.println("Sorry, you must be a player to perform this action.");
	    			}
	    			
	    		}
	    		else if (args[0].equalsIgnoreCase("help")){
	    			msg(player,ChatColor.GOLD+"Commands:");
	    			msg(player,ChatColor.GREEN+" - /compass <mode> - sets your compass mode");
	    			msg(player,ChatColor.GREEN+" - /compass <mode> <player> - sets a player's mode");
	    			msg(player,ChatColor.GREEN+" - /compass list - a list of all the modes");
	    			msg(player,ChatColor.GREEN+" - /compass reload - reloads the config file");
	    			msg(player,ChatColor.GREEN+" - /compass help - shows this page");
	    		}
	    		else if ((args[0].equalsIgnoreCase("reload"))){
	    			if (player!=null) {
	    				if (checkperm(player,"compassmodes.reload")) {
	    					for (Player user:Bukkit.getOnlinePlayers()) {
	    						update(user);
	    					}
	    	    			this.reloadConfig();
	    	    			this.saveDefaultConfig();
	    					msg(player,ChatColor.GRAY + "Successfully reloaded " + ChatColor.RED + "CompassModes"+ChatColor.WHITE + ".");
	    				}
	    				else {
	    					msg(player,ChatColor.RED + "Sorry, you do not have permission to perform this action.");
	    				}
	    			}
	    			else {
		    			this.reloadConfig();
		    			this.saveDefaultConfig();
	    				System.out.println("Successfully reloaded CompassModes");
	    			}
	    			// RELOAD CONFIG
	    		}
	    		else if (args.length == 1){
	    			if (sender instanceof Player) {
	    				if ((args[0].equalsIgnoreCase("CURRENT"))&&(checkperm(player,"compassmodes.current"))){
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+sender.getName()+"."+((Player) sender).getWorld().getName(),player.getLocation().getX()+","+player.getLocation().getZ());
	    					if (this.getConfig().getString("multiworld."+player.getWorld().getName()+".override").equalsIgnoreCase("true")) {
	    						msg(player,"Your preference is currently being overridden");
	    					}
	    					else {
	    						player.setCompassTarget(player.getLocation());
	    						msg(player,"Compass set to current location");
	    					}
	    					this.saveConfig();
	    					
	    				}
	    				else if ((args[0].equalsIgnoreCase("NEAR"))&&(checkperm(player,"compassmodes.near"))){
	    					this.reloadConfig();
	    	        		double last = 0;
	    	        		Player sp = null;
	    	        		
	    	        		for(Player all:getServer().getOnlinePlayers()){
	    						if ((all.getLocation().getWorld() == player.getLocation().getWorld()) && (all.getName() != player.getName())) {
	    							double dist = player.getLocation().distance(all.getLocation());
	    							if (((sp == null) || (dist < last))&&((dist < Integer.parseInt(getConfig().getString("range")))||(getConfig().getString("range") == "0"))) {
	    								sp = all;
	    								last = dist;
	    							}
	    						}
	    	        		}
	    					if (sp != null) {
		    					if (this.getConfig().getString("multiworld."+player.getWorld().getName()+".override").equalsIgnoreCase("true")) {
		    						msg(player,"Your preference is currently being overridden");
		    					}
		    					else {
		    						player.setCompassTarget(sp.getLocation());
		    						msg(player,"Currently tracking "+sp.getName());
		    					}
	    						this.getConfig().set("Players."+sender.getName()+"."+((Player) sender).getWorld().getName(),"NEAR");
	    						this.saveConfig();
	    					}
	    					
	    					else {
	    						msg(player,"[Error] There are no players nearby");
	    					}
	    					
	    				}
	    				else if ((args[0].equalsIgnoreCase("DEFAULT"))&&(checkperm(player,"compassmodes.default"))){
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+sender.getName()+"."+((Player) sender).getWorld().getName(),"DEFAULT");
	    					player.setCompassTarget(player.getWorld().getSpawnLocation());
	    					this.saveConfig();
	    					msg(player,"Compass set to spawnpoint");
	    				
	    				}
	    				else if ((args[0].equalsIgnoreCase("NONE"))){
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+sender.getName()+"."+((Player) sender).getWorld().getName(),null);
	    					msg(player,"Compass preference cleared");
	    					this.saveConfig();
	    					update((Player) sender);
	    				}
	    				else if ((args[0].equalsIgnoreCase("BED"))&&(checkperm(player,"compassmodes.bed"))){
	    					this.reloadConfig();
	    					if (player.getBedSpawnLocation() != null) {
	    						this.getConfig().set("Players."+sender.getName()+"."+((Player) sender).getWorld().getName(),"BED");
	    						player.setCompassTarget(player.getBedSpawnLocation());
		    					this.saveConfig();
		    					msg(player,"Compass set to your bed");
	    					}
	    					else {
	    						msg(player,"You don't have a bed :(");
	    					}
	    				}
	    				else if ((args[0].equalsIgnoreCase("RANDOM"))&&(checkperm(player,"compassmodes.random"))){
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+sender.getName()+"."+((Player) sender).getWorld().getName(),"RANDOM");
	    					player.setCompassTarget(player.getLocation());
	    					this.saveConfig();
	    					msg(player,"Compass will now point towards a random location");
	    				}
	    				else if ((args[0].equalsIgnoreCase("EAST"))&&(checkperm(player,"compassmodes.location"))){
	    					Location myloc = new Location(player.getWorld(),-Double.MAX_VALUE,64,0);
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+sender.getName()+"."+((Player) sender).getWorld().getName(),-Double.MAX_VALUE+",0");
	    					player.setCompassTarget(myloc);
	    					this.saveConfig();
	    					msg(player,"Compass will now point east");
	    				}
	    				else if ((args[0].equalsIgnoreCase("WEST"))&&(checkperm(player,"compassmodes.location"))){
	    					Location myloc = new Location(player.getWorld(),Double.MAX_VALUE,64,0);
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+sender.getName()+"."+((Player) sender).getWorld().getName(),Double.MAX_VALUE+",0");
	    					player.setCompassTarget(myloc);
	    					this.saveConfig();
	    					msg(player,"Compass will now point west");
	    				}
	    				else if ((args[0].equalsIgnoreCase("SOUTH"))&&(checkperm(player,"compassmodes.location"))){
	    					Location myloc = new Location(player.getWorld(),0,64,-Double.MAX_VALUE);
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+sender.getName()+"."+((Player) sender).getWorld().getName(),"0,"+(-Double.MAX_VALUE));
	    					player.setCompassTarget(myloc);
	    					this.saveConfig();
	    					msg(player,"Compass will now point south");
	    				}
	    				else if ((args[0].equalsIgnoreCase("NORTH"))&&(checkperm(player,"compassmodes.location"))){ //TODO
	    					Location myloc = new Location(player.getWorld(),0,64,Double.MIN_VALUE);
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+sender.getName()+"."+((Player) sender).getWorld().getName(),"0,"+(Double.MAX_VALUE));
	    					player.setCompassTarget(myloc);
	    					this.saveConfig();
	    					msg(player,"Compass will now point north");
	    				}
	    				else if ((args[0].equalsIgnoreCase("DEATH"))&&(checkperm(player,"compassmodes.deathpoint"))){
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+sender.getName()+"."+((Player) sender).getWorld().getName(),"death");
	    					try {
	    						String[] last =  deathpoints.get(sender.getName()).split(",");
	    						Location myloc = new Location(Bukkit.getWorld(last[2]),Double.valueOf(last[0]).intValue(),64,Double.valueOf(last[1]).intValue());
	    						if (Bukkit.getWorld(last[2]) == player.getWorld()) {
	    							player.setCompassTarget(myloc);
	    							this.getConfig().set("Players."+sender.getName()+"."+((Player) sender).getWorld().getName(),"DEATH");
	    							msg(player,"Compass set to your death point");
	    						}
	    						else {
	    							msg(player,"You did not die in this map.");
	    						}
	    					}
	    					catch (Exception e) {
	    						msg(player,"You have not died recently.");
	    					}
	    					this.saveConfig();
	    					
	    				}
	    				else if (((StringUtils.countMatches(String.valueOf(args[0]), ",") == 1))&&(checkperm(player,"compassmodes.location"))) {
	    					try {
	    						this.reloadConfig();
	    						String[] parts = args[0].split(",");
	    						Location myloc = new Location(player.getWorld(),Double.valueOf(parts[0]).intValue(),64,Double.valueOf(parts[1]).intValue());
	    						
	    						if (this.getConfig().getString("multiworld."+player.getWorld().getName()+".override").equalsIgnoreCase("true")) {
		    						msg(player,"Your preference is currently being overridden");
		    					}
		    					else {
		    						player.setCompassTarget(myloc);
		    					}
	    						msg(player,"Compass set to "+args[0]);
	    						this.getConfig().set("Players."+sender.getName()+"."+((Player) sender).getWorld().getName(),args[0]);
	    						this.saveConfig();
	    					}
	    					catch (Exception e) {
	    						msg(player,"Invalid syntax, please use /compass X,Z");
	    			        }
	    				}
	    				else if (((Bukkit.getPlayer(args[0])!=null))&&(checkperm(player,"compassmodes.player"))){
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+player.getName()+"."+player.getWorld().getName(),args[0]);
	    					this.saveConfig();
	    					msg(player,"You are now tracking " + args[0]);
    						if (this.getConfig().getString("multiworld."+player.getWorld().getName()+".override").equalsIgnoreCase("true")) {
	    						msg(player,"Your preference is currently being overridden");
	    					}
	    				}
	    				else {
	    					msg(player,"The mode you entered doesn't exist or is denied: "+ args[0]+". try /compass help");
	    				}
	    				
	    			}
	    			else {
	    				msg(player,"Sorry, you do not have an inventory.");
	    			}
	    			this.saveConfig();
	    		}
	    			else {
	    			if (checkperm(player,"compassmodes.other")) {
	    			if (Bukkit.getPlayer(args[1])!=null) {
	    				Player user = Bukkit.getPlayer(args[1]);
	    				if (args[0].equalsIgnoreCase("DEFAULT")) {
	    					user.setCompassTarget(user.getWorld().getSpawnLocation());
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+args[1]+"."+user.getWorld().getName()+"."+user.getWorld().getName(),"DEFAULT");
	    					this.saveConfig();
	    					msg(player,"Compass set to DEFAULT for "+args[1]);
	    				}
	    				else if (args[0].equalsIgnoreCase("HERE")) {
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+args[1]+"."+user.getWorld().getName(),player.getLocation().getX()+","+player.getLocation().getZ());
	    					this.saveConfig();
	    					Location myloc = new Location(player.getWorld(),player.getLocation().getX(),64,player.getLocation().getZ());
	    					user.setCompassTarget(myloc);
	    					msg(player,"Compass set to "+player.getLocation().getX()+", "+player.getLocation().getZ()+" for "+args[1]);
	    				}
	    				else if (args[0].equalsIgnoreCase("CURRENT")) {
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+args[1]+"."+user.getWorld().getName(),user.getLocation().getX()+","+user.getLocation().getZ());
	    					this.saveConfig();
	    					Location myloc = new Location(user.getWorld(),user.getLocation().getX(),64,user.getLocation().getZ());
	    					user.setCompassTarget(myloc);
	    					msg(player,"Compass set to "+user.getLocation().getX()+", "+user.getLocation().getZ()+" for "+args[1]);
	    				}
	    				else if (args[0].equalsIgnoreCase("RANDOM")) {
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+args[1]+"."+user.getWorld().getName(),"RANDOM");
	    					this.saveConfig();
	    					msg(player,"Compass set to RANDOM for "+args[1]);
	    				}
	    				else if (args[0].equalsIgnoreCase("DEATH")) {
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+args[1]+"."+user.getWorld().getName(),"DEATH");
	    					this.saveConfig();
	    					
	    					
	    	        		try {
	    	    				String[] last =  deathpoints.get(user.getName()).split(",");
	    	    				Location myloc = new Location(Bukkit.getWorld(last[2]),Double.valueOf(last[0]).intValue(),64,Double.valueOf(last[1]).intValue());
	    	    				if (Bukkit.getWorld(last[2]) == user.getWorld()) {
	    	    					user.setCompassTarget(myloc);
	    	    				}
	    	    				msg(player,"Compass set to DEATH for "+args[1]);
	    	    			}
	    	    			catch (Exception e) {
	    	    				msg(player,args[1]+" does not have a death point.");
	    	    			}
	    					
	    					
	    				}
	    				else if (args[0].equalsIgnoreCase("NEAR")) {
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+args[1]+"."+user.getWorld().getName(),"NEAR");
	    					this.saveConfig();
	    					msg(player,"Compass set to NEAR for "+args[1]);
	    				}
	    				else if ((StringUtils.countMatches(String.valueOf(args[0]), ",") == 1)) {
	    					try {
	    						this.reloadConfig();
	    						String[] parts = args[0].split(",");
	    						Location myloc = new Location(user.getWorld(),Double.valueOf(parts[0]).intValue(),64,Double.valueOf(parts[1]).intValue());
	    						user.setCompassTarget(myloc);
	    						this.getConfig().set("Players."+args[1]+"."+user.getWorld().getName(),args[0]);
	    						this.saveConfig();
	    						msg(player,"Compass set to "+args[0]+ "for "+args[1]);
	    					}
	    					catch (Exception e) {
	    						msg(player,"Invalid syntax, please use /compass X,Z <playername>");
	    			        }
	    				}
	    				else if (args[0].equalsIgnoreCase("BED")) {
	    					if (user.getBedSpawnLocation() != null) {
	    						this.getConfig().set("Players."+user.getName(),"BED");
	    						user.setCompassTarget(user.getBedSpawnLocation());
	    						this.saveConfig();
	    						msg(player,"Compass set for "+args[1]);
	    					}
	    					else {
	    						msg(player,"Player did not have a bed");
	    					}
	    				}
	    				else if (args[0].equalsIgnoreCase("NORTH")) {
	    					//TODO
	    					Location myloc = new Location(user.getWorld(),0,64,Double.MIN_VALUE);
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+user.getName(),"0,"+(Double.MAX_VALUE));
	    					user.setCompassTarget(myloc);
	    					this.saveConfig();
	    					msg(player,"Compass set for "+args[1]);
	    				}
	    				else if (args[0].equalsIgnoreCase("EAST")) {
	    					Location myloc = new Location(user.getWorld(),-Double.MAX_VALUE,64,0);
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+user.getName(),-Double.MAX_VALUE+",0");
	    					user.setCompassTarget(myloc);
	    					this.saveConfig();
	    					msg(player,"Compass set for "+args[1]);
	    				}
	    				else if (args[0].equalsIgnoreCase("SOUTH")) {
	    					Location myloc = new Location(user.getWorld(),0,64,-Double.MAX_VALUE);
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+user.getName(),"0,"+(-Double.MAX_VALUE));
	    					user.setCompassTarget(myloc);
	    					this.saveConfig();
	    					msg(player,"Compass set for "+args[1]);
	    				}
	    				else if (args[0].equalsIgnoreCase("WEST")) {
	    					Location myloc = new Location(user.getWorld(),Double.MAX_VALUE,64,0);
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+user.getName(),Double.MAX_VALUE+",0");
	    					user.setCompassTarget(myloc);
	    					this.saveConfig();
	    					msg(player,"Compass set for "+args[1]);
	    				}
	    				else if ((Bukkit.getPlayer(args[0])!=null)){
	    					this.reloadConfig();
	    					this.getConfig().set("Players."+args[1]+"."+user.getWorld().getName(),args[0]);
	    					this.saveConfig();
	    					msg(player,args[1] + " is now tracking " + args[0]);
	    				}
	    				else {
	    					msg(player,"Unknown mode" + args[0]);
	    				}
	    			}
	    			else {
	    				msg(player,"Cannot find PLAYER "+args[1]);
	    			}
	    			}
	    			else {
	    				msg(player,"Too many parameters. For help use /compass help");
	    			}
    			}
    		}
    		else {
    			msg(player,ChatColor.GOLD+"Commands:");
    			msg(player,ChatColor.GREEN+" - /compass <mode> - sets your compass mode");
    			msg(player,ChatColor.GREEN+" - /compass <mode> <player> - sets a player's mode");
    			msg(player,ChatColor.GREEN+" - /compass list - a list of all the modes");
    		}
    	}
    	return false; 
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
		final Player myplayer = event.getPlayer();
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(this, new Runnable() {

			@Override
			public void run() {
				update(myplayer);				
			}
        }, 20L);
    }
    
    public void update(Player player) {
    	boolean override = false;
    	
        
        
		if (true) {
		
        //Location myloc = new Location(player.getWorld(),191,18,-1023);
        //player.setCompassTarget(player.getLocation());
			String current = "";
        try {
    		try {
			if (getConfig().getString("multiworld."+player.getWorld().getName()+".override").equalsIgnoreCase("true")) {
				current = getConfig().getString("multiworld."+player.getWorld().getName()+".mode");
			}
			else if (this.getConfig().contains("Players."+player.getName()+"."+player.getWorld().getName())) {
				current = getConfig().getString("Players."+player.getName()+"."+player.getWorld().getName());
				
			}
			else {
				current = getConfig().getString("multiworld."+player.getWorld().getName()+".mode");
			}
    		}
    		catch (Exception e) {
    		}
        	if (((StringUtils.countMatches(String.valueOf(current), ",") == 1)&&(checkperm(player,"compassmodes.location")))) {
        		String[] parts = current.split(",");
        		Location myloc = new Location(player.getWorld(),Double.valueOf(parts[0]).intValue(),64,Double.valueOf(parts[1]).intValue());
        		player.setCompassTarget(myloc);
        	}
        	else if ((current.equalsIgnoreCase("DEATH"))&&(checkperm(player,"compassmodes.deathpoint"))) { 
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
        	else if ((current.equalsIgnoreCase("BED"))&&(checkperm(player,"compassmodes.bed"))) { 
				this.reloadConfig();
				if (player.getBedSpawnLocation() != null) {
					this.getConfig().set("Players."+player.getName()+"."+player.getWorld().getName(),"BED");
					player.setCompassTarget(player.getBedSpawnLocation());
					this.saveConfig();
				}       	
        	} 	
        }
        catch (Exception e) {
        	player.setCompassTarget(player.getWorld().getSpawnLocation());
        	e.printStackTrace();
        }
    }
}
 
    @Override
    public void onDisable() {
    	try {
    	timer.cancel();
    	timer.purge();
    	}
    	catch (Exception e) {
    		
    	}
    	this.reloadConfig();
    	this.saveConfig();
        // TODO Insert logic to be performed when the plugin is disabled
    }
         
    // Leave event
}
