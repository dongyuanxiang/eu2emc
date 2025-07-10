package com.dongyuanxing.eu2emc;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import moze_intel.projecte.api.ProjectEAPI;
import moze_intel.projecte.api.capabilities.IKnowledgeProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.MinecraftForge;
import scala.Int;

public class TileEntityEnergyConverter extends TileEntity implements ITickable, IEnergySink {
    // 添加volatile修饰符确保多线程可见性
    private volatile double storedEMC = 0;
    private volatile long totalEUConsumed = 0;
    private volatile double totalEMCProduced = 0;
    private volatile int currentEU = 0;
    private boolean isAdded = false;

    private static final double EU_TO_EMC_RATIO = 0.08;

    //默认模式
    private int currentMode=5;

    // 添加数据同步标记
    private boolean needsDataSync = false;

    @Override
    public void update() {
        if (!world.isRemote) {
            // 每tick重置当前EU
            currentEU = 0;

            // 首次加入能源网络
            if (!isAdded) {
                EnergyTileLoadEvent event = new EnergyTileLoadEvent(this);
                MinecraftForge.EVENT_BUS.post(event);
                isAdded = true;
            }

            // 数据变化时标记需要同步
            if (needsDataSync) {
                markDirty();
                IBlockState state = world.getBlockState(pos);
                world.notifyBlockUpdate(pos, state, state, 3); // 通知客户端更新
                needsDataSync = false;
            }
        }
    }

    // 重写数据同步方法
    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (!world.isRemote) {
            needsDataSync = true;
        }
    }

    @Override
    public void invalidate() {
        super.invalidate();
        if (!world.isRemote && isAdded) {
            EnergyTileUnloadEvent event = new EnergyTileUnloadEvent(this);
            MinecraftForge.EVENT_BUS.post(event);
            isAdded = false;
        }
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        if (!world.isRemote && isAdded) {
            EnergyTileUnloadEvent event = new EnergyTileUnloadEvent(this);
            MinecraftForge.EVENT_BUS.post(event);
            isAdded = false;
        }
    }

    public void toggleCurrentMode(){
        currentMode = (currentMode + 1) % 6;
        System.out.println(currentMode);
        needsDataSync = true;
        markDirty();
    }

    public int getCurrentMode() {
        return currentMode;
    }

    public double getCurrentModeLimit() {
        switch (currentMode) {
            case 0:
                return 0;
            case 1:
                return 32;
            case 2:
                return 128;
            case 3:
                return 512;
            case 4:
                return 2048;
            default:
                return Double.MAX_VALUE;
        }
    }

    @Override
    public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing side) {
        return true;
    }

    @Override
    public double injectEnergy(EnumFacing direction, double amount, double voltage) {// 限制输入速率
        double limit = getCurrentModeLimit();
        if (limit <= 0){
            return amount;
        }

        double used = Math.min(amount, limit);

        double emcProduced = used * EU_TO_EMC_RATIO;

        storedEMC += emcProduced;
        totalEUConsumed += (long) used;
        totalEMCProduced += emcProduced;
        currentEU += (int) used;

        // 标记需要同步
        needsDataSync = true;
        markDirty();
        return amount - used;
    }

    // 收集EMC方法
    public int collectEMC(EntityPlayer player) {
        int amount = (int) Math.floor(storedEMC);
        if (amount > 0 && !world.isRemote) {
            storedEMC -= amount;
            // 使用ProjectE API添加EMC
            IKnowledgeProvider knowledge = player.getCapability(ProjectEAPI.KNOWLEDGE_CAPABILITY, null);
            if (knowledge != null) {
                knowledge.setEmc(knowledge.getEmc() + ((long)(amount)));
                knowledge.sync((EntityPlayerMP) player);
            }
            // 标记需要同步
            needsDataSync = true;
            markDirty();
            return amount;
        }
        return 0;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        currentMode = compound.getInteger("CurrentMode");
        storedEMC = compound.getDouble("StoredEMC");
        totalEUConsumed = compound.getLong("TotalEU");
        totalEMCProduced = compound.getDouble("TotalEMC");
        currentEU = compound.getInteger("CurrentEU");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("CurrentMode", currentMode);
        compound.setDouble("StoredEMC", storedEMC);
        compound.setLong("TotalEU", totalEUConsumed);
        compound.setDouble("TotalEMC", totalEMCProduced);
        compound.setInteger("CurrentEU", currentEU);
        return compound;
    }

    // Getter方法
    public double getStoredEMC() { return storedEMC; }
    public long getTotalEUConsumed() { return totalEUConsumed; }
    public double getTotalEMCProduced() { return totalEMCProduced; }
    public int getCurrentEU() { return currentEU; }



    @Override
    public double getDemandedEnergy() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getSinkTier() {
        return 4; // EV层级
    }

}