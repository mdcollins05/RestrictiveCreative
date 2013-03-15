/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blockmovers.plugins.restrictivecreative;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.*;

/**
 *
 * @author MattC
 */
public class RestrictiveCreativePlayerListener implements Listener {

    private final RestrictiveCreative plugin;

    public RestrictiveCreativePlayerListener(RestrictiveCreative plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();

        if (!plugin.creative.contains(player.getName())) {
            return;
        }

        event.getItemDrop().remove();
        //event.setCancelled(true);
        //event.
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        //if they don't move off the block, dont process
        if (event.getTo().getBlock() == event.getFrom().getBlock()) {
            return;
        }

        Player player = event.getPlayer();

        if (!plugin.creative.contains(player.getName())) {
            return;
        } else {
            if (!player.isFlying()) {
                return;
            }
        }

        if (player.hasPermission("rc.allow.fly")) {
            return;
        }

        Location l = player.getLocation();
        Integer x = l.getBlockX();
        Integer y = l.getBlockY(); //height
        Integer z = l.getBlockZ();
        Float yaw = l.getYaw();
        Float pitch = l.getPitch();

        Integer checkLimit = player.getWorld().getMaxHeight();
        Integer highestUnderPlayer = null;

        for (int i = 0; i <= checkLimit; i++) {
            Integer testy = (y - i);
            if (testy < 0) {
                break; //There's some error and we should probably stop checking
            }
            Integer blockid = player.getWorld().getBlockTypeIdAt(x, testy, z);
            if (blockid != Material.AIR.getId()) {
                highestUnderPlayer = testy; //highest y coordinate with a block under the player
            }
            if (highestUnderPlayer != null) { //if it's set we know we hit something to stand on
                if (i > plugin.flightLimit) { //if they are over the flight limit, bring em down
                    double newy = testy + plugin.flightLimit;
                    player.teleport(new Location(player.getWorld(), l.getX(), newy, l.getZ(), yaw, pitch));
                    player.sendMessage(ChatColor.RED + "Flying too high is dangerous.");
                }
                break;
            }
        }
    }

    @EventHandler
    public void preventLaunches(ProjectileLaunchEvent event) {
//        plugin.getServer().broadcastMessage("Launch Fired.");
//        LivingEntity e = event.getEntity().getShooter();
//        if (e instanceof Player) {
//            Player player = (Player) e;
//            int item = event.getEntityType().getTypeId();
//            plugin.getServer().broadcastMessage("Item id: " + item);
//            if (plugin.creative.contains(player.getName())) {
//                plugin.getServer().broadcastMessage("Creative mode.");
//                if (plugin.creativeItemUseBlackList.contains(item)) {
//                    plugin.getServer().broadcastMessage("Item in list.");
//                    if (!plugin.checkPerm(player, "creative", "use", item)) {
//                        plugin.getServer().broadcastMessage("Item blocked.");
//                        event.setCancelled(true);
//                        return;
//                    }
//                }
//            } else {
//                plugin.getServer().broadcastMessage("Survival mode.");
//                if (plugin.generalItemUseBlackList.contains(item)) {
//                    plugin.getServer().broadcastMessage("Item in list.");
//                    if (!plugin.checkPerm(player, "general", "use", item)) {
//                        plugin.getServer().broadcastMessage("Item blocked.");
//                        event.setCancelled(true);
//                        return;
//                    }
//                }
//            }
//        }
        if (event.getEntityType() == EntityType.THROWN_EXP_BOTTLE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.isCancelled()) {
            //Bukkit.broadcastMessage(event.getPlayer() + " " + event.getAction() + " " + event.getPlayer().getItemInHand().getTypeId());
            return;
        }

        Action action = event.getAction();
        Integer block = event.getClickedBlock().getTypeId();
        Player player = event.getPlayer();
        Integer item = player.getItemInHand().getTypeId();


        //if (player.getItemInHand().getTypeId() == 384 || player.getItemInHand().getTypeId() == 383) {
        //    if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
        //       
        //    }
        //}

//        if (plugin.creative.contains(player.getName())) {
//            player.sendMessage("creative mode " + item);
//            if (plugin.creativeItemUseBlackList.contains(item)) {
//                player.sendMessage("item in list");
//                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
//                    player.sendMessage("right click");
//                    if (plugin.checkPerm(player, "creative", "use", item)) {
//                        player.sendMessage("item in list");
//                        event.setCancelled(true);
//                    }
//                }
//            }
//        } else {
//            if (plugin.generalItemUseBlackList.contains(item)) {
//                if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
//                    if (plugin.checkPerm(player, "general", "use", item)) {
//                        event.setCancelled(true);
//                    }
//                }
//            }
//            return; // if not creative dont check below
//        }

        if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
            if (plugin.creative.contains(player.getName())) {
                if (plugin.creativeItemUseBlackList.contains(item)) {
                    if (!plugin.checkPerm(player, "creative", "use", item)) {
                        event.setCancelled(true);
                        return;
                    }
                }
            } else {
                if (plugin.generalItemUseBlackList.contains(item)) {
                    if (!plugin.checkPerm(player, "general", "use", item)) {
                        event.setCancelled(true);
                        return;
                    }
                }
            }

        }

        if (plugin.creative.contains(player.getName())) {
            if (action == Action.RIGHT_CLICK_BLOCK) {
                if (block == Material.CHEST.getId() || block == Material.STORAGE_MINECART.getId()) {
                    if (!player.hasPermission("rc.chest.open")) {
                        player.sendMessage(ChatColor.RED + "You can't open chests in this mode.");
                        event.setCancelled(true);
                        return;
                    }
                }
                if (block == Material.FURNACE.getId() || block == Material.POWERED_MINECART.getId()) {
                    if (!player.hasPermission("rc.furnace.open")) {
                        player.sendMessage(ChatColor.RED + "You can't open furnaces in this mode.");
                        event.setCancelled(true);
                        return;
                    }
                }
                if (block == Material.BURNING_FURNACE.getId()) {
                    if (!player.hasPermission("rc.furnace.open")) {
                        player.sendMessage(ChatColor.RED + "You can't open furnaces in this mode.");
                        event.setCancelled(true);
                        return;
                    }
                }
                if (block == Material.DISPENSER.getId()) {
                    if (!player.hasPermission("rc.dispenser.open")) {
                        player.sendMessage(ChatColor.RED + "You can't open dispensers in this mode.");
                        event.setCancelled(true);
                        return;
                    }
                }
                if (block == Material.JUKEBOX.getId()) {
                    if (!player.hasPermission("rc.jukebox.open")) {
                        player.sendMessage(ChatColor.RED + "You can't open jukeboxes in this mode.");
                        event.setCancelled(true);
                        return;
                    }
                }
                if (block == Material.ENCHANTMENT_TABLE.getId()) {
                    if (!player.hasPermission("rc.enchant.open")) {
                        player.sendMessage(ChatColor.RED + "You can't use enchantment tables in this mode.");
                        event.setCancelled(true);
                        return;
                    }
                }
                if (block == Material.WALL_SIGN.getId()) {
                    //if (!player.hasPermission("rc.enchant.open")) {
                    player.sendMessage(ChatColor.RED + "You can't use signs in this mode.");
                    event.setCancelled(true);
                    return;
                    //}
                }
                if (block == Material.SIGN_POST.getId()) {
                    //if (!player.hasPermission("rc.enchant.open")) {
                    player.sendMessage(ChatColor.RED + "You can't use signs in this mode.");
                    event.setCancelled(true);
                    return;
                    //}
                }
            } else if (action == Action.LEFT_CLICK_BLOCK) {
                if (block == Material.CHEST.getId()) {
                    if (!player.hasPermission("rc.chest.break")) {
                        player.sendMessage(ChatColor.RED + "You can't destroy chests in this mode.");
                        event.setCancelled(true);
                        return;
                    }
                }
                if (block == Material.FURNACE.getId()) {
                    if (!player.hasPermission("rc.furnace.break")) {
                        player.sendMessage(ChatColor.RED + "You can't destroy furnaces in this mode.");
                        event.setCancelled(true);
                        return;
                    }
                }
                if (block == Material.BURNING_FURNACE.getId()) {
                    if (!player.hasPermission("rc.furnace.break")) {
                        player.sendMessage(ChatColor.RED + "You can't destroy furnaces in this mode.");
                        event.setCancelled(true);
                        return;
                    }
                }
                if (block == Material.DISPENSER.getId()) {
                    if (!player.hasPermission("rc.dispenser.break")) {
                        player.sendMessage(ChatColor.RED + "You can't destroy dispensers in this mode.");
                        event.setCancelled(true);
                        return;
                    }
                }
                if (block == Material.JUKEBOX.getId()) {
                    if (!player.hasPermission("rc.jukebox.break")) {
                        player.sendMessage(ChatColor.RED + "You can't destroy jukeboxes in this mode.");
                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
        //player.getServer().broadcastMessage(player + " interact event: " + actionname);
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        Integer item = event.getBucket().getId();
        String playername = player.getName();

        if (!plugin.creative.contains(player.getName())) {
            return;
        }

        if (plugin.creative.contains(player.getName())) {
            if (!plugin.checkPerm(player, "creative", "place", item)) {
                event.setCancelled(true);
            }
        } else {
            if (!plugin.checkPerm(player, "general", "place", item)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();

        if (!plugin.creative.contains(player.getName())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.getGameMode() == GameMode.CREATIVE) { //creative mode
            if (!plugin.creative.contains(player.getName())) {
                plugin.creative.add(player.getName());
            }
        } else { //not creative mode
            if (plugin.creative.contains(player.getName())) {
                plugin.creative.remove(player.getName());
            }
        }
    }

    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();

        if (event.getNewGameMode() == GameMode.CREATIVE) { //creative mode
            if (!plugin.creative.contains(player.getName())) {
                plugin.creative.add(player.getName());
            }
        } else { //not creative mode
            if (plugin.creative.contains(player.getName())) {
                plugin.creative.remove(player.getName());
            }
        }
    }
}
