package com.dongyuanxing.eu2emc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

public class ContainerEnergyConverter extends Container {
    private final TileEntityEnergyConverter te;

    public ContainerEnergyConverter(InventoryPlayer playerInv, TileEntityEnergyConverter te) {
        this.te = te;

        // 玩家背包 (3x9)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlotToContainer(new Slot(playerInv, col + row * 9 + 9, 8 + col * 18, 112 + row * 18));
            }
        }

        // 玩家快捷栏 (1x9)
        for (int col = 0; col < 9; col++) {
            this.addSlotToContainer(new Slot(playerInv, col, 8 + col * 18, 170));
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return te.getDistanceSq(player.posX, player.posY, player.posZ) <= 64;
    }
}