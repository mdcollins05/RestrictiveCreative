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
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class RestrictiveCreative extends JavaPlugin {

    static final Logger log = Logger.getLogger("Minecraft"); //set up our logger
    public Set<Player> creative = new HashSet<Player>(); //hashmap for who has this command enabled or not
    public List<Integer> creativeBreakWhitelist = null;
    public List<Integer> creativeBreakBlacklist = null;
    public List<Integer> creativePlaceWhitelist = null;
    public List<Integer> creativePlaceBlacklist = null;
    public String creativeConfigBreakListType = null;
    public String creativeConfigPlaceListType = null;
    public List<Integer> generalBreakWhitelist = null;
    public List<Integer> generalBreakBlacklist = null;
    public List<Integer> generalPlaceWhitelist = null;
    public List<Integer> generalPlaceBlacklist = null;
    public String generalConfigBreakListType = null;
    public String generalConfigPlaceListType = null;
    public List<Integer> creativeItemUseBlackList = null;
    public List<Integer> generalItemUseBlackList = null;
    public Integer flightLimit = null;
    public List<Material> droppableItems = new ArrayList();
    //private PlayerListener = new RestrictiveCreativePlayerListener(this);
    //private EntityListener Listener = new RestrictiveCreativeEntityListener(this);
    //private BlockListener Listener = new RestrictiveCreativeBlockListener(this);
    //private SLAPI SLAPI = new SLAPI();
    //public FileConfiguration config; //config object

    public void onEnable() {
        PluginDescriptionFile pdffile = this.getDescription();
        PluginManager pm = this.getServer().getPluginManager(); //the plugin object which allows us to add listeners later on

        //pm.registerEvent(Event.Type.PLAYER_DROP_ITEM, playerListener, Event.Priority.Normal, this);
        //pm.registerEvent(Event.Type.PLAYER_PICKUP_ITEM, playerListener, Event.Priority.Normal, this);
        //pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Event.Priority.Normal, this);
        //pm.registerEvent(Event.Type.PLAYER_GAME_MODE_CHANGE, playerListener, Event.Priority.Normal, this);
        //pm.registerEvent(Event.Type.PLAYER_INTERACT, playerListener, Event.Priority.Normal, this);
        //pm.registerEvent(Event.Type.PLAYER_BUCKET_EMPTY, playerListener, Event.Priority.Normal, this);

        //pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Event.Priority.Normal, this);
        //pm.registerEvent(Event.Type.ENTITY_TARGET, entityListener, Event.Priority.Normal, this);

        //pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Event.Priority.Normal, this);
        //pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Event.Priority.Normal, this);
        getServer().getPluginManager().registerEvents(new RestrictiveCreativeBlockListener(this), this);
        getServer().getPluginManager().registerEvents(new RestrictiveCreativeEntityListener(this), this);
        getServer().getPluginManager().registerEvents(new RestrictiveCreativePlayerListener(this), this);

        loadConfiguration();

        flightLimit = getConfig().getInt("creative.fly.heightlimit");

        creativeBreakWhitelist = getConfig().getIntegerList("creative.break.whitelist");
        creativeBreakBlacklist = getConfig().getIntegerList("creative.break.blacklist");
        creativeConfigBreakListType = getConfig().getString("creative.break.defaultlist");

        creativePlaceWhitelist = getConfig().getIntegerList("creative.place.whitelist");
        creativePlaceBlacklist = getConfig().getIntegerList("creative.place.blacklist");
        creativeConfigPlaceListType = getConfig().getString("creative.place.defaultlist");

        generalBreakWhitelist = getConfig().getIntegerList("general.break.whitelist");
        generalBreakBlacklist = getConfig().getIntegerList("general.break.blacklist");
        generalConfigBreakListType = getConfig().getString("general.break.defaultlist");

        generalPlaceWhitelist = getConfig().getIntegerList("general.place.whitelist");
        generalPlaceBlacklist = getConfig().getIntegerList("general.place.blacklist");
        generalConfigPlaceListType = getConfig().getString("general.place.defaultlist");

        creativeItemUseBlackList = getConfig().getIntegerList("creative.items.blacklist");
        generalItemUseBlackList = getConfig().getIntegerList("general.items.blacklist");

        droppableItems.add(Material.DETECTOR_RAIL);
        droppableItems.add(Material.DIODE_BLOCK_OFF);
        droppableItems.add(Material.DIODE_BLOCK_ON);
        droppableItems.add(Material.LEVER);
        droppableItems.add(Material.RAILS);
        droppableItems.add(Material.REDSTONE_WIRE);
        droppableItems.add(Material.STONE_BUTTON);
        droppableItems.add(Material.TORCH);
        droppableItems.add(Material.WOODEN_DOOR);
        droppableItems.add(Material.IRON_DOOR_BLOCK);
        droppableItems.add(Material.SIGN);

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
                                if (s.hasPermission("rc.toggle.others")) {
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
                            if (s.hasPermission("rc.toggle") || this.isCreative(s)) {
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

    public boolean playerStanding(Player player) {
        //get location coords, check blocks on all sides if within .25 of solid block
        return false;
    }

    public boolean checkPerm(Player player, String mode, String action, Integer item) {
        boolean listapplied = false;
        boolean exemptblacklist = false;
        boolean exemptwhitelist = false;
        boolean blacklistperm = false;
        boolean whitelistperm = false;
        String list = null;
        List<Integer> blacklist = null;
        List<Integer> whitelist = null;

        if (mode.equalsIgnoreCase("creative")) {
            if (action.equalsIgnoreCase("place")) {
                list = this.creativeConfigPlaceListType;
                whitelist = this.creativePlaceWhitelist;
                blacklist = this.creativePlaceBlacklist;
                whitelistperm = player.hasPermission("rc.creative.place.whitelist");
                blacklistperm = player.hasPermission("rc.creative.place.blacklist");
                exemptwhitelist = player.hasPermission("rc.creative.place.whitelist.exempt");
                exemptblacklist = player.hasPermission("rc.creative.place.blacklist.exempt");
            } else if (action.equalsIgnoreCase("break")) {
                list = this.creativeConfigBreakListType;
                whitelist = this.creativeBreakWhitelist;
                blacklist = this.creativeBreakBlacklist;
                whitelistperm = player.hasPermission("rc.creative.break.whitelist");
                blacklistperm = player.hasPermission("rc.creative.break.blacklist");
                exemptwhitelist = player.hasPermission("rc.creative.break.whitelist.exempt");
                exemptblacklist = player.hasPermission("rc.creative.break.blacklist.exempt");
            } else if (action.equalsIgnoreCase("use")) {
                list = "blacklist";
                //whitelist = this.creativeBreakWhitelist;
                blacklist = this.creativeItemUseBlackList;
                //whitelistperm = player.hasPermission("rc.creative.break.whitelist");
                //blacklistperm = player.hasPermission("rc.creative.break.blacklist");
                //exemptwhitelist = player.hasPermission("rc.creative.break.whitelist.exempt");
                exemptblacklist = player.hasPermission("rc.creative.items.exempt");
            } else {
                return false;
            }
        } else if (mode.equalsIgnoreCase("general")) {
            if (action.equalsIgnoreCase("place")) {
                list = this.generalConfigPlaceListType;
                whitelist = this.generalPlaceWhitelist;
                blacklist = this.generalPlaceBlacklist;
                whitelistperm = player.hasPermission("rc.general.place.whitelist");
                blacklistperm = player.hasPermission("rc.general.place.blacklist");
                exemptwhitelist = player.hasPermission("rc.general.place.whitelist.exempt");
                exemptblacklist = player.hasPermission("rc.general.place.blacklist.exempt");
            } else if (action.equalsIgnoreCase("break")) {
                list = this.generalConfigBreakListType;
                whitelist = this.generalBreakWhitelist;
                blacklist = this.generalBreakBlacklist;
                whitelistperm = player.hasPermission("rc.general.break.whitelist");
                blacklistperm = player.hasPermission("rc.general.break.blacklist");
                exemptwhitelist = player.hasPermission("rc.general.break.whitelist.exempt");
                exemptblacklist = player.hasPermission("rc.general.break.blacklist.exempt");
            } else if (action.equalsIgnoreCase("use")) {
                list = "blacklist";
                //whitelist = this.creativeBreakWhitelist;
                blacklist = this.generalItemUseBlackList;
                //whitelistperm = player.hasPermission("rc.creative.break.whitelist");
                //blacklistperm = player.hasPermission("rc.creative.break.blacklist");
                //exemptwhitelist = player.hasPermission("rc.creative.break.whitelist.exempt");
                exemptblacklist = player.hasPermission("rc.general.items.exempt");
            } else {
                return false;
            }
        } else {
            return false;
        }


        if (whitelist != null) {
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
        }

        if (blacklist != null) {
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
        String playername = p.getName();
        String invPath = getDataFolder() + File.separator + "playerdata";
        String path = invPath + File.separator + playername + ".bin";
        new File(invPath).mkdir();
        File backupFile = new File(path);
        if (!this.creative.contains(p) | enable) {
            if (!p.hasPermission("rc.keepinv")) {
                if (!backupFile.exists()) {
                    try {
                        backupFile.createNewFile();
                    } catch (Exception e) {
                        log.info("RestrictiveCreative - Couldn't make backup for " + p.getName());
                        return;
                    }
                }

                try {
                    SerializableInventory inv = new SerializableInventory(p.getInventory());

                    FileOutputStream fos = new FileOutputStream(backupFile);
                    ObjectOutputStream oos = new ObjectOutputStream(fos);

                    oos.writeObject(inv);
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
            if (backupFile.exists()) {
                try {
                    FileInputStream fis = new FileInputStream(backupFile);
                    ObjectInputStream ois = new ObjectInputStream(fis);

                    Object o = ois.readObject();
                    ois.close();

                    SerializableInventory inv = (SerializableInventory) o;
                    SerializableInventory.loadContents(p, inv);

                    backupFile.delete();

                } catch (Exception e) {
                    e.printStackTrace();
                    log.info("RestrictiveCreative - Couldn't restore backup for " + p.getName());
                }
            }
            p.setGameMode(GameMode.SURVIVAL);
            this.creative.remove(p);
        }
    }

    public boolean isCreative(Player p) {
        if (p.getGameMode() == GameMode.CREATIVE) {
            return true;
        }
        return false;
    }

    public void loadConfiguration() {
        getConfig().addDefault("creative.fly.heightlimit", 5);

        //creative break blacklist/whitelist
        getConfig().addDefault("creative.break.defaultlist", "blacklist");
        List<Integer> breakwhitelist = new ArrayList();
        breakwhitelist.add(2);
        breakwhitelist.add(3);
        breakwhitelist.add(4);
        breakwhitelist.add(12);
        breakwhitelist.add(13);
        breakwhitelist.add(17);
        breakwhitelist.add(35);
        breakwhitelist.add(50);
        getConfig().addDefault("creative.break.whitelist", breakwhitelist);
        List<Integer> breakblacklist = new ArrayList();
        breakblacklist.add(7);
        getConfig().addDefault("creative.break.blacklist", breakblacklist);

        //creative place blacklist/whitelist
        getConfig().addDefault("creative.place.defaultlist", "whitelist");
        List<Integer> placewhitelist = new ArrayList();
        placewhitelist.add(2);
        placewhitelist.add(3);
        placewhitelist.add(4);
        placewhitelist.add(12);
        placewhitelist.add(13);
        placewhitelist.add(17);
        placewhitelist.add(35);
        placewhitelist.add(50);
        getConfig().addDefault("creative.place.whitelist", placewhitelist);
        List<Integer> placeblacklist = new ArrayList();
        placeblacklist.add(7);
        placeblacklist.add(10);
        placeblacklist.add(11);
        placeblacklist.add(51);
        placeblacklist.add(52);
        getConfig().addDefault("creative.place.blacklist", placeblacklist);

        //general place blacklist/whitelist
        getConfig().addDefault("general.place.defaultlist", "blacklist");
        List<Integer> genplacewhitelist = new ArrayList();
        genplacewhitelist.add(2);
        genplacewhitelist.add(3);
        genplacewhitelist.add(4);
        genplacewhitelist.add(12);
        genplacewhitelist.add(13);
        genplacewhitelist.add(17);
        genplacewhitelist.add(35);
        genplacewhitelist.add(50);
        getConfig().addDefault("general.place.whitelist", genplacewhitelist);
        List<Integer> genplaceblacklist = new ArrayList();
        genplaceblacklist.add(7);
        getConfig().addDefault("general.place.blacklist", genplaceblacklist);

        //general break blacklist/whitelist
        getConfig().addDefault("general.break.defaultlist", "blacklist");
        List<Integer> genbreakwhitelist = new ArrayList();
        genbreakwhitelist.add(2);
        genbreakwhitelist.add(3);
        genbreakwhitelist.add(4);
        genbreakwhitelist.add(12);
        genbreakwhitelist.add(13);
        genbreakwhitelist.add(17);
        genbreakwhitelist.add(35);
        genbreakwhitelist.add(50);
        getConfig().addDefault("general.break.whitelist", genbreakwhitelist);
        List<Integer> genbreakblacklist = new ArrayList();
        genbreakblacklist.add(7);

        //items blacklists
        List<Integer> itemblacklist = new ArrayList();
        itemblacklist.add(381);
        itemblacklist.add(383);
        itemblacklist.add(384);
        itemblacklist.add(385);
        itemblacklist.add(259);
        getConfig().addDefault("creative.items.blacklist", itemblacklist);
        List<Integer> genitemblacklist = new ArrayList();
        genitemblacklist.add(384);
        getConfig().addDefault("general.items.blacklist", genitemblacklist);
        getConfig().options().copyDefaults(true);
        //Save the config whenever you manipulate it
        saveConfig();
    }
}
