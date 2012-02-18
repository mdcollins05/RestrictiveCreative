/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blockmovers.plugins.restrictivecreative;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

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

        Item item = event.getItemDrop();

        if (!plugin.creative.contains(player)) {
            return;
        }

        item.remove();
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

        if (!plugin.creative.contains(player)) {
            return;
        }
        //else {
        //    if (player.isSneaking()) {
        //        return;
        //    }
        //}

        if (player.hasPermission("rc.allow.fly")) {
            return;
        }
        
        Location l = player.getLocation();
        Integer x = l.getBlockX();
        Integer y = l.getBlockY(); //height
        Integer z = l.getBlockZ();
        Float yaw = l.getYaw();
        Float pitch = l.getPitch();

        for (int i = 0; i <= plugin.flightLimit; i++) {
            Integer testy = (y - i);
            Integer blockid = player.getWorld().getBlockTypeIdAt(x, testy, z);
            if (blockid != Material.AIR.getId()) {
                break;
            }
            if (i == plugin.flightLimit) {
                double newy = testy + 4;
                player.teleport(new Location(player.getWorld(), player.getLocation().getX(), newy, player.getLocation().getZ(), yaw, pitch));
                player.sendMessage(ChatColor.RED + "Flying too high is dangerous.");
            }
        }

    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Action action = event.getAction();
        String actionname = event.getEventName();
        Block block = event.getClickedBlock();
        int type = block.getTypeId();
        Player player = event.getPlayer();

        if (!plugin.creative.contains(player)) {
            return;
        }

        if (action == Action.RIGHT_CLICK_BLOCK) {
            if (type == Material.CHEST.getId()) {
                if (!player.hasPermission("rc.chest.open")) {
                    player.sendMessage(ChatColor.RED + "You can't open chests in this mode.");
                    event.setCancelled(true);
                }
            }
        } else if (action == Action.LEFT_CLICK_BLOCK) {
            if (type == Material.CHEST.getId()) {
                if (!player.hasPermission("rc.chest.break")) {
                    player.sendMessage(ChatColor.RED + "You can't destroy chests in this mode.");
                    event.setCancelled(true);
                }
            }

        }
        //player.getServer().broadcastMessage(player + " interact event: " + actionname);
        return;
    }

    @EventHandler
    public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        Integer item = event.getBucket().getId();
        String playername = player.getName();

        if (!plugin.creative.contains(player)) {
            return;
        }

        if (plugin.creative.contains(player)) {
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

        if (!plugin.creative.contains(player)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("rc.creativeonjoin")) {
            plugin.toggleCreative(player, true);
            return;
        } else {
            player.setGameMode(GameMode.SURVIVAL);
        }
    }

    @EventHandler
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        if (event.isCancelled()) {
            return;
        }

        GameMode gameMode = event.getPlayer().getGameMode();
        Player player = event.getPlayer();

        if (gameMode.getValue() == 0) {
            plugin.creative.add(player);
        } else if (gameMode.getValue() == 1) {
            plugin.creative.remove(player);
        }
    }
}
