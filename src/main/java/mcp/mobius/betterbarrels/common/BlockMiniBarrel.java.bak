package mcp.mobius.betterbarrels.common;

import java.util.List;

import mcp.mobius.betterbarrels.common.items.ItemBarrelSticker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet3Chat;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatMessageComponent;
import net.minecraft.world.World;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.ForgeDirection;

public class BlockMiniBarrel extends BlockBarrel {
	public BlockMiniBarrel(int par1) { 
		super(par1);
		this.setUnlocalizedName("MiniBarrel (WIP)");
		this.setBlockBounds(0.25f, 0.0f, 0.25f, 0.75f, 0.5f, 0.75f);
	}
	
	@Override
	public TileEntity createNewTileEntity(World var1) {
        return new TileEntityMiniBarrel(16);
	}
	
	@Override	
    public boolean isOpaqueCube()
    {
        return false;
    }

	@Override		
    public boolean renderAsNormalBlock()
    {
        return false;
    }
	
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int var6, float var7, float var8, float var9){
    	ItemStack playerStack = player.getHeldItem();

    	TileEntity tileEntity = world.getBlockTileEntity(x, y, z);        
    	
    	if (!world.isRemote)
        {
    		TileEntityBarrel barrel = (TileEntityBarrel)tileEntity; 
    		
    		if (!barrel.storage.canInteract(player.username))
    			((EntityPlayerMP)player).playerNetServerHandler.sendPacketToPlayer(new Packet3Chat(
    					ChatMessageComponent.createFromText("You have no right to do that."), false));	
    		
    		else if ((playerStack == null) && player.isSneaking())
    			barrel.switchGhosting(world);

    		else if (playerStack == null)
    			barrel.rightClick(player);

            else if (playerStack.getItem() instanceof ItemBarrelSticker)
            	barrel.applySticker(world, x, y, z, playerStack, ForgeDirection.getOrientation(var6));
            
            else if (barrel.storage.canInteract(player.username)) 	
            	barrel.rightClick(player);
        }
        return true;
    }
}
