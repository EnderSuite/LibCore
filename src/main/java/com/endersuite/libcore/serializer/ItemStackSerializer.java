package com.endersuite.libcore.serializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.*;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

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

    public static String serialize(int slot, ItemStack itemStack) {
        List<SerializedItem> serializedItems = new ArrayList<>();
        serializedItems.add(getSerializedItem(slot, itemStack));
        return serialize(serializedItems);
    }

    public static String serialize(Inventory inventory) {
        List<SerializedItem> serializedItems = new ArrayList<>();
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) != null && !inventory.getItem(i).getType().equals(Material.AIR)) {
                ItemStack itemStack = inventory.getItem(i);
                serializedItems.add(getSerializedItem(i, itemStack));
            }
        }
        return serialize(serializedItems);
    }

    public static String serialize(List<SerializedItem> serializedItems) {
        return GSON.toJson(serializedItems);
    }

    public static List<SerializedItem> deserializeInventory(String serialized) {
        try {
            Type items = new TypeToken<List<SerializedItem>>(){}.getType();
            return GSON.fromJson(serialized, items);
        } catch (Exception e) {
            return null;
        }
    }

    private static SerializedItem getSerializedItem(int slot, ItemStack itemStack) {
        SerializedItem serializer = new SerializedItem();
        serializer.setMaterial(itemStack.getType().name());
        serializer.setDisplayName(itemStack.getItemMeta().hasDisplayName() ?
                itemStack.getItemMeta().getDisplayName() : null);
        serializer.setAmount(itemStack.getAmount());
        serializer.setSlot(slot);
        serializer.setMaterialAttributes(itemStack.serialize());
        return serializer;
    }

}
