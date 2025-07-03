package com.dongyuanxing.eu2emc;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
    public static final int ENERGY_CONVERTER_GUI = 0;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == ENERGY_CONVERTER_GUI) {
            return new ContainerEnergyConverter(player.inventory, (TileEntityEnergyConverter) world.getTileEntity(new BlockPos(x, y, z)));
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == ENERGY_CONVERTER_GUI) {
            return new GUIEnergyConverter(player.inventory, (TileEntityEnergyConverter) world.getTileEntity(new BlockPos(x, y, z)));
        }
        return null;
    }
}