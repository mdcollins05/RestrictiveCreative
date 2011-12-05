/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blockmovers.plugins.restrictivecreative;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;

/**
 *
 * @author MattC
 */
public class RestrictiveCreativeEntityListener extends EntityListener {
    private final RestrictiveCreative plugin;
    
    public RestrictiveCreativeEntityListener(RestrictiveCreative plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onEntityDamage(EntityDamageEvent event) {

        if (event instanceof EntityDamageByEntityEvent) {
            Player attacker = (Player) ((EntityDamageByEntityEvent) event.getEntity().getLastDamageCause()).getDamager();
            if (attacker instanceof Player) {
                if (!plugin.creative.contains(attacker)) {
                    return;
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }
    
}
