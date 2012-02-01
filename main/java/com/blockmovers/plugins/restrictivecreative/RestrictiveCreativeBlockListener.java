/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blockmovers.plugins.restrictivecreative;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;

/**
 *
 * @author MattC
 */
public class RestrictiveCreativeBlockListener extends BlockListener {

    private final RestrictiveCreative plugin;

    public RestrictiveCreativeBlockListener(RestrictiveCreative plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        //Block block = event.getBlock();
        Integer item = event.getBlockPlaced().getTypeId();
        //String playername = player.getName();

        if (!plugin.creative.contains(player)) {
            return;
        }

        //player.getServer().broadcastMessage(playername + " placed a " + item);

        if (!plugin.checkPerm(player, "place", item)) {
            event.setCancelled(true);
        }
    }

    @Override
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        //Block block = event.getBlock();
        Integer item = event.getBlock().getTypeId();
        //String playername = player.getName();

        if (!plugin.creative.contains(player)) {
            return;
        }
        
        //player.getServer().broadcastMessage(playername + " broke " + item + ". looking for " + Material.BEDROCK.getId());
        
        if (!plugin.checkPerm(player, "break", item)) {
            event.setCancelled(true);
        }
    }
}
