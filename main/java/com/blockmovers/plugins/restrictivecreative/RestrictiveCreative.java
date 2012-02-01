package com.blockmovers.plugins.restrictivecreative;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RestrictiveCreative extends JavaPlugin {

    static final Logger log = Logger.getLogger("Minecraft"); //set up our logger
    public Set<Player> creative = new HashSet<Player>(); //hashmap for who has this command enabled or not
    public List<Integer> breakWhitelist = null;
    public List<Integer> breakBlacklist = null;
    public List<Integer> placeWhitelist = null;
    public List<Integer> placeBlacklist = null;
    public String configBreakListType = null;
    public String configPlaceListType = null;
    private PlayerListener playerListener = new RestrictiveCreativePlayerListener(this);
    private EntityListener entityListener = new RestrictiveCreativeEntityListener(this);
    private BlockListener blockListener = new RestrictiveCreativeBlockListener(this);
    //private SLAPI SLAPI = new SLAPI();
    //public FileConfiguration config; //config object

    public void onEnable() {
        PluginDescriptionFile pdffile = this.getDescription();
        PluginManager pm = this.getServer().getPluginManager(); //the plugin object which allows us to add listeners later on

        pm.registerEvent(Event.Type.PLAYER_DROP_ITEM, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_PICKUP_ITEM, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_GAME_MODE_CHANGE, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_BUCKET_EMPTY, playerListener, Event.Priority.Normal, this);

        pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.ENTITY_TARGET, entityListener, Event.Priority.Normal, this);
        //pm.registerEvent(Event.Type.ITEM_SPAWN, entityListener, Event.Priority.Normal, this);

        pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Event.Priority.Normal, this);

        loadConfiguration();

        breakWhitelist = getConfig().getList("break.whitelist");
        breakBlacklist = getConfig().getList("break.blacklist");
        configBreakListType = getConfig().getString("break.defaultlist");
        
        placeWhitelist = getConfig().getList("place.whitelist");
        placeBlacklist = getConfig().getList("place.blacklist");
        configPlaceListType = getConfig().getString("place.defaultlist");

        log.info(pdffile.getName() + " version " + pdffile.getVersion() + " is enabled.");
    }

    public void onDisable() {
        PluginDescriptionFile pdffile = this.getDescription();

        log.info(pdffile.getName() + " version " + pdffile.getVersion() + " is disabled.");
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String alias, String[] args) {
        Player target = null;
        if (cmd.getName().equalsIgnoreCase("creative")) {
            if (args.length >= 1) {
                if (args[0].equals("toggle") || args[0].equals("t")) {
                    if (args.length == 2) {
                        if (getServer().getPlayer(args[1]) != null) {
                            if (cs instanceof Player) {
                                Player s = (Player) cs;
                                if (s.hasPermission("restrictivecreative.toggle.others")) {
                                    target = getServer().getPlayer(args[1]);
                                } else {
                                    s.sendMessage(ChatColor.RED + "You don't have permission to do that!");
                                    return false;
                                }
                            } else {
                                target = getServer().getPlayer(args[1]);
                            }
                        } else {
                            cs.sendMessage(ChatColor.RED + "Player not found.");
                            return false;
                        }
                    } else if (args.length == 1) {
                        if (cs instanceof Player) {
                            Player s = (Player) cs;
                            if (s.hasPermission("restrictivecreative.toggle")) {
                                target = (Player) s;
                            } else {
                                s.sendMessage(ChatColor.RED + "You don't have permission to do that!");
                                return false;
                            }
                        } else {
                            cs.sendMessage(ChatColor.RED + "Console cannot do that command. Give a player to affect.");
                            return false;
                        }
                    } else {
                        return false;
                    }
                    this.toggleCreative(target, false);
                }
            } else {
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean checkPerm(Player player, String action, Integer item) {
        boolean listapplied = false;
        boolean exemptblacklist = false;
        boolean exemptwhitelist = false;
        boolean blacklistperm = false;
        boolean whitelistperm = false;
        String list = null;
        List<Integer> blacklist = null;
        List<Integer> whitelist = null;
        
        if (action.equalsIgnoreCase("place")) {
            list = this.configPlaceListType;
            whitelist = this.placeWhitelist;
            blacklist = this.placeBlacklist;
            whitelistperm = player.hasPermission("restrictivecreative.place.whitelist");
            blacklistperm = player.hasPermission("restrictivecreative.place.blacklist");
            exemptwhitelist = player.hasPermission("restrictivecreative.place.whitelist.exempt");
            exemptblacklist = player.hasPermission("restrictivecreative.place.blacklist.exempt");
        }
        else if (action.equalsIgnoreCase("break")) {
            list = this.configBreakListType;
            whitelist = this.breakWhitelist;
            blacklist = this.breakBlacklist;
            whitelistperm = player.hasPermission("restrictivecreative.break.whitelist");
            blacklistperm = player.hasPermission("restrictivecreative.break.blacklist");
            exemptwhitelist = player.hasPermission("restrictivecreative.break.whitelist.exempt");
            exemptblacklist = player.hasPermission("restrictivecreative.break.blacklist.exempt");
        }
        else
        {
            return false;
        }
        
        if (!exemptwhitelist) {
            if (!whitelist.contains(item)) {
                if (list.equalsIgnoreCase("whitelist")) {
                    listapplied = true;
                }
                if (whitelistperm) {
                    listapplied = true;
                }
            }
        }
        
        if (!exemptblacklist) {
            if (blacklist.contains(item)) {
                if (list.equalsIgnoreCase("blacklist")) {
                    listapplied = true;
                }
                if (blacklistperm) {
                    listapplied = true;
                }
            }
        }
        
//        if (list.equalsIgnoreCase("whitelist") || whitelistperm) {
//            if (!whitelist.contains(item)) {
//                if (!exemptwhitelist) {
//                    listapplied = true;
//                }
//            }
//        }
//
//        if (list.equalsIgnoreCase("blacklist") || blacklistperm) {
//            if (blacklist.contains(item)) {
//                if (!exemptblacklist) {
//                    listapplied = true;
//                }
//            }
//        }

        if (listapplied) {
            player.sendMessage(ChatColor.RED + "You can't " + action + " that in this mode.");
            return false;
        } else {
            return true;
        }
    }

    public void toggleCreative(Player p, boolean enable) {
        ItemStack[] stacks = null;
        String playername = p.getPlayerListName();
        String invPath = "plugins" + File.separator + "RestrictiveCreative" + File.separator + "playerdata";
        String path = invPath + File.separator + playername + ".bin";
        new File(invPath).mkdir();
        File backupFile = new File(path);
        if (!this.creative.contains(p) | enable) {
            if (!p.hasPermission("restrictivecreative.keepinv")) {
                if (!backupFile.exists()) {
                    try {
                        backupFile.createNewFile();
                    } catch (Exception e) {
                        log.info("RestrictiveCreative - Couldn't make backup for " + p.getName());
                        return;
                    }
                }

                ItemStack[] items = p.getInventory().getContents();
                ItemStack[] armor = p.getInventory().getArmorContents();
                try {
                    InventoryItem[] inv = new InventoryItem[items.length + armor.length];
                    for (int i = 0; i < items.length; i++) {
                        inv[i] = InventoryItem.parseItemStack(items[i]);
                    }
                    for (int i = 0; i < armor.length; i++) {
                        inv[(i + items.length)] = InventoryItem.parseItemStack(armor[i]);
                    }

                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
                    oos.writeObject(inv);
                    oos.flush();
                    oos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("RestrictiveCreative - Couldn't make backup for " + p.getName());
                    return;
                }
                PlayerInventory inv = p.getInventory();
                inv.clear();
                inv.setHelmet(null);
                inv.setChestplate(null);
                inv.setLeggings(null);
                inv.setBoots(null);
            }
            p.setGameMode(GameMode.CREATIVE);
            this.creative.add(p);
        } else {
            if (!p.hasPermission("restrictivecreative.keepinv")) {
                try {
                    if (!backupFile.exists()) {
                        return;
                    }

                    FileInputStream fis = new FileInputStream(backupFile);
                    ObjectInputStream ois = new ObjectInputStream(fis);
                    InventoryItem[] fromFile = (InventoryItem[]) ois.readObject();
                    ois.close();

                    backupFile.delete();

                    stacks = InventoryItem.toItemStacks(fromFile);
                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("RestrictiveCreative - Couldn't get backup for " + p.getName());
                }
                //list = (ArrayList<Object>)SLAPI.load("example.bin");
                //ItemStack[] stacks = SLAPI.load("example.bin");
                if (stacks == null) {
                    log.info("RestrictiveCreative - Couldn't restore backup for " + p.getName());
                    return;
                }

                if (!p.isOnline()) {
                    System.out.println("THE WORLD IS GOING TO END! SOMETHING IS WRONG!");
                }

                ItemStack[] items = new ItemStack[stacks.length - 4];
                ItemStack[] armor = new ItemStack[4];

                for (int i = 0; i < stacks.length - 4; i++) {
                    items[i] = stacks[i];
                }
                for (int i = 0; i < 4; i++) {
                    armor[i] = stacks[(stacks.length - 4 + i)];
                }

                PlayerInventory inv = p.getInventory();
                inv.setArmorContents(armor);
                for (int i = 0; i < items.length; i++) {
                    inv.setItem(i, items[i]);
                }

            }
            p.setGameMode(GameMode.SURVIVAL);
            this.creative.remove(p);
        }
    }

    public void loadConfiguration() {
        getConfig().addDefault("break.defaultlist", "blacklist");
        List<Integer> breakwhitelist = new ArrayList();
        breakwhitelist.add(2);
        breakwhitelist.add(3);
        breakwhitelist.add(4);
        breakwhitelist.add(12);
        breakwhitelist.add(13);
        breakwhitelist.add(17);
        breakwhitelist.add(35);
        breakwhitelist.add(50);
        getConfig().addDefault("break.whitelist", breakwhitelist);
        List<Integer> breakblacklist = new ArrayList();
        breakblacklist.add(7);
        getConfig().addDefault("break.blacklist", breakblacklist);
        getConfig().addDefault("place.defaultlist", "whitelist");
        List<Integer> placewhitelist = new ArrayList();
        placewhitelist.add(2);
        placewhitelist.add(3);
        placewhitelist.add(4);
        placewhitelist.add(12);
        placewhitelist.add(13);
        placewhitelist.add(17);
        placewhitelist.add(35);
        placewhitelist.add(50);
        getConfig().addDefault("place.whitelist", placewhitelist);
        List<Integer> placeblacklist = new ArrayList();
        placeblacklist.add(7);
        placeblacklist.add(10);
        placeblacklist.add(11);
        placeblacklist.add(51);
        placeblacklist.add(52);
        getConfig().addDefault("place.blacklist", placeblacklist);
        getConfig().options().copyDefaults(true);
        //Save the config whenever you manipulate it
        saveConfig();
    }
}
