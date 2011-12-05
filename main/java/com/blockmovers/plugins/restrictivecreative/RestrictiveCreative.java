package com.blockmovers.plugins.restrictivecreative;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RestrictiveCreative extends JavaPlugin {
    static final Logger log = Logger.getLogger("Minecraft"); //set up our logger
    public Set<Player> creative = new HashSet<Player>(); //hashmap for who has this command enabled or not
    private PlayerListener playerListener = new RestrictiveCreativePlayerListener(this);
    private EntityListener entityListener = new RestrictiveCreativeEntityListener(this);
    private BlockListener blockListener = new RestrictiveCreativeBlockListener(this);
    //public FileConfiguration config; //config object
    
    public void onEnable() {
        PluginDescriptionFile pdffile = this.getDescription();
        PluginManager pm = this.getServer().getPluginManager(); //the plugin object which allows us to add listeners later on
        //config = getConfig(); //retrieve/create the config file
        
        pm.registerEvent(Event.Type.PLAYER_DROP_ITEM, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_PICKUP_ITEM, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_GAME_MODE_CHANGE, playerListener, Event.Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Monitor, this);
        
        pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.ENTITY_TARGET, entityListener, Event.Priority.Normal, this);
        
        pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Event.Priority.Normal, this);
        
        log.info(pdffile.getName() + " version " + pdffile.getVersion() + " is enabled.");
    }
    
        public void onDisable() {
        PluginDescriptionFile pdffile = this.getDescription();
        
        log.info(pdffile.getName() + " version " + pdffile.getVersion() + " is disabled.");
    }
    
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
        Player s = (Player) cs;
        Player target = null;
        if (cmd.getName().equalsIgnoreCase("creative")) {
            if (args.length >= 1) {
                if (args[0].equals("toggle") || args[0].equals("t")) {
                    if (args.length == 2) {
                        if (getServer().getPlayer(args[1]) != null)
                        {
                            target = getServer().getPlayer(args[1]);
                        }
                    }
                    else if (args.length == 1) {
                        if (cs instanceof Player) {
                            target = (Player) s;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                    
                    if (!creative.contains(target)) {
                        target.setGameMode(GameMode.CREATIVE);
                        creative.add(target);
                    } else {
                        target.setGameMode(GameMode.SURVIVAL);
                        creative.remove(target);
                    }
                }
            } else {
                return false;
            }
            return true;
        }
        return false;
    }
}
