package mcp.mobius.betterbarrels.common.blocks.logic;

import mcp.mobius.betterbarrels.common.blocks.TileEntityBarrel;
import mcp.mobius.betterbarrels.common.items.upgrades.UpgradeSide;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class LogicHopper {

	public static LogicHopper _instance = new LogicHopper();
	private LogicHopper(){}
	public static LogicHopper instance() { return LogicHopper._instance; }

	public boolean run(TileEntityBarrel barrel){
		boolean transaction = false;
		ItemStack  stack = barrel.getStorage().getStackInSlot(1);
		if (stack == null || stack.stackSize == 0) return false;
		
		for (ForgeDirection side : ForgeDirection.VALID_DIRECTIONS){
			if (barrel.sideUpgrades[side.ordinal()] == UpgradeSide.HOPPER){
				//System.out.printf("%s\n", side);
				
				TileEntity targetEntity = barrel.getWorldObj().getTileEntity(barrel.xCoord + side.offsetX, barrel.yCoord + side.offsetY, barrel.zCoord + side.offsetZ);
				if ((targetEntity instanceof IInventory) && !this.isFull((IInventory)targetEntity, side.getOpposite())){
					
					stack = barrel.getStorage().getStackInSlot(1);
					if(stack != null && stack.stackSize > 0 && this.pushItemToInventory((IInventory)targetEntity, side.getOpposite(), stack)){
						barrel.getStorage().markDirty();
						transaction = true;
						targetEntity.markDirty();
					}
				}
			}
		}
		return transaction;		
	}


	
	private boolean isFull(IInventory inventory, ForgeDirection side){
    	if (inventory instanceof ISidedInventory  && side.ordinal() > -1){
    	    ISidedInventory sinv = (ISidedInventory)inventory;
    	    int[] islots = sinv.getAccessibleSlotsFromSide(side.ordinal());
    	    
    	    for (int index : islots){
    	    	ItemStack is = sinv.getStackInSlot(index); 
    	    	if ( is == null || is.stackSize != is.getMaxStackSize()) return false;
    	    }
    	    return true;
    	    
    	} else {
    		for (int index = 0; index < inventory.getSizeInventory(); index++){
    			ItemStack is = inventory.getStackInSlot(index);
    	    	if ( is == null || is.stackSize != is.getMaxStackSize()) return false;    			
    		}
    		return true;
    	}
	}
	
	private boolean pushItemToInventory(IInventory inventory, ForgeDirection side, ItemStack stack){
    	if (inventory instanceof ISidedInventory  && side.ordinal() > -1){
    	    ISidedInventory sinv = (ISidedInventory)inventory;
    	    int[] islots = sinv.getAccessibleSlotsFromSide(side.ordinal()); 
    	    
    	    for (int slot : islots){
    	    	if (!sinv.canInsertItem(slot, stack, side.ordinal())) continue;
    	    	ItemStack targetStack = sinv.getStackInSlot(slot);
    	    	
    	    	if (targetStack == null){
    	    		targetStack = stack.copy();
    	    		targetStack.stackSize = 1;
    	    		sinv.setInventorySlotContents(slot, targetStack);
    	    		stack.stackSize -= 1;
    	    		return true;
    	    		
    	    	} else if (targetStack.isItemEqual(stack) && targetStack.stackSize < targetStack.getMaxStackSize()) {
    	    		targetStack.stackSize += 1;
    	    		stack.stackSize -= 1;
    	    		return true;
    	    	}
    	    }
    	    
    	    
    	} else {
    		int nslots = inventory.getSizeInventory();
    		for (int slot = 0; slot < nslots; slot++){
    	    	ItemStack targetStack = inventory.getStackInSlot(slot);
    	    	
    	    	if (targetStack == null){
    	    		targetStack = stack.copy();
    	    		targetStack.stackSize = 1;
    	    		inventory.setInventorySlotContents(slot, targetStack);
    	    		stack.stackSize -= 1;
    	    		return true;
    	    		
    	    	} else if (targetStack.isItemEqual(stack) && targetStack.stackSize < targetStack.getMaxStackSize()) {
    	    		targetStack.stackSize += 1;
    	    		stack.stackSize -= 1;
    	    		return true;
    	    	}    			
    		}
    		
    	}

    	return false;
	}
}
