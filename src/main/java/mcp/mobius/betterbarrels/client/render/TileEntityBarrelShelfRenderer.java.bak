package mcp.mobius.betterbarrels.client.render;

import org.lwjgl.opengl.GL11;

import mcp.mobius.betterbarrels.client.Coordinates;
import mcp.mobius.betterbarrels.common.TileEntityBarrel;
import mcp.mobius.betterbarrels.common.TileEntityBarrelShelf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityBarrelShelfRenderer extends TileEntityBaseRenderer {

	private ModelBarrelShelf model = new ModelBarrelShelf();

	protected static float zoffset = -0.50F;
    protected float scale = 1.0F/64.0F;	

	@Override
	public void renderTileEntityAt(TileEntity tileentity, double d0, double d1,	double d2, float f) {
		this.saveState();
		
		TileEntityBarrelShelf ent = (TileEntityBarrelShelf)tileentity;
		
        GL11.glPushMatrix();
        GL11.glTranslatef((float)d0+0.5f, (float)d1-0.5f, (float)d2+0.5f);
        GL11.glRotatef(this.getOrientationAngle(ent.blockOrientation), 0F, 1F, 0F);
        this.texManager.bindTexture(blankRes);
        
        GL11.glPushMatrix();
        model.render();
        GL11.glPopMatrix();
        GL11.glPopMatrix();
        
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LIGHTING); 
        
        this.setLight(ent, ent.blockOrientation);
        
        for (int i = 0; i < 4;  i++){
        	if (ent.storages[i] != null){
        		switch(i){
        		case 0:
        			this.renderIconOnBlock(16, ent.blockOrientation, new Coordinates(d0, d1, d2), 7.26f, 8f, 8f + 124f, -0.001f);
        			break;
        		case 1:
        			this.renderIconOnBlock(16, ent.blockOrientation, new Coordinates(d0, d1, d2), 7.26f, 8f + 124f, 8f + 124f, -0.001f);
        			break;
        		case 2:
        			this.renderIconOnBlock(16, ent.blockOrientation, new Coordinates(d0, d1, d2), 7.26f, 8f, 8f, -0.001f);
        			break;
        		case 3:
        			this.renderIconOnBlock(16, ent.blockOrientation, new Coordinates(d0, d1, d2), 7.26f, 8f + 124f, 8f, -0.001f);
        			break;
        		}
        	}
        		
        }

        this.loadState();
	}
	
    protected float getOrientationAngle(ForgeDirection side){
    	int sideRotation[]  = {0,0,3,1,0,2};
    	return sideRotation[side.ordinal()] * 90F;
    }
}
