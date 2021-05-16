package com.endersuite.libcore.serializer;

import com.endersuite.libcore.strfmt.StrFmt;
import lombok.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.Serializable;
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
