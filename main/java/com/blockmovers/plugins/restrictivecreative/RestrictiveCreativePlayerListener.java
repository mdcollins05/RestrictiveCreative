/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blockmovers.plugins.restrictivecreative;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;

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
        Action action = event.getAction();
        Block block = event.getClickedBlock();
        int type = block.getTypeId();
        
        if (type == Material.CHEST.getId()) {
            event.setCancelled(true);
        }
        
//        if (action == Action.RIGHT_CLICK_BLOCK) {
//            
//        } else if (action == Action.LEFT_CLICK_BLOCK) {
//            
//        }
        //plugin.getServer().broadcastMessage("Player interact event: " + type);
        return;
    }

    @Override
    public void onPlayerPickupItem(PlayerPickupItemEvent event) {
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
            player.setGameMode(GameMode.CREATIVE);
            plugin.creative.add(player);
            return;
        }
        
        player.setGameMode(GameMode.SURVIVAL);
    }

    @Override
    public void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
        GameMode gameMode = event.getPlayer().getGameMode();
        Player player = event.getPlayer();
        
        if (gameMode.getValue() == 0) {
            plugin.creative.add(player);
        }
        else if (gameMode.getValue() == 1) {
            plugin.creative.remove(player);
        }
    }
    
}
