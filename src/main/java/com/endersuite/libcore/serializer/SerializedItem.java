package com.endersuite.libcore.serializer;

import com.endersuite.libcore.strfmt.StrFmt;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author TheRealDomm
 * @since 16.05.2021
 */
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class SerializedItem implements Serializable {

    private String material;
    private String displayName;
    private int amount;
    private int slot;
    private boolean emptySlot = false;
    private Map<String, Object> materialAttributes;

    public ItemStack toItemStack() {
        new StrFmt(materialAttributes.toString()).toLog();
        ItemStack itemStack = ItemStack.deserialize(this.materialAttributes);
        if (this.materialAttributes.containsKey("meta") && this.materialAttributes.get("meta") instanceof Map) {
            try {
                String packageName = Bukkit.getServer().getClass().getPackage().getName();
                String version = packageName.substring(packageName.lastIndexOf(".") + 1);
                Class<?> craftMeta = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftMetaItem");
                Method method = craftMeta.getMethod("deserialize", Map.class);
                Object meta = method.invoke(null, this.materialAttributes.get("meta"));
                itemStack.setItemMeta((ItemMeta) meta);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("No meta instance found!!");
        }
        /*if (materialAttributes.containsKey("meta")) {
            itemStack.setItemMeta((ItemMeta) materialAttributes.get("meta"));
        }*/
        return itemStack;
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
