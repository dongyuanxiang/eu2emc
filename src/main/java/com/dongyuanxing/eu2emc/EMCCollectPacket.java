package com.dongyuanxing.eu2emc;



import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class EMCCollectPacket implements IMessage {
    private BlockPos pos;

    public EMCCollectPacket() {}
    public EMCCollectPacket(BlockPos pos) { this.pos = pos; }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.pos = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
    }

    public static class Handler implements IMessageHandler<EMCCollectPacket, IMessage> {
        @Override
        public IMessage onMessage(EMCCollectPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            player.getServerWorld().addScheduledTask(() -> {
                TileEntity te = player.world.getTileEntity(message.pos);
                if (te instanceof TileEntityEnergyConverter) {
                    ((TileEntityEnergyConverter) te).collectEMC(player);
                }
            });
            return null;
        }
    }
}