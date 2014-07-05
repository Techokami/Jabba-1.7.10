package mcp.mobius.betterbarrels.common;


import org.lwjgl.util.Point;

import mcp.mobius.betterbarrels.mod_BetterBarrels;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockBarrelShelf extends BlockContainer {

	private ForgeDirection blockOrientation = ForgeDirection.UNKNOWN;	
	
	public BlockBarrelShelf(int par1) {
        super(par1, Material.wood);
        this.setHardness(2.0F);
        this.setResistance(5.0F);
        this.setUnlocalizedName("Barrel Shelf (WIP)");
		this.setCreativeTab(CreativeTabs.tabBlock);
	}

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack par6ItemStack) 
    {
		//int orientationFlagField = 0;
        this.blockOrientation = this.getBlockOrientation(entity);
        //orientationFlagField = (1 << (this.barrelOrientation.ordinal() - 2));
        //world.setBlockMetadataWithNotify(x, y, z, orientationFlagField, 1 & 2);
    }	
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		
		if (world.isRemote)
			return new TileEntityBarrelShelf(ForgeDirection.NORTH);
		else
			return new TileEntityBarrelShelf(this.blockOrientation);
		
		//	this.blockOrientation = this.getBlockOrientation(Minecraft.getMinecraft().thePlayer);
		
		
	}

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int var6, float var7, float var8, float var9){
    	ItemStack playerStack    = player.getHeldItem();
    	TileEntityBarrelShelf te = (TileEntityBarrelShelf)world.getBlockTileEntity(x, y, z);        
    	
    	if (!world.isRemote)
    		te.rightClicked(world, player, this.getRaytracedSlot(te));

    	return true;
    }
	
	@Override
	public boolean renderAsNormalBlock(){
		return false;
	}

	@Override
	public boolean isOpaqueCube(){
		return false;
	}

	@Override
	public int getRenderType(){
		return -1;
	}
	
	@Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
    {
		//System.out.printf("%s\n", Minecraft.getMinecraft().objectMouseOver.hitVec);
		
		int slot = this.getRaytracedSlot(world, x, y, z); 
		
		if ( slot == -1 )
			return super.getSelectedBoundingBoxFromPool(world, x, y, z);
		else {
			int    side = Minecraft.getMinecraft().objectMouseOver.sideHit;
			float  xoffset = 15.5f/32f * (slot%2);
			float  yoffset = 15.5f/32f * (slot/2);
			
			
			switch(side){
			case 2:
				return AxisAlignedBB.getAABBPool().getAABB(x + 31f/32f - xoffset, y + 1f/32f  + yoffset, z, x + 16.5f/32f - xoffset, y + 15.5f/32f + yoffset, z + 31f/32f);
			case 3:
				return AxisAlignedBB.getAABBPool().getAABB(x + 1.1f/32f  + xoffset, y + 1.1f/32f  + yoffset, z + 1, x + 15.4f/32f + xoffset, y + 15.4f/32f + yoffset, z + 1f/32f);	
			case 4:
				return AxisAlignedBB.getAABBPool().getAABB(x, y + 1.1f/32f  + yoffset, z + 1.1f/32f  + xoffset, x + 31f/32f, y + 15.4f/32f + yoffset, z + 15.4f/32f + xoffset);
			case 5:
				return AxisAlignedBB.getAABBPool().getAABB(x - 1f/32f, y + 1f/32f  + yoffset, z + 31f/32f - xoffset, x +1, y + 15.5f/32f + yoffset, z + 16.5f/32f - xoffset);				
			}
			
		}
			//return AxisAlignedBB.getAABBPool().getAABB((double)x + this.minX, (double)y + this.minY, (double)z + this.minZ, (double)x + this.maxX, (double)y + this.maxY, (double)z + this.maxZ);
		return super.getSelectedBoundingBoxFromPool(world, x, y, z);
    }	
	
	private Vec3 getRaytracingFaceCoordinates(){
		MovingObjectPosition raytrace = Minecraft.getMinecraft().objectMouseOver;
		Vec3 hitVec = Vec3.createVectorHelper(raytrace.hitVec.xCoord - raytrace.blockX, raytrace.hitVec.yCoord - raytrace.blockY,  raytrace.hitVec.zCoord - raytrace.blockZ);

		switch(raytrace.sideHit){
		case 0:
			return Vec3.createVectorHelper(      hitVec.xCoord, 1.0 - hitVec.zCoord, 0.0);		
		case 1:
			return Vec3.createVectorHelper(      hitVec.xCoord, 1.0 - hitVec.zCoord, 0.0);
		case 2:
			return Vec3.createVectorHelper(1.0 - hitVec.xCoord,       hitVec.yCoord, 0.0);
		case 3:
			return Vec3.createVectorHelper(      hitVec.xCoord,       hitVec.yCoord, 0.0);
		case 4:
			return Vec3.createVectorHelper(      hitVec.zCoord,       hitVec.yCoord, 0.0);
		case 5:
			return Vec3.createVectorHelper(1.0 - hitVec.zCoord,       hitVec.yCoord, 0.0);
		}		

		return null;
	}
	
	private Vec3 getRaytracingGlobalCoordinates(int blockX, int blockY, int blockZ, int side, double x, double y){
		//MovingObjectPosition raytrace = Minecraft.getMinecraft().objectMouseOver;
		
		switch(side){
		case 2:
			return Vec3.createVectorHelper(blockX - x + 1, y + blockY, blockZ);
		case 3:
			return Vec3.createVectorHelper(blockX + x,     y + blockY, blockZ + 1);
		case 4:
			return Vec3.createVectorHelper(blockX,         y + blockY, blockZ + x);
		case 5:
			return Vec3.createVectorHelper(blockX + 1,     y + blockY, blockZ - x + 1);
		}		

		return null;		
	}

	private int getRaytracedSlot(World world, int x, int y, int z){
		TileEntityBarrelShelf te = (TileEntityBarrelShelf)world.getBlockTileEntity(x, y, z);
		return this.getRaytracedSlot(te);
	}	
	
	private int getRaytracedSlot(TileEntityBarrelShelf te){
		if(Minecraft.getMinecraft().objectMouseOver.sideHit != te.blockOrientation.ordinal()) return -1;
		Vec3 hitPoint = this.getRaytracingFaceCoordinates();
		
		
		if ((hitPoint.xCoord > 1f/32f) && (hitPoint.xCoord < 15.5f/32f)){
			if ((hitPoint.yCoord > 1f/32f) && (hitPoint.yCoord < 15.5f/32f))
				return 0;
			if ((hitPoint.yCoord > 16.5f/32f) && (hitPoint.yCoord < 31f/32f))
				return 2;
		}
		if ((hitPoint.xCoord > 16.5f/32f) && (hitPoint.xCoord < 31f/32f)){
			if ((hitPoint.yCoord > 1f/32f) && (hitPoint.yCoord < 15.5f/32f))
				return 1;
			if ((hitPoint.yCoord > 16.5f/32f) && (hitPoint.yCoord < 31f/32f))
				return 3;			
		}		
		
		return -1;
	}

	private ForgeDirection getBlockOrientation(EntityLivingBase entity){
        int playerOrientation    = MathHelper.floor_double((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;	
        
        switch (playerOrientation){
    	case 0:
    		return ForgeDirection.NORTH;
    	case 1:
    		return ForgeDirection.EAST;
    	case 2:
    		return ForgeDirection.SOUTH;
    	case 3:
    		return ForgeDirection.WEST;
        }
        return ForgeDirection.UNKNOWN;
	}
}
