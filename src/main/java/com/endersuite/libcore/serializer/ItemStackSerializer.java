package com.endersuite.libcore.serializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.Map;

/**
 * @author TheRealDomm
 * @since 15.05.2021
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class ItemStackSerializer implements Serializable {

    private static final Gson GSON = new GsonBuilder().create();

    private String material;
    private String displayName;
    private int amount;
    private int slot;
    private Map<String, Object> materialAttributes;

    public static String serialize(int slot, ItemStack itemStack) {
        ItemStackSerializer serializer = new ItemStackSerializer();
        serializer.setMaterial(itemStack.getType().name());
        serializer.setDisplayName(itemStack.getItemMeta().hasDisplayName() ?
                itemStack.getItemMeta().getDisplayName() : null);
        serializer.setAmount(itemStack.getAmount());
        serializer.setSlot(slot);
        serializer.setMaterialAttributes(itemStack.serialize());
        return GSON.toJson(serializer);
    }

    public ItemStackSerializer getInstance(String serialized) {
        return GSON.fromJson(serialized, ItemStackSerializer.class);
    }

    public ItemStack toItemStack() {
        return ItemStack.deserialize(this.materialAttributes);
    }

    public boolean isCompatible() {
        try {
            Material.valueOf(this.material);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean hasDisplayName() {
        return this.displayName != null;
    }

}
