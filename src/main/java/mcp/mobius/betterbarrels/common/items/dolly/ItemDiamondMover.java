package mcp.mobius.betterbarrels.common.items.dolly;

import mcp.mobius.betterbarrels.BetterBarrels;
import mcp.mobius.betterbarrels.common.JabbaCreativeTab;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemDiamondMover extends ItemBarrelMover {
	
	public ItemDiamondMover(){
		super();
		this.setUnlocalizedName("dolly.diamond.empty");
        this.setMaxDamage(6); 
        this.setCreativeTab(JabbaCreativeTab.tab);
	}
	
	protected boolean canPickSpawners(){
		return true;
	}	
	
	@Override	
    public String getUnlocalizedName(ItemStack stack)
    {   
		if (stack.getItemDamage() == 0)
			return "item.dolly.diamond.empty";
		else
			return "item.dolly.diamond.full";
    }	

    @Override    
    public void registerIcons(IIconRegister par1IconRegister)
    {
    	this.itemIcon    = par1IconRegister.registerIcon(BetterBarrels.modid + ":" + "dolly_diamond_empty");
    	this.text_empty  = this.itemIcon;
    	this.text_filled = par1IconRegister.registerIcon(BetterBarrels.modid + ":" + "dolly_diamond_filled");
    } 	
	
	@Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
		//if (world.isRemote){return false;}
		
		if (!world.isRemote && (!stack.hasTagCompound() || !stack.getTagCompound().hasKey("Container"))){
			return this.pickupContainer(stack, player, world, x, y, z);
		}
		
		if (!world.isRemote && (stack.hasTagCompound() && stack.getTagCompound().hasKey("Container") && stack.getTagCompound().getCompoundTag("Container").getBoolean("isSpawner"))){
			boolean ret = this.placeContainer(stack, player, world, x, y, z, side); 
			if (ret) 
				stack.setItemDamage(stack.getItemDamage() + 1);
			if (stack.getItemDamage() >= stack.getMaxDamage())
				stack.stackSize -= 1;
			return ret;
		}
		
		else if (!world.isRemote && (stack.hasTagCompound() && stack.getTagCompound().hasKey("Container"))){
			boolean ret = this.placeContainer(stack, player, world, x, y, z, side); 
		}		
		
        return false;
    }	
	
}
