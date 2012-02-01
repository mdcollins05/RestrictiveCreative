/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blockmovers.plugins.restrictivecreative;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPickupItemEvent;

/**
 *
 * @author MattC
 */
public class RestrictiveCreativePlayerListener extends PlayerListener {

    private final RestrictiveCreative plugin;

    public RestrictiveCreativePlayerListener(RestrictiveCreative plugin) {
        this.plugin = plugin;
    }

    @Override
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

    @Override
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
                if (!player.hasPermission("restrictivecreative.chest.open")) {
                    player.sendMessage(ChatColor.RED + "You can't open chests in this mode.");
                    event.setCancelled(true);
                }
            }
        } else if (action == Action.LEFT_CLICK_BLOCK) {
            if (type == Material.CHEST.getId()) {
                if (!player.hasPermission("restrictivecreative.chest.break")) {
                    player.sendMessage(ChatColor.RED + "You can't destroy chests in this mode.");
                    event.setCancelled(true);
                }
            }

        }
        //player.getServer().broadcastMessage(player + " interact event: " + actionname);
        return;
    }

    @Override
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

        if (!plugin.checkPerm(player, "place", item)) {
            event.setCancelled(true);
        }
    }

    @Override
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

    @Override
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.hasPermission("restrictivecreative.creativeonjoin")) {
            plugin.toggleCreative(player, true);
            return;
        } else {
            player.setGameMode(GameMode.SURVIVAL);
        }
    }

    @Override
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
