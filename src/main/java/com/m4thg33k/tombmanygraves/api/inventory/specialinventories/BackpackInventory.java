package com.m4thg33k.tombmanygraves.api.inventory.specialinventories;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import com.m4thg33k.tombmanygraves2api.api.ISpecialInventory;
import com.m4thg33k.tombmanygraves2api.api.SpecialInventory;
import com.m4thg33k.tombmanygraves2api.api.SpecialInventoryHelper;
import com.m4thg33k.tombmanygraves2api.api.TransitionInventory;

import de.eydamos.backpack.data.PlayerSave;
import de.eydamos.backpack.helper.BackpackHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;

@SpecialInventory(id = "eydamosbackpacks", name = "Backpacks", overridable = true, reqMod = "backpack")
public class BackpackInventory implements ISpecialInventory {

    @Override
    public boolean pregrabLogic(EntityPlayer player) {
        return true; // no logic to stop graves
    }

    @Override
    public NBTBase getNbtData(EntityPlayer player) {
        ItemStack backpack = BackpackHelper.getBackpackFromPlayer(player, false); // get backpack in slot
        if (! backpack.isEmpty()) {
            return SpecialInventoryHelper.getTagListFromIInventory(PlayerSave.loadPlayer(player.getEntityWorld(), player));
        } else {
            return null;
        }
    }

    @Override
    public void insertInventory(EntityPlayer player, NBTBase compound, boolean shouldForce) {
        if (compound instanceof NBTTagList) {
            TransitionInventory graveItems = new TransitionInventory((NBTTagList) compound);
            IInventory currentInventory = PlayerSave.loadPlayer(player.getEntityWorld(), player);

            for (int i = 0; i < graveItems.getSizeInventory(); i++) {
                ItemStack graveItem = graveItems.getStackInSlot(i);
                if (! graveItem.isEmpty()) {
                    ItemStack playerItem = currentInventory.getStackInSlot(i).copy();

                    if (playerItem.isEmpty()) {
                        // No problem, just put the grave item in!
                        currentInventory.setInventorySlotContents(i, graveItem);
                    } else if (shouldForce) {
                        // Slot is blocked, but we're forcing the grave item into place.
                        currentInventory.setInventorySlotContents(i, graveItem);
                        SpecialInventoryHelper.dropItem(player, playerItem);
                    } else {
                        // Slot is blocked, but we're not forcing items in - drop the grave item
                        SpecialInventoryHelper.dropItem(player, graveItem);
                    }
                }
            }
        }
    }

    @Nonnull
    @Override
    public List<ItemStack> getDrops(NBTBase compound) {
        if (compound instanceof NBTTagList) {
            return (new TransitionInventory((NBTTagList) compound)).getListOfNonEmptyItemStacks();
        } else {
            return new ArrayList<ItemStack>();
        }
    }
}