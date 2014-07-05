package mcp.mobius.betterbarrels.common;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import mcp.mobius.betterbarrels.mod_BetterBarrels;
import mcp.mobius.betterbarrels.server.BSpaceStorageHandler;
import mcp.mobius.betterbarrels.server.SaveHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityBarrelShelf extends TileEntity {

	public  ForgeDirection blockOrientation;
	public  StorageLocal[] storages = new StorageLocal[4];
	
	private boolean dirty = false;
	private int     tickSinceLastUpdate = 0;
	private int     barrelSize = 16;
	
	public TileEntityBarrelShelf(){
		this.blockOrientation = ForgeDirection.UNKNOWN;
	}	
	
	public TileEntityBarrelShelf(ForgeDirection blockOrientation){
		this.blockOrientation = blockOrientation;
	}

	@Override
	public void updateEntity() {
		tickSinceLastUpdate += 1;
		if (tickSinceLastUpdate >= 20){
			this.dirty = true;
			tickSinceLastUpdate = 0;
		}
		
		if (this.dirty){
			
			PacketDispatcher.sendPacketToAllAround(this.xCoord, this.yCoord, this.zCoord, 50.0, this.worldObj.provider.dimensionId, this.getDescriptionPacket());			
			this.dirty = false;
		}
	}
	
	/* INTERACTION */
	public void rightClicked(World world, EntityPlayer player, int slot){
		ItemStack playerStack    = player.getHeldItem();
		
		if ((playerStack == null) && (this.storages[slot] == null)) return;
		if (slot == -1) return;
		
		if ((this.storages[slot] == null) && (playerStack.isItemEqual(new ItemStack( mod_BetterBarrels.blockMiniBarrel )))){
			this.storages[slot] = new StorageLocal(barrelSize);
			playerStack.stackSize -= 1;
			this.dirty = true;
		}

	}
	
	/* SERIALIZATION */
	
    @Override	
    public void writeToNBT(NBTTagCompound NBTTag)
    {
    	this.writeToNBT(NBTTag, true);
    }	
	
	public void writeToNBT(NBTTagCompound NBTTag, boolean full){
		if (full)
			super.writeToNBT(NBTTag);
		
        NBTTag.setInteger("orientation", this.blockOrientation.ordinal());
        
        for (int i = 0; i < 4; i++)
        	if (this.storages[i] != null)
        		NBTTag.setCompoundTag(String.format("storage_%02d", i), this.storages[i].writeTagCompound());
    }

    @Override
    public void readFromNBT(NBTTagCompound NBTTag){
    	this.readFromNBT(NBTTag, true);
    }	
	
    private void readFromNBT(NBTTagCompound NBTTag, boolean full){
		if (full)
			super.readFromNBT(NBTTag);
		
		this.blockOrientation = ForgeDirection.getOrientation(NBTTag.getInteger("orientation"));
		
        for (int i = 0; i < 4; i++){
        	if (NBTTag.hasKey(String.format("storage_%02d", i))){
        		if (this.storages[i] == null)
        			this.storages[i] = new StorageLocal(barrelSize);
        		this.storages[i].readTagCompound(NBTTag.getCompoundTag(String.format("storage_%02d", i)));
        	}
        	// Should handle removing barrels here too.
        }
		
		this.dirty = true;
    }
	
    @Override
    public Packet132TileEntityData getDescriptionPacket()
    {
        NBTTagCompound var1 = new NBTTagCompound();
        this.writeToNBT(var1, false);
        return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 0, var1);
    }	
    
    @Override
    public void onDataPacket(INetworkManager net, Packet132TileEntityData pkt) {
        this.readFromNBT(pkt.data, false);
    }      
}
