package com.dongyuanxing.eu2emc;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

public class BlockEnergyConverter extends Block implements ITileEntityProvider {

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    public BlockEnergyConverter() {
        super(Material.ROCK);
        setUnlocalizedName("converter");
        setRegistryName(new ResourceLocation(EU2EMCConverter.MODID, "converter"));
        setCreativeTab(CreativeTabs.REDSTONE);
        setHardness(3.0F);
        setResistance(5.0F);
        setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));


    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing,
                                            float hitX, float hitY, float hitZ,
                                            int meta, EntityLivingBase placer, EnumHand hand) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
    }

//    @Override
//    public IBlockState getStateFromMeta(int meta) {
//        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta));
//    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getHorizontalIndex();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }


    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityEnergyConverter();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
                                    EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            player.openGui(EU2EMCConverter.instance, GuiHandler.ENERGY_CONVERTER_GUI, world,
                    pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

//    @Override
//    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
//        return Item.getItemFromBlock(this);
//    }
//
//    @Override
//    public ItemStack getItem(World worldIn, BlockPos pos, IBlockState state) {
//        return new ItemStack(this);
//    }
//
//    // 添加渲染层设置
//    @Override
//    @SideOnly(Side.CLIENT)
//    public BlockRenderLayer getBlockLayer() {
//        return BlockRenderLayer.SOLID;
//    }
//
//    @Override
//    public boolean isOpaqueCube(IBlockState state) {
//        return true;
//    }
//
//    @Override
//    public boolean isFullCube(IBlockState state) {
//        return true;
//    }

}