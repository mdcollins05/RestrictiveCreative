/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.blockmovers.plugins.restrictivecreative;

/**
 *
 * @author garbagemule
 * Under the
 * "Please don't use the nice parts of the code and claim it as your own, 
 * but instead please give me credit for it. However, if you want to use 
 * some of the ugly code, please don't, because it's ugly and you should 
 * be able to write something better yourself."
 * License
 */
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

public class SerializableItem
        implements Serializable {
private static final long serialVersionUID = -2855528738291283052L;
  private int id;
  private int amount;
  private short damage;
  private Byte data;
  private Map<Integer, Object> enchantments;

  private SerializableItem(ItemStack stack)
  {
    this.id = stack.getTypeId();
    this.amount = stack.getAmount();
    this.damage = stack.getDurability();

    MaterialData md = stack.getData();
    this.data = (md == null ? null : Byte.valueOf(md.getData()));

    this.enchantments = new HashMap();
    for (Map.Entry entry : stack.getEnchantments().entrySet())
      this.enchantments.put(Integer.valueOf(((Enchantment)entry.getKey()).getId()), entry.getValue());
  }

  public ItemStack toItemStack()
  {
    ItemStack stack = new ItemStack(this.id, this.amount, this.damage, this.data);

    if (!this.enchantments.isEmpty()) {
      for (Map.Entry entry : this.enchantments.entrySet()) {
        stack.addUnsafeEnchantment(Enchantment.getById(((Integer)entry.getKey()).intValue()), ((Integer)entry.getValue()).intValue());
      }
    }

    return stack;
  }

  public static SerializableItem parseSerializableItem(ItemStack stack) {
    if (stack == null) {
      return null;
    }

    return new SerializableItem(stack);
  }
   
}