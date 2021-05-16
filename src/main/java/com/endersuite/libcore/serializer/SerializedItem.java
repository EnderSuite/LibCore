package com.endersuite.libcore.serializer;

import lombok.*;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

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
    private boolean emptySlot;
    private Map<String, Object> materialAttributes;

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
