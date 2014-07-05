package mcp.mobius.betterbarrels.client.render;

import mcp.mobius.betterbarrels.BetterBarrels;
import mcp.mobius.betterbarrels.common.blocks.BlockBarrel;
import mcp.mobius.betterbarrels.common.blocks.TileEntityBarrel;
import mcp.mobius.betterbarrels.common.items.upgrades.StructuralLevel;
import mcp.mobius.betterbarrels.common.items.upgrades.UpgradeSide;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;

public class BlockBarrelRenderer implements ISimpleBlockRenderingHandler {

	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		BlockBarrel barrel      = (BlockBarrel)block;
		Tessellator tessellator = Tessellator.instance;
		
		IIcon iconSide, iconTop, iconLabel, iconLabelTop;
		iconSide     = StructuralLevel.LEVELS[0].getIconSide();
		iconTop      = StructuralLevel.LEVELS[0].getIconTop();
		iconLabel    = StructuralLevel.LEVELS[0].getIconLabel();
		iconLabelTop = StructuralLevel.LEVELS[0].getIconLabelTop();		

		double minXSide = iconSide.getMinU();
		double maxXSide = iconSide.getMaxU();
		double minYSide = iconSide.getMinV();
		double maxYSide = iconSide.getMaxV();

		double minXTop = iconTop.getMinU();
		double maxXTop = iconTop.getMaxU();
		double minYTop = iconTop.getMinV();
		double maxYTop = iconTop.getMaxV();

		double minXLabel = iconLabel.getMinU();
		double maxXLabel = iconLabel.getMaxU();
		double minYLabel = iconLabel.getMinV();
		double maxYLabel = iconLabel.getMaxV();	
		
		double xMin = 0, xMax = 1;
		double yMin = 0, yMax = 1;
		double zMin = 0, zMax = 1;
		
		GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
		GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
		
		/* BOTTOM */
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, -1.0F, 0.0F);
		tessellator.addVertexWithUV(xMin, yMin, zMax, minXTop, minYTop);
		tessellator.addVertexWithUV(xMin, yMin, zMin, minXTop, maxYTop);
		tessellator.addVertexWithUV(xMax, yMin, zMin, maxXTop, maxYTop);
		tessellator.addVertexWithUV(xMax, yMin, zMax, maxXTop, minYTop);
		tessellator.draw();	
		
		/* TOP */
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 1.0F, 0.0F);
		tessellator.addVertexWithUV(xMin, yMax, zMin, minXTop, minYTop);
		tessellator.addVertexWithUV(xMin, yMax, zMax, minXTop, maxYTop);
		tessellator.addVertexWithUV(xMax, yMax, zMax, maxXTop, maxYTop);
		tessellator.addVertexWithUV(xMax, yMax, zMin, maxXTop, minYTop);
		tessellator.draw();	
		
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, -1.0F);
		tessellator.addVertexWithUV(xMax, yMax, zMin, minXSide, minYSide);
		tessellator.addVertexWithUV(xMax, yMin, zMin, minXSide, maxYSide);
		tessellator.addVertexWithUV(xMin, yMin, zMin, maxXSide, maxYSide);
		tessellator.addVertexWithUV(xMin, yMax, zMin, maxXSide, minYSide);
		tessellator.draw();		
		
		tessellator.startDrawingQuads();
		tessellator.setNormal(0.0F, 0.0F, 1.0F);
		tessellator.addVertexWithUV(xMin, yMax, zMax, minXSide, minYSide);
		tessellator.addVertexWithUV(xMin, yMin, zMax, minXSide, maxYSide);
		tessellator.addVertexWithUV(xMax, yMin, zMax, maxXSide, maxYSide);
		tessellator.addVertexWithUV(xMax, yMax, zMax, maxXSide, minYSide);
		tessellator.draw();
		
		/* FRONT */
		tessellator.startDrawingQuads();
		tessellator.setNormal(-1.0F, 0.0F, 0.0F);
		tessellator.addVertexWithUV(xMin, yMin, zMax, minXLabel, maxYLabel);
		tessellator.addVertexWithUV(xMin, yMax, zMax, minXLabel, minYLabel);
		tessellator.addVertexWithUV(xMin, yMax, zMin, maxXLabel, minYLabel);
		tessellator.addVertexWithUV(xMin, yMin, zMin, maxXLabel, maxYLabel);
		tessellator.draw();	
		
		/* BACK */
		tessellator.startDrawingQuads();
		tessellator.setNormal(1.0F, 0.0F, 0.0F);
		tessellator.addVertexWithUV(xMax, yMin, zMin, minXSide, minYSide);
		tessellator.addVertexWithUV(xMax, yMax, zMin, minXSide, maxYSide);
		tessellator.addVertexWithUV(xMax, yMax, zMax, maxXSide, maxYSide);
		tessellator.addVertexWithUV(xMax, yMin, zMax, maxXSide, minYSide);
		tessellator.draw();			
	}
	
	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block tile, int modelId, RenderBlocks renderer) {
		if (renderer.hasOverrideBlockTexture())
		{ // usually: block is being broken
			renderer.renderFaceYNeg(tile, x, y, z, null);
			renderer.renderFaceYPos(tile, x, y, z, null);
			renderer.renderFaceZNeg(tile, x, y, z, null);
			renderer.renderFaceZPos(tile, x, y, z, null);
			renderer.renderFaceXNeg(tile, x, y, z, null);
			renderer.renderFaceXPos(tile, x, y, z, null);
			return true;
		}		
		
		int worldHeight = world.getHeight();
		BlockBarrel block       = (BlockBarrel)tile;	
		TileEntityBarrel barrel = (TileEntityBarrel)world.getTileEntity(x, y, z);
		ItemStack        stack  = barrel.getStorage().getItem();
		Tessellator tessellator = Tessellator.instance;
		
		IIcon iconSide, iconTop, iconLabel, iconLabelTop, iconSideHopper, iconSideRS, iconLock, iconLinked, iconLockLinked;
		IIcon iconStack = null;
		int  levelStructural = barrel.coreUpgrades.levelStructural;
		iconSide       = StructuralLevel.LEVELS[levelStructural].getIconSide();
		iconTop        = StructuralLevel.LEVELS[levelStructural].getIconTop();
		iconLabel      = StructuralLevel.LEVELS[levelStructural].getIconLabel();
		iconLabelTop   = StructuralLevel.LEVELS[levelStructural].getIconLabelTop();
		iconSideHopper = BlockBarrel.text_sidehopper;
		iconSideRS     = BlockBarrel.text_siders;
		iconLock       = BlockBarrel.text_lock;
		iconLinked     = BlockBarrel.text_linked;
		iconLockLinked = BlockBarrel.text_locklinked;
		
		/*
		if (barrel.storage.hasItem())
			iconStack = stack.getItem().getIconFromDamage(stack.getItemDamage());
		*/
		
		double minXSide = iconSide.getMinU();
		double maxXSide = iconSide.getMaxU();
		double minYSide = iconSide.getMinV();
		double maxYSide = iconSide.getMaxV();

		double minXTop = iconTop.getMinU();
		double maxXTop = iconTop.getMaxU();
		double minYTop = iconTop.getMinV();
		double maxYTop = iconTop.getMaxV();

		double minXLabel = iconLabel.getMinU();
		double maxXLabel = iconLabel.getMaxU();
		double minYLabel = iconLabel.getMinV();
		double maxYLabel = iconLabel.getMaxV();			
		
		double minXLabelTop = iconLabelTop.getMinU();
		double maxXLabelTop = iconLabelTop.getMaxU();
		double minYLabelTop = iconLabelTop.getMinV();
		double maxYLabelTop = iconLabelTop.getMaxV();		
		
		double minXSideHopper = iconSideHopper.getMinU();
		double maxXSideHopper = iconSideHopper.getMaxU();
		double minYSideHopper = iconSideHopper.getMinV();
		double maxYSideHopper = iconSideHopper.getMaxV();		
	
		double minXSideRS = iconSideRS.getMinU();
		double maxXSideRS = iconSideRS.getMaxU();
		double minYSideRS = iconSideRS.getMinV();
		double maxYSideRS = iconSideRS.getMaxV();		
		
		double minXLock = iconLock.getMinU();
		double maxXLock = iconLock.getMaxU();
		double minYLock = iconLock.getMinV();
		double maxYLock = iconLock.getMaxV();	
		
		double minXLinked = iconLinked.getMinU();
		double maxXLinked = iconLinked.getMaxU();
		double minYLinked = iconLinked.getMinV();
		double maxYLinked = iconLinked.getMaxV();		

		double minXLockLinked = iconLockLinked.getMinU();
		double maxXLockLinked = iconLockLinked.getMaxU();
		double minYLockLinked = iconLockLinked.getMinV();
		double maxYLockLinked = iconLockLinked.getMaxV();			
		
		double minXStack = iconStack != null ? iconStack.getMinU() : 0;
		double maxXStack = iconStack != null ? iconStack.getMaxU() : 0;
		double minYStack = iconStack != null ? iconStack.getMinV() : 0;
		double maxYStack = iconStack != null ? iconStack.getMaxV() : 0;		
		
		double[] minX = new double[6];
		double[] maxX = new double[6];
		double[] minY = new double[6];
		double[] maxY = new double[6];
		
		boolean[] hasOverlay = new boolean[6]; 
		double[] minOverlayX = new double[6];
		double[] maxOverlayX = new double[6];
		double[] minOverlayY = new double[6];
		double[] maxOverlayY = new double[6];		
		
		for (int side = 0; side < 6; side++){
			if ((side == 0 || side == 1) && (barrel.sideUpgrades[side] == UpgradeSide.STICKER)){
				minX[side] = minXLabelTop;
				maxX[side] = maxXLabelTop;
				minY[side] = minYLabelTop;
				maxY[side] = maxYLabelTop;				
			}
			else if ((side == 0 || side == 1) && (barrel.sideUpgrades[side] != UpgradeSide.STICKER)){
				minX[side] = minXTop;
				maxX[side] = maxXTop;
				minY[side] = minYTop;
				maxY[side] = maxYTop;				
			}	
			else if (barrel.sideUpgrades[side] == UpgradeSide.FRONT || barrel.sideUpgrades[side] == UpgradeSide.STICKER){
				minX[side] = minXLabel;
				maxX[side] = maxXLabel;
				minY[side] = minYLabel;
				maxY[side] = maxYLabel;
			}
			else {
				minX[side] = minXSide;
				maxX[side] = maxXSide;
				minY[side] = minYSide;
				maxY[side] = maxYSide;				
			}
			
			if (barrel.sideUpgrades[side] == UpgradeSide.HOPPER){
				hasOverlay[side]  = true;
				minOverlayX[side] = minXSideHopper;
				maxOverlayX[side] = maxXSideHopper;
				minOverlayY[side] = minYSideHopper;
				maxOverlayY[side] = maxYSideHopper;					
			}
			else if (barrel.sideUpgrades[side] == UpgradeSide.REDSTONE){
				hasOverlay[side]  = true;
				minOverlayX[side] = minXSideRS;
				maxOverlayX[side] = maxXSideRS;
				minOverlayY[side] = minYSideRS;
				maxOverlayY[side] = maxYSideRS;					
			}		
			else if ( barrel.getStorage().isGhosting() && barrel.getLinked() && (barrel.sideUpgrades[side] == UpgradeSide.FRONT || barrel.sideUpgrades[side] == UpgradeSide.STICKER)){
				hasOverlay[side]  = true;
				minOverlayX[side] = maxXLockLinked;
				maxOverlayX[side] = minXLockLinked;
				minOverlayY[side] = maxYLockLinked;
				maxOverlayY[side] = minYLockLinked;					
			}			
			else if ( barrel.getStorage().isGhosting() && (barrel.sideUpgrades[side] == UpgradeSide.FRONT || barrel.sideUpgrades[side] == UpgradeSide.STICKER)){
				hasOverlay[side]  = true;
				minOverlayX[side] = maxXLock;
				maxOverlayX[side] = minXLock;
				minOverlayY[side] = maxYLock;
				maxOverlayY[side] = minYLock;					
			}
			else if ( barrel.getLinked() && (barrel.sideUpgrades[side] == UpgradeSide.FRONT || barrel.sideUpgrades[side] == UpgradeSide.STICKER)){
				hasOverlay[side]  = true;
				minOverlayX[side] = maxXLinked;
				maxOverlayX[side] = minXLinked;
				minOverlayY[side] = maxYLinked;
				maxOverlayY[side] = minYLinked;					
			}			
		}
		
		double xMin = x, xMax = x + 1;
		double yMin = y, yMax = y + 1;
		double zMin = z, zMax = z + 1;		
		
		boolean renderAll = renderer.renderAllFaces;

		boolean[] renderSide = {
				renderAll || y <= 0 || block.shouldSideBeRendered(world, x, y - 1, z, 0),
						renderAll || y >= worldHeight || block.shouldSideBeRendered(world, x, y + 1, z, 1),
						renderAll || block.shouldSideBeRendered(world, x, y, z - 1, 2),
						renderAll || block.shouldSideBeRendered(world, x, y, z + 1, 3),
						renderAll || block.shouldSideBeRendered(world, x - 1, y, z, 4),
						renderAll || block.shouldSideBeRendered(world, x + 1, y, z, 5),
		};		
		
		tessellator.setColorOpaque_F(1, 1, 1);
		
		if (renderSide[0])
		{ // DOWN
			tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y - 1, z));
			tessellator.addVertexWithUV(xMin, yMin, zMax, minX[0], minY[0]);
			tessellator.addVertexWithUV(xMin, yMin, zMin, minX[0], maxY[0]);
			tessellator.addVertexWithUV(xMax, yMin, zMin, maxX[0], maxY[0]);
			tessellator.addVertexWithUV(xMax, yMin, zMax, maxX[0], minY[0]);
			
			if (hasOverlay[0]){
				tessellator.addVertexWithUV(xMin, yMin, zMax, maxOverlayX[0], maxOverlayY[0]);
				tessellator.addVertexWithUV(xMin, yMin, zMin, maxOverlayX[0], minOverlayY[0]);
				tessellator.addVertexWithUV(xMax, yMin, zMin, minOverlayX[0], minOverlayY[0]);
				tessellator.addVertexWithUV(xMax, yMin, zMax, minOverlayX[0], maxOverlayY[0]);				
			}
			
		}
		
		if (renderSide[1])
		{ // UP
			tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y + 1, z));
			tessellator.addVertexWithUV(xMin, yMax, zMin, minX[1], minY[1]);
			tessellator.addVertexWithUV(xMin, yMax, zMax, minX[1], maxY[1]);
			tessellator.addVertexWithUV(xMax, yMax, zMax, maxX[1], maxY[1]);
			tessellator.addVertexWithUV(xMax, yMax, zMin, maxX[1], minY[1]);
			
			if (hasOverlay[1]){
				tessellator.addVertexWithUV(xMin, yMax, zMin, maxOverlayX[1], maxOverlayY[1]);
				tessellator.addVertexWithUV(xMin, yMax, zMax, maxOverlayX[1], minOverlayY[1]);
				tessellator.addVertexWithUV(xMax, yMax, zMax, minOverlayX[1], minOverlayY[1]);
				tessellator.addVertexWithUV(xMax, yMax, zMin, minOverlayX[1], maxOverlayY[1]);				
			}			
		}
		
		if (renderSide[2])
		{
			tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z - 1));
			tessellator.addVertexWithUV(xMax, yMax, zMin, minX[2], minY[2]);
			tessellator.addVertexWithUV(xMax, yMin, zMin, minX[2], maxY[2]);
			tessellator.addVertexWithUV(xMin, yMin, zMin, maxX[2], maxY[2]);
			tessellator.addVertexWithUV(xMin, yMax, zMin, maxX[2], minY[2]);
			
			if (hasOverlay[2]){
				tessellator.addVertexWithUV(xMax, yMax, zMin, maxOverlayX[2], maxOverlayY[2]);
				tessellator.addVertexWithUV(xMax, yMin, zMin, maxOverlayX[2], minOverlayY[2]);
				tessellator.addVertexWithUV(xMin, yMin, zMin, minOverlayX[2], minOverlayY[2]);
				tessellator.addVertexWithUV(xMin, yMax, zMin, minOverlayX[2], maxOverlayY[2]);	
			}
			
		}
		
		if (renderSide[3])
		{
			tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x, y, z + 1));			
			tessellator.addVertexWithUV(xMin, yMax, zMax, minX[3], minY[3]);
			tessellator.addVertexWithUV(xMin, yMin, zMax, minX[3], maxY[3]);
			tessellator.addVertexWithUV(xMax, yMin, zMax, maxX[3], maxY[3]);
			tessellator.addVertexWithUV(xMax, yMax, zMax, maxX[3], minY[3]);

			if (hasOverlay[3]){
				tessellator.addVertexWithUV(xMin, yMax, zMax, maxOverlayX[3], maxOverlayY[3]);
				tessellator.addVertexWithUV(xMin, yMin, zMax, maxOverlayX[3], minOverlayY[3]);
				tessellator.addVertexWithUV(xMax, yMin, zMax, minOverlayX[3], minOverlayY[3]);
				tessellator.addVertexWithUV(xMax, yMax, zMax, minOverlayX[3], maxOverlayY[3]);	
			}
						
			
		}
		
		if (renderSide[4])
		{
			tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x - 1 , y, z));			
			tessellator.addVertexWithUV(xMin, yMin, zMax, minX[4], maxY[4]);
			tessellator.addVertexWithUV(xMin, yMax, zMax, minX[4], minY[4]);
			tessellator.addVertexWithUV(xMin, yMax, zMin, maxX[4], minY[4]);
			tessellator.addVertexWithUV(xMin, yMin, zMin, maxX[4], maxY[4]);
			
			if (hasOverlay[4]){
				tessellator.addVertexWithUV(xMin, yMin, zMax, minOverlayX[4], minOverlayY[4]);
				tessellator.addVertexWithUV(xMin, yMax, zMax, minOverlayX[4], maxOverlayY[4]);
				tessellator.addVertexWithUV(xMin, yMax, zMin, maxOverlayX[4], maxOverlayY[4]);
				tessellator.addVertexWithUV(xMin, yMin, zMin, maxOverlayX[4], minOverlayY[4]);	
			}			
		}
		
		if (renderSide[5])
		{
			tessellator.setBrightness(block.getMixedBrightnessForBlock(world, x + 1 , y, z));			
			tessellator.addVertexWithUV(xMax, yMin, zMin, minX[5], maxY[5]);
			tessellator.addVertexWithUV(xMax, yMax, zMin, minX[5], minY[5]);
			tessellator.addVertexWithUV(xMax, yMax, zMax, maxX[5], minY[5]);
			tessellator.addVertexWithUV(xMax, yMin, zMax, maxX[5], maxY[5]);
			
			if (hasOverlay[5]){
				tessellator.addVertexWithUV(xMax, yMin, zMin, minOverlayX[5], minOverlayY[5]);
				tessellator.addVertexWithUV(xMax, yMax, zMin, minOverlayX[5], maxOverlayY[5]);
				tessellator.addVertexWithUV(xMax, yMax, zMax, maxOverlayX[5], maxOverlayY[5]);
				tessellator.addVertexWithUV(xMax, yMin, zMax, maxOverlayX[5], minOverlayY[5]);	
			}

			if (stack != null){
				
			}
			
			/*
			if (iconStack != null){
				System.out.printf("%s\n",iconStack.getIconName());
				tessellator.addVertexWithUV(xMax + 1.0, yMin, zMin, minXStack, minYStack);
				tessellator.addVertexWithUV(xMax, yMax, zMin, minXStack, maxYStack);
				tessellator.addVertexWithUV(xMax, yMax, zMax, maxXStack, maxYStack);
				tessellator.addVertexWithUV(xMax, yMin, zMax, maxXStack, minYStack);				
			}
			*/

		}
		
		return true;
	}
	
	@Override
	public boolean shouldRender3DInInventory(int modelID) {
		return true;
	}

	@Override
	public int getRenderId() {
		return BetterBarrels.blockBarrelRendererID;
	}

}
