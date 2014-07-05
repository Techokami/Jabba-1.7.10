package mcp.mobius.betterbarrels.client.render;

import java.util.HashMap;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import mcp.mobius.betterbarrels.mod_BetterBarrels;
import mcp.mobius.betterbarrels.client.Coordinates;
import mcp.mobius.betterbarrels.common.TileEntityBarrel;
import mcp.mobius.betterbarrels.common.TileEntityMiniBarrel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityMiniBarrelRenderer extends TileEntityBarrelRenderer {

	protected float zoffset = -0.25F;
	
	@Override
	public void renderTileEntityAt(TileEntity tileEntity, double xpos, double ypos, double zpos, float var8) {
		if (tileEntity instanceof TileEntityBarrel)
        {
			// First, we get the associated block metadata for orientation
			int blockOrientation = ((TileEntityBarrel) tileEntity).blockOrientation;
			TileEntityBarrel barrelEntity = (TileEntityBarrel)tileEntity;
			Coordinates barrelPos = new Coordinates(xpos, ypos, zpos);
			
			boolean hasBlending = GL11.glGetBoolean(GL11.GL_BLEND);
			boolean hasLight    = GL11.glGetBoolean(GL11.GL_LIGHTING);
	    	int   boundTexIndex = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);
			
	        GL11.glDisable(GL11.GL_BLEND);
	        GL11.glDisable(GL11.GL_LIGHTING);  			

	        int textureUpgrade = barrelEntity.upgradeCapacity;
	        
	        if (!mod_BetterBarrels.fullBarrelTexture)
	        	textureUpgrade = 0;
	        
        	for (ForgeDirection forgeSide: ForgeDirection.VALID_DIRECTIONS){
        		int textureIndex = 0;
            	if (this.isItemDisplaySide(forgeSide, blockOrientation))
            		textureIndex = 16*textureUpgrade + 1;  		
            	else if ((forgeSide == ForgeDirection.UP) || (forgeSide == ForgeDirection.DOWN))
            		textureIndex = 16*textureUpgrade;
            	else
            		textureIndex = 16*textureUpgrade + 2;
            	
            	this.setLight(barrelEntity, forgeSide);
            	this.renderBarrelSide(textureIndex, forgeSide, barrelPos, false, false);            	
            	
            	if (this.isItemDisplaySide(forgeSide, blockOrientation))
            		this.renderBarrelSide(3, forgeSide, barrelPos, false, false);
        	}	        
        	
	        for (ForgeDirection forgeSide: ForgeDirection.VALID_DIRECTIONS){					
				this.setLight(barrelEntity, forgeSide);
					
				if (barrelEntity.storage.hasItem() &&  this.isItemDisplaySide(forgeSide, blockOrientation))
				{
					this.renderStackOnBlock(barrelEntity.storage.getItem(), forgeSide, barrelPos, 1.0F, 24.0F, 42.0F);
					String barrelString = this.getBarrelString(barrelEntity);
					this.renderTextOnBlock(barrelString, forgeSide, barrelPos, 0.25F, 32.0F, -33F, 255, 255, 255, 0, true);
					
				}

				if (barrelEntity.storage.isGhosting() && this.isItemDisplaySide(forgeSide, blockOrientation))
					this.renderIconOnBlock(8, forgeSide, barrelPos, 0.25F, 22.0F, 58.0F, 0.01F);				
			}        	
        	
        	if (hasBlending)
        		GL11.glEnable(GL11.GL_BLEND);
        	else
        		GL11.glDisable(GL11.GL_BLEND);

        	if (hasLight)
        		GL11.glEnable(GL11.GL_LIGHTING);
        	else
        		GL11.glDisable(GL11.GL_LIGHTING);
        	
        	GL11.glBindTexture(GL11.GL_TEXTURE_2D, boundTexIndex);
        	
        }
	}

	protected void renderBarrelSide(int index,  ForgeDirection side, Coordinates barrelPos, boolean reverted, boolean transparent){
    	
        float revert = reverted ? -0.9991F : 1F;
        side = reverted ? side.getOpposite() : side;
        
        float size = 8.0F;
        
    	GL11.glPushMatrix();

    	this.texManager.bindTexture(blocksSheetRes);
        
        GL11.glTranslated(barrelPos.x + 0.5F, barrelPos.y + 0.25F, barrelPos.z + 0.5F);     // We align the rendering on the center of the block
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
        GL11.glRotatef(this.getRotationYForSide(side), 0.0F, 1.0F, 0.0F); // We rotate it so it face the right face
        GL11.glRotatef(this.getRotationXForSide(side), 1.0F, 0.0F, 0.0F);
        GL11.glTranslated(-0.25F, -0.25F, -0.25F * revert);
        GL11.glScalef(scale*size, scale*size, 0.0000F);

        this.drawTexturedModalRect(0, 0, 16*(index%16), 16*(index/16), 16, 16);
        GL11.glPopMatrix();    	           
    } 	

    protected void renderStackOnBlock(ItemStack stack, ForgeDirection side, Coordinates barrelPos, float size, double iconPosX, double iconPosY){
    	
    	if (stack == null){return;}

    	GL11.glPushMatrix();

    	GL11.glTranslated(barrelPos.x + 0.5F, barrelPos.y + 0.5F, barrelPos.z + 0.5F); // We align the rendering on the block
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F); 					  				   // We turn the picture upside down (for some reason, axis are inverted)
        GL11.glRotatef(this.getRotationYForSide(side), 0.0F, 1.0F, 0.0F);              // We rotate it so it face the right face
        GL11.glTranslated(-0.5F, -0.5F, -0.25F - 0.01F);
        GL11.glScalef(scale*size, scale*size, -0.0001F);			      // We flatten the rendering and scale it to the right size
        
        GL11.glTranslated(iconPosX/size, iconPosY/size, 0);		  // Finally, we translate the icon itself to the correct position
        
        if (!ForgeHooksClient.renderInventoryItem(this.renderBlocks, this.texManager, stack, true, 0.0F, 0.0F, 0.0F))
        {
            this.renderItem.renderItemIntoGUI(this.renderFont, this.texManager, stack, 0, 0);
        }        
        
        GL11.glPopMatrix();    	 
    }    

    protected void renderTextOnBlock(String renderString, ForgeDirection side, Coordinates barrelPos, float size, double textPosX, double textPosY, int red, int green, int blue, int alpha, boolean centered){

    	if (renderString.equals("") || renderString == null){return;}
    	
    	FontRenderer fontRenderer = this.getFontRenderer();            	
    	int stringWidth = fontRenderer.getStringWidth(renderString);

    	GL11.glPushMatrix();

    	GL11.glTranslated(barrelPos.x + 0.5F, barrelPos.y + 0.5F, barrelPos.z + 0.5F); // We align the rendering on the block
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F); 					  				   // We turn the picture upside down (for some reason, axis are inverted)
        GL11.glRotatef(this.getRotationYForSide(side), 0.0F, 1.0F, 0.0F);              // We rotate it so it face the right face
        GL11.glTranslated(-0.5F, -0.5F, -0.25F - 0.01F);
        GL11.glTranslated(textPosX*this.scale, -textPosY*this.scale, 0.0F);
        GL11.glDepthMask(false);        
        GL11.glScalef(scale*size, scale*size, this.scale*size);			      // We flatten the rendering and scale it to the right size    	
    	
        GL11.glDisable(GL11.GL_LIGHTING);

        int color = (alpha << 24) | (red << 16) | (blue << 8) | green;
        
        if (centered){
        	fontRenderer.drawString(renderString, -stringWidth / 2, 0, color);
        } else {
        	fontRenderer.drawString(renderString, 0, 0, color);
        }
        
        GL11.glDepthMask(true);
        GL11.glPopMatrix();       
    }    

    protected void renderIconOnBlock(int index,  ForgeDirection side, Coordinates barrelPos, float size, double iconPosX, double iconPosY, float zDepth){
    	GL11.glPushMatrix();

    	this.texManager.bindTexture(itemsSheetRes);

    	GL11.glTranslated(barrelPos.x + 0.5F, barrelPos.y + 0.5F, barrelPos.z + 0.5F); // We align the rendering on the block
        GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F); 					  				   // We turn the picture upside down (for some reason, axis are inverted)
        GL11.glRotatef(this.getRotationYForSide(side), 0.0F, 1.0F, 0.0F);              // We rotate it so it face the right face
        GL11.glTranslated(-0.5F, -0.5F, -0.25F - zDepth);
        GL11.glScalef(scale*size, scale*size, 0.0000F);			      // We flatten the rendering and scale it to the right size
        
        GL11.glTranslated(iconPosX/size, iconPosY/size, 0);		  // Finally, we translate the icon itself to the correct position        

        this.drawTexturedModalRect((int)(iconPosX/size), 0, 16*(index%16), 16*(index/16), 16, 16);        
       
        GL11.glPopMatrix();        
    }    
}
