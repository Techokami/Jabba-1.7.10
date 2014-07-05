package mcp.mobius.betterbarrels.common.blocks;

import java.util.Random;

import mcp.mobius.betterbarrels.BetterBarrels;
import mcp.mobius.betterbarrels.bspace.BSpaceStorageHandler;
import mcp.mobius.betterbarrels.common.JabbaCreativeTab;
import mcp.mobius.betterbarrels.common.items.upgrades.StructuralLevel;
import mcp.mobius.betterbarrels.common.items.upgrades.UpgradeCore;
import mcp.mobius.betterbarrels.common.items.upgrades.UpgradeSide;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockBarrel extends BlockContainer{

	public static IIcon   text_sidehopper = null;
	public static IIcon   text_siders     = null;
	public static IIcon   text_lock       = null;
	public static IIcon   text_linked     = null;
	public static IIcon   text_locklinked = null;
	
    public BlockBarrel(){
    	super(new Material(MapColor.woodColor) {
    		{
    			this.setBurning();
    			this.setAdventureModeExempt();
    		}
    	});
        this.setHardness(2.0F);
        this.setResistance(5.0F);
        this.setHarvestLevel("axe", 1);
        this.setBlockName("blockbarrel");
		this.setCreativeTab(JabbaCreativeTab.tab);
    }

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
        return new TileEntityBarrel();
	}
	
    @Override    
    public void registerBlockIcons(IIconRegister iconRegister){
		BlockBarrel.text_sidehopper  = iconRegister.registerIcon(BetterBarrels.modid + ":" + "facade_hopper");
		BlockBarrel.text_siders      = iconRegister.registerIcon(BetterBarrels.modid + ":" + "facade_redstone");
		BlockBarrel.text_lock        = iconRegister.registerIcon(BetterBarrels.modid + ":" + "overlay_locked");
		BlockBarrel.text_linked      = iconRegister.registerIcon(BetterBarrels.modid + ":" + "overlay_linked");
		BlockBarrel.text_locklinked  = iconRegister.registerIcon(BetterBarrels.modid + ":" + "overlay_lockedlinked");
		if (StructuralLevel.LEVELS == null) StructuralLevel.createLevelArray();
		for (int i = 0; i < StructuralLevel.LEVELS.length; i++)
			StructuralLevel.LEVELS[i].registerBlockIcons(iconRegister, i);
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack par6ItemStack){
    	// We get the orientation and check if the TE is already properly created.
    	// If so we set the entity value to the correct orientation and set the block meta to 1 to kill the normal block rendering.
    	
        int barrelOrientation = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
        TileEntityBarrel barrelEntity = (TileEntityBarrel)world.getTileEntity(x, y, z);
        
        if (barrelEntity != null){
	        switch (barrelOrientation){
	        	case 0:
	        		barrelEntity.orientation = ForgeDirection.NORTH;
	        		break;
	        	case 1:
	        		barrelEntity.orientation = ForgeDirection.EAST;	        		
	        		break;
	        	case 2:
	        		barrelEntity.orientation = ForgeDirection.SOUTH;	        		
	        		break;
	        	case 3:
	        		barrelEntity.orientation = ForgeDirection.WEST;	        		
	        		break;        		
	        }
	        barrelEntity.sideUpgrades[barrelEntity.orientation.ordinal()] = UpgradeSide.FRONT;
        }
    }
    
    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
    	if (player.capabilities.isCreativeMode && !player.isSneaking() ) {
    		this.onBlockClicked(world, x, y, z, player);
    		return false;
    	} else {
    		return super.removedByPlayer(world, player, x, y, z);
    	}
    }

    @Override
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player){
        if (!world.isRemote){
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            ((TileEntityBarrel)tileEntity).leftClick(player);
        }
    }     
    
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float var7, float var8, float var9){
        if (!world.isRemote){
            TileEntity tileEntity = world.getTileEntity(x, y, z);
            ((TileEntityBarrel)tileEntity).rightClick(player, side);        	
        }
        return true;
    }
    
	private void dropStack(World world, ItemStack stack, int x, int y, int z){
    	Random random = new Random();
        float var10 = random.nextFloat() * 0.8F + 0.1F;
        float var11 = random.nextFloat() * 0.8F + 0.1F;
        EntityItem items;

        
        for (float var12 = random.nextFloat() * 0.8F + 0.1F; stack.stackSize > 0; world.spawnEntityInWorld(items))
        {
            int var13 = random.nextInt(21) + 10;

            if (var13 > stack.stackSize)
            {
                var13 = stack.stackSize;
            }

            stack.stackSize -= var13;
            items = new EntityItem(world, x + var10, y + var11, z + var12, new ItemStack(stack.getItem(), var13, stack.getItemDamage()));
            float var15 = 0.05F;
            items.motionX = (float)random.nextGaussian() * var15;
            items.motionY = (float)random.nextGaussian() * var15 + 0.2F;
            items.motionZ = (float)random.nextGaussian() * var15;

            if (stack.hasTagCompound())
            {
            	items.getEntityItem().setTagCompound((NBTTagCompound)stack.getTagCompound().copy());
            }
        }  		
	}
	
	@Override
	public void onBlockDestroyedByPlayer(World world, int x, int y, int z, int par5) {
		TileEntityBarrel barrelEntity = (TileEntityBarrel)world.getTileEntity(x, y, z);		

		// We drop the structural upgrades
		if ((barrelEntity != null) && (barrelEntity.coreUpgrades.levelStructural > 0)){
    		int currentUpgrade = barrelEntity.coreUpgrades.levelStructural;
    		while (currentUpgrade > 0){
    			ItemStack droppedStack = new ItemStack(BetterBarrels.itemUpgradeStructural, 1, currentUpgrade-1);
    			this.dropStack(world, droppedStack, x, y, z);
    			currentUpgrade -= 1;
    		}
    	}    	
    	
    	// We drop the core upgrades
    	if (barrelEntity != null){
    		for (UpgradeCore core : barrelEntity.coreUpgrades.upgradeList){
				ItemStack droppedStack = new ItemStack(BetterBarrels.itemUpgradeCore, 1, core.ordinal());
 				this.dropStack(world, droppedStack, x, y, z);
    		}
    	}     	
    	
    	// We drop the side upgrades
    	if (barrelEntity != null){
    		for (int i = 0; i < 6; i++){
    			Item upgrade = UpgradeSide.mapItem[barrelEntity.sideUpgrades[i]];
    			if (upgrade != null){
    				ItemStack droppedStack = new ItemStack(upgrade, 1, UpgradeSide.mapMeta[barrelEntity.sideUpgrades[i]]);
    				this.dropStack(world, droppedStack, x, y, z);
    			}
    		}
    	}    	
    	
    	// We drop the stacks
        if ((barrelEntity != null) && (barrelEntity.getStorage().hasItem()) && (!barrelEntity.getLinked()))
        {
        	barrelEntity.updateEntity();
        	int ndroppedstacks = 0;
        	ItemStack droppedstack = barrelEntity.getStorage().getStack();
        	// TODO : is this just an amount limiter to prevent too many items spawning into the world?
        	// limits max number of dropped stacks to 64, perhaps should be limited to 64 * storage upgrade count?
        	while ((droppedstack != null) && (ndroppedstacks <= 64)){ //TODO: shouldn't this be the max stack size of the barrel, not 64?
        		ndroppedstacks += 1;
        		
        		if (droppedstack != null)
        			this.dropStack(world, droppedstack, x, y, z);

        		droppedstack    = barrelEntity.getStorage().getStack();
        	}
        }		

        try{
        	BSpaceStorageHandler.instance().unregisterEnderBarrel(barrelEntity.id);
        } catch (Exception e){
        	BetterBarrels.log.info("Tried to remove the barrel from the index without a valid entity");
        }
	}

	@Override
	public void onBlockHarvested(World world, int x, int y, int z, int par5, EntityPlayer par6EntityPlayer){
		// Note: the part after the OR can be excluded if you wish for instant block destruction with no item drop in creative
		if (!par6EntityPlayer.capabilities.isCreativeMode || (par6EntityPlayer.capabilities.isCreativeMode && par6EntityPlayer.isSneaking())) {
			this.onBlockDestroyedByPlayer(world, x, y, z, par5);
		}
	}

	/* REDSTONE HANDLING */

	@Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side){
		return this.isProvidingWeakPower(world, x, y, z, side);
    }

    @Override
	public boolean canProvidePower(){
    	return true;
    }

    @Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side){
    	TileEntityBarrel barrel = (TileEntityBarrel)world.getTileEntity(x, y, z);
    	return barrel.getRedstonePower(side);
    }

	@Override
	public int getRenderType() {
		BetterBarrels.proxy.checkRenderers();

		return BetterBarrels.blockBarrelRendererID;
	}    
    
    /*
    @Override
    public boolean isBlockNormalCube(World world, int x, int y, int z){
    	return false;
    }
    */
    
    /*
    private boolean isIndirectlyPowered(World par1World, int par2, int par3, int par4)
    {
        int l = par1World.getBlockMetadata(par2, par3, par4);
        return l == 5 && par1World.getIndirectPowerOutput(par2, par3 - 1, par4, 0) ? true : (l == 3 && par1World.getIndirectPowerOutput(par2, par3, par4 - 1, 2) ? true : (l == 4 && par1World.getIndirectPowerOutput(par2, par3, par4 + 1, 3) ? true : (l == 1 && par1World.getIndirectPowerOutput(par2 - 1, par3, par4, 4) ? true : l == 2 && par1World.getIndirectPowerOutput(par2 + 1, par3, par4, 5))));
    }
    */    
}
