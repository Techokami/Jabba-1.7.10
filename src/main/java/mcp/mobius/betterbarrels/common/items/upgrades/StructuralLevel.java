package mcp.mobius.betterbarrels.common.items.upgrades;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import mcp.mobius.betterbarrels.BetterBarrels;
import mcp.mobius.betterbarrels.Utils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;

import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class StructuralLevel {
	public static String[] upgradeMaterialsList = new String[]{ "Ore.plankWood", "Ore.ingotIron", "Ore.ingotGold", "Ore.gemDiamond", "Ore.obsidian", "Ore.whiteStone", "Ore.gemEmerald" };
	public static int maxCraftableTier = upgradeMaterialsList.length;
	public static StructuralLevel[] LEVELS;

	private static boolean structureArrayInitialized = false;

	public String name;
	public Utils.Material material;
	public ItemStack materialStack;
	private AccessibleTextureAtlasSprite iconBlockSide;
	private AccessibleTextureAtlasSprite iconBlockLabel;
	private AccessibleTextureAtlasSprite iconBlockTop;
	private AccessibleTextureAtlasSprite iconBlockTopLabel;
	private AccessibleTextureAtlasSprite iconItem;
	private int textColor;
	private int maxCoreSlots;
	private boolean needsMaterialInitialization = false;
	private int level = 0;

	private StructuralLevel() {
		// Special case for base barrel with no upgrade
		this.textColor = 0xFFFFFFFF;
		this.maxCoreSlots = 0;
	}

	private StructuralLevel(String materialin, final int level) {
		this.level = level;
		this.material = new Utils.Material(materialin);

		this.needsMaterialInitialization = true;

		this.maxCoreSlots = 0;
		for (int i = 0; i < level; i++)
			this.maxCoreSlots += MathHelper.floor_double(Math.pow(2, i));

		BetterBarrels.debug("03 - Created structural entry for [" + (this.material.isOreDict() ? this.material.name : (this.material.modDomain + ":" + this.material.name + ":" + this.material.meta)) + "] with " + this.maxCoreSlots + " slots.");
	}

	public void initializeMaterial() {
		if(this.needsMaterialInitialization) {
			this.materialStack = this.material.getStack();
		}
	}

	public static void initializeStructuralMaterials() {
		BetterBarrels.debug("04 - Looking up structural materials in the Ore Dictionary");
		for (StructuralLevel level : StructuralLevel.LEVELS) {
			level.initializeMaterial();
		}
	}

	public void discoverMaterialName() {
		BetterBarrels.debug("15 - Looking up user friendly name for " + (this.material.isOreDict() ? this.material.name : (this.material.modDomain + ":" + this.material.name + ":" + this.material.meta)));
		this.name = materialStack.getDisplayName();

		if (this.name.indexOf(".name") > 0) {
			this.name = LanguageRegistry.instance().getStringLocalization(this.name);
		}
		BetterBarrels.debug("16 - Found: " + this.name);
	}

	public static void createLevelArray() {
		if (structureArrayInitialized) return;
		LEVELS = new StructuralLevel[Math.min(18, upgradeMaterialsList.length) + 1];
		BetterBarrels.debug("02 - Creating materials array of length " + LEVELS.length);
		LEVELS[0] = new StructuralLevel();
		for (int i = 1; i < LEVELS.length; i++) {
			LEVELS[i] = new StructuralLevel(upgradeMaterialsList[i - 1], i);
		}
		structureArrayInitialized = true;
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIconSide() {
		return this.iconBlockSide;
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIconTop() {
		return this.iconBlockTop;
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIconLabel() {
		return this.iconBlockLabel;
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIconLabelTop() {
		return this.iconBlockTopLabel;
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIconItem() {
		return this.iconItem;
	}

	@SideOnly(Side.CLIENT)
	public int getTextColor() {
		return this.textColor;
	}

	public int getMaxCoreSlots() {
		return this.maxCoreSlots;
	}

	// Begin the crazy icon stuff
	/* Basic process:
	 * 
	 * register dummy icons, this is to get entries and values(position offsets, etc..) into the texture sheet
	 * use opengl functions to access the texture sheet and read the base pieces
	 * manipulate as desired
	 * use opengl to replace modified array into original texture sheet since or registered Icons just store offsets to the texture sheet
	 * End result: final icon used is dynamically generated at runtime, at every resource manager reload
	 */
	@SideOnly(Side.CLIENT)
	private static class BaseTextures {
		public int[] labelBackground;
		public int[] labelBorder;
		public int[] topBackground;
		public int[] topBorder;
		public int[] topLabel;
		public int[] sideBackground;
		public int[] sideBorder;
		public int[] item;
		public int[] itemArrow;
	}

	@SideOnly(Side.CLIENT)
	private static BaseTextures baseTexturePixels;

	public static void loadBaseTextureData() {
		BetterBarrels.debug("08 - Pre-loading component texture data.");
		StructuralLevel.baseTexturePixels = new BaseTextures();

		StructuralLevel.baseTexturePixels.labelBorder = getPixelsForTexture(false, BetterBarrels.modid + ":barrel_label_border");
		StructuralLevel.baseTexturePixels.labelBackground = getPixelsForTexture(false, BetterBarrels.modid + ":barrel_label_background");
		StructuralLevel.baseTexturePixels.topBorder = getPixelsForTexture(false, BetterBarrels.modid + ":barrel_top_border");
		StructuralLevel.baseTexturePixels.topBackground = getPixelsForTexture(false, BetterBarrels.modid + ":barrel_top_background");
		StructuralLevel.baseTexturePixels.topLabel = getPixelsForTexture(false, BetterBarrels.modid + ":barrel_top_label");
		StructuralLevel.baseTexturePixels.sideBorder = getPixelsForTexture(false, BetterBarrels.modid + ":barrel_side_border");
		StructuralLevel.baseTexturePixels.sideBackground = getPixelsForTexture(false, BetterBarrels.modid + ":barrel_side_background");
		StructuralLevel.baseTexturePixels.item = getPixelsForTexture(true, BetterBarrels.modid + ":capaupg_base");
		StructuralLevel.baseTexturePixels.itemArrow = getPixelsForTexture(true, BetterBarrels.modid + ":capaupg_color");
	}

	public static void unloadBaseTextureData() {
		BetterBarrels.debug("39 - Unloading preloaded texture data");
		StructuralLevel.baseTexturePixels = null;
	}

	@SideOnly(Side.CLIENT)
	private static class AccessibleTextureAtlasSprite extends TextureAtlasSprite {
		AccessibleTextureAtlasSprite(String par1Str) {
			super(par1Str);
		}

		private static Method fixPixels = null;
		private static Method setupAnisotropic = null;
		private static Field useAnisotropic = null;

		static {
			for(String fieldName : new String[]{"k", "field_147966_k", "useAnisotropicFiltering"}) {
				try {
					AccessibleTextureAtlasSprite.useAnisotropic = TextureAtlasSprite.class.getDeclaredField(fieldName);
					if(AccessibleTextureAtlasSprite.useAnisotropic != null) {
						AccessibleTextureAtlasSprite.useAnisotropic.setAccessible(true);
						break;
					}
				} catch (Throwable t) {}
			}
			if(AccessibleTextureAtlasSprite.useAnisotropic == null) {
				BetterBarrels.log.severe("Unable to locate required field 'useAnisotropicFiltering' for texture generation.  Please post this error at the error tracker along with a copy of your ForgeModLoader-client-0.log.");
			}

			for(String methodName : new String[]{"a", "func_147961_a", "fixTransparentPixels"}) {
				try {
					AccessibleTextureAtlasSprite.fixPixels = TextureAtlasSprite.class.getDeclaredMethod(methodName, new Class[]{int[][].class});
					if(AccessibleTextureAtlasSprite.fixPixels != null) {
						AccessibleTextureAtlasSprite.fixPixels.setAccessible(true);
						break;
					}
				} catch (Throwable t) {}
			}
			if(AccessibleTextureAtlasSprite.fixPixels == null) {
				BetterBarrels.log.severe("Unable to locate required method 'fixTransparentPixels' for texture generation.  Please post this error at the error tracker along with a copy of your ForgeModLoader-client-0.log.");
			}

			for(String methodName : new String[]{"a", "func_147960_a", "prepareAnisotropicFiltering"}) {
				try {
					AccessibleTextureAtlasSprite.setupAnisotropic = TextureAtlasSprite.class.getDeclaredMethod(methodName, new Class[]{ int[][].class, int.class, int.class });
					if(AccessibleTextureAtlasSprite.setupAnisotropic != null) {
						AccessibleTextureAtlasSprite.setupAnisotropic.setAccessible(true);
						break;
					}
				} catch (Throwable t) {}
			}
			if(AccessibleTextureAtlasSprite.setupAnisotropic == null) {
				BetterBarrels.log.severe("Unable to locate required method 'prepareAnisotropicFiltering' for texture generation.  Please post this error at the error tracker along with a copy of your ForgeModLoader-client-0.log.");
			}
		}
		
		@SuppressWarnings("unchecked")
		public void replaceTextureData(int[] pixels, int mipmapLevels) throws Exception {
			this.setFramesTextureData(Lists.newArrayList());
			int[][] aint = new int [1 + mipmapLevels][];
			aint[0] = pixels;
			AccessibleTextureAtlasSprite.fixPixels.invoke(this, (Object)aint);
			this.framesTextureData.add((int[][])AccessibleTextureAtlasSprite.setupAnisotropic.invoke(this, (Object)aint, (Object)(AccessibleTextureAtlasSprite.useAnisotropic.getBoolean(this) ? this.width - 16: this.width), (Object)(AccessibleTextureAtlasSprite.useAnisotropic.getBoolean(this) ? this.height - 16: this.height)));
			this.generateMipmaps(mipmapLevels);
			BetterBarrels.debug("37 - Attempting to replace texture for ["+this.getIconName()+"] with an array of ["+(this.getFrameTextureData(0)!=null?this.getFrameTextureData(0)[0].length:"(null)")+"] pixels, current texture dims are ["+this.width+"x"+this.height+"] for a total size of "+(this.width*this.height));
			BetterBarrels.debug(this.toString());
			if (this.getFrameTextureData(0)[0].length != (this.height * this.width)) {
				throw new Exception("Attempting to replace texture image data with too much or too little data.");
			}
			BetterBarrels.debug("38 - Calling Minecraft Texture upload utility method");
			TextureUtil.uploadTextureMipmap(this.getFrameTextureData(0), this.width, this.height, this.originX, this.originY, false, false);
			this.clearFramesTextureData();
		}
	}

	@SideOnly(Side.CLIENT)
	private static AccessibleTextureAtlasSprite registerIcon(IIconRegister par1IconRegister, String key) {
		AccessibleTextureAtlasSprite ret = new AccessibleTextureAtlasSprite(key);
		if (((TextureMap)par1IconRegister).setTextureEntry(key, ret)) {
			return ret;
		} else {
			return (AccessibleTextureAtlasSprite)((TextureMap)par1IconRegister).getTextureExtry(key);
		}
	}

	@SideOnly(Side.CLIENT)
	public void registerItemIcon(IIconRegister par1IconRegister, int ordinal) {
		this.iconItem = StructuralLevel.registerIcon(par1IconRegister, BetterBarrels.modid + ":blanks/capacity/" + String.valueOf(ordinal));
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister par1IconRegister, int ordinal) {
		if (ordinal > 0) {
			this.iconBlockSide = StructuralLevel.registerIcon(par1IconRegister, BetterBarrels.modid + ":blanks/side/" + String.valueOf(ordinal));
			this.iconBlockLabel = StructuralLevel.registerIcon(par1IconRegister, BetterBarrels.modid + ":blanks/label/" + String.valueOf(ordinal));
			this.iconBlockTop = StructuralLevel.registerIcon(par1IconRegister, BetterBarrels.modid + ":blanks/top/" + String.valueOf(ordinal));
			this.iconBlockTopLabel = StructuralLevel.registerIcon(par1IconRegister, BetterBarrels.modid + ":blanks/toplabel/" + String.valueOf(ordinal));
		} else {
			this.iconBlockSide = StructuralLevel.registerIcon(par1IconRegister, BetterBarrels.modid + ":barrel_side_" + String.valueOf(ordinal));
			this.iconBlockTop = StructuralLevel.registerIcon(par1IconRegister, BetterBarrels.modid + ":barrel_top_" + String.valueOf(ordinal));
			this.iconBlockLabel = StructuralLevel.registerIcon(par1IconRegister, BetterBarrels.modid + ":barrel_label_" + String.valueOf(ordinal));
			this.iconBlockTopLabel = StructuralLevel.registerIcon(par1IconRegister, BetterBarrels.modid + ":barrel_labeltop_" + String.valueOf(ordinal));
		}
	}

	public static String romanNumeral(int num) {
		LinkedHashMap<String, Integer> numeralConversion = new LinkedHashMap<String, Integer>();
		numeralConversion.put("M", 1000);
		numeralConversion.put("CM", 900);
		numeralConversion.put("D", 500);
		numeralConversion.put("CD", 400);
		numeralConversion.put("C", 100);
		numeralConversion.put("XC", 90);
		numeralConversion.put("L", 50);
		numeralConversion.put("XL", 40);
		numeralConversion.put("X", 10);
		numeralConversion.put("IX", 9);
		numeralConversion.put("V", 5);
		numeralConversion.put("IV", 4);
		numeralConversion.put("I", 1);

		String result = new String();

		while (numeralConversion.size() > 0) {
			String romanKey = (String)numeralConversion.keySet().toArray()[0];
			Integer arabicValue = (Integer)numeralConversion.values().toArray()[0];
			if (num < arabicValue) {
				numeralConversion.remove(romanKey);
			} else {
				num -= arabicValue;
				result += romanKey;
			}
		}

		return result;
	}

	public static int[] structuralColorOverrides = null;

	private class PixelARGB {
		int A, R, G, B;
		int combined;
		private int addCount = 0;

		PixelARGB(final int pixel) {
			A = (pixel >> 24) & 0xFF;
			R = (pixel >> 16) & 0xFF;
			G = (pixel >> 8) & 0xFF;
			B = pixel & 0xFF;
			combined = pixel;
		}

		PixelARGB(final int alpha, final int red, final int green, final int blue) {
			A = alpha;
			R = red;
			G = green;
			B = blue;
			combined = ((A & 0xFF) << 24) + ((R & 0xFF) << 16) + ((G & 0xFF) << 8) + (B & 0xFF);
		}

		PixelARGB alphaAdd(PixelARGB add) {
			addCount++;
			A += add.A;
			R += (add.R * add.A) / 255;
			G += (add.G * add.G) / 255;
			B += (add.B * add.B) / 255;
			combined = ((A & 0xFF) << 24) + ((R & 0xFF) << 16) + ((G & 0xFF) << 8) + (B & 0xFF);
			return this;
		}

		PixelARGB normalize() {
			if (addCount == 0) return this;
			R = R * 255 / A;
			G = G * 255 / A;
			B = B * 255 / A;
			A = A / addCount;
			combined = ((A & 0xFF) << 24) + ((R & 0xFF) << 16) + ((G & 0xFF) << 8) + (B & 0xFF);
			addCount = 0;
			return this;
		}

		PixelARGB addIgnoreAlpha(PixelARGB add) {
			addCount++;
			R += add.R;
			G += add.G;
			B += add.B;
			combined = ((A & 0xFF) << 24) + ((R & 0xFF) << 16) + ((G & 0xFF) << 8) + (B & 0xFF);
			return this;
		}

		PixelARGB addSkipTransparent(PixelARGB add) {
			if (add.A == 0)
				return this;
			addCount++;
			R += add.R;
			G += add.G;
			B += add.B;
			combined = ((A & 0xFF) << 24) + ((R & 0xFF) << 16) + ((G & 0xFF) << 8) + (B & 0xFF);
			return this;
		}

		PixelARGB normalizeIgnoreAlpha() {
			if (addCount == 0) return this;
			R = R / addCount;
			G = G / addCount;
			B = B / addCount;
			combined = ((A & 0xFF) << 24) + ((R & 0xFF) << 16) + ((G & 0xFF) << 8) + (B & 0xFF);
			addCount = 0;
			return this;
		}

		PixelARGB YIQContrastTextColor() {
			int color = (((R * 299) + (G * 587) + (B * 114)) / 1000) >= 128 ? 0: 255;
			return new PixelARGB(255, color, color, color);
		}
	}

	private void grainMergeArrayWithColor(int[] pixels, PixelARGB color) {
		BetterBarrels.debug("35 - Running grain merge on material with color");
		for (int i = 0; i < pixels.length; i++) {
			PixelARGB pix = new PixelARGB(pixels[i]);
			if (pix.A == 0)
				pixels[i] = 0;
			else
				pixels[i] = (new PixelARGB(255, Math.max(0, (Math.min(255, pix.R + color.R - 128))), Math.max(0, (Math.min(255, pix.G + color.G - 128))), Math.max(0, (Math.min(255, pix.B + color.B - 128))))).combined;
		}
		BetterBarrels.debug("36 - sanity check, pixels.length:" + pixels.length);
	}

	private void mergeArraysBasedOnAlpha(int[] target, int[] merge) {
		// Merge arrays, ignoring any transparent pixels in the merge array
		for (int i = 0; i < merge.length; i++) {
			PixelARGB targetPixel = new PixelARGB(target[i]);
			PixelARGB mergePixel = new PixelARGB(merge[i]);
			target[i] = mergePixel.A == 0 ? targetPixel.combined: mergePixel.combined;
		}
	}

	private PixelARGB averageColorFromArray(int[] pixels) {
		PixelARGB totals = new PixelARGB(0);
		for (int pixel: pixels) {
			totals.alphaAdd(new PixelARGB(pixel));
		}
		return totals.normalize();
	}

	private PixelARGB averageColorFromArrayB(int[] pixels) {
		PixelARGB totals = new PixelARGB(0);
		for (int pixel: pixels) {
			// totals.addIgnoreAlpha(new PixelARGB(pixel));
			totals.addSkipTransparent(new PixelARGB(pixel));
		}
		return totals.normalizeIgnoreAlpha();
	}

	@SideOnly(Side.CLIENT)
	private static int[] getPixelsForTexture(boolean item, ResourceLocation resourcelocation) {
		BetterBarrels.debug("09 - Entering texture load method for texture : " + resourcelocation.toString());
		/*TextureMap map = (TextureMap)Minecraft.getMinecraft().renderEngine.getTexture(item ? TextureMap.locationItemsTexture: TextureMap.locationBlocksTexture);
      for (String field: new String[] {}) {

      }*/
		ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getResourceDomain(), String.format("%s/%s%s", new Object[] {(item ? "textures/items": "textures/blocks")/*map.basePath*/, resourcelocation.getResourcePath(), ".png"}));
		BetterBarrels.debug("11 - Modified resource path : " + resourcelocation1.toString());
		int[] pixels = null;
		try {
			pixels = TextureUtil.readImageData(Minecraft.getMinecraft().getResourceManager(), resourcelocation1);
		} catch (Throwable t) {
			BetterBarrels.log.severe("JABBA-Debug Problem loading texture: " + resourcelocation);
		}
		BetterBarrels.debug("12 - read texture data of length : " + (pixels != null ? pixels.length : "(null)"));
		return pixels;
	}

	@SideOnly(Side.CLIENT)
	private static int[] getPixelsForTexture(boolean item, String location) {
		return getPixelsForTexture(item, new ResourceLocation(location));
	}

	@SideOnly(Side.CLIENT)
	private static int[] getPixelsForTexture(boolean item, IIcon icon) {
		int[] pixels = getPixelsForTexture(item, new ResourceLocation(icon.getIconName()));
		if (pixels == null) {
			pixels = new int[icon.getIconHeight() * icon.getIconWidth()];
			BetterBarrels.debug("13 - No texture data read, creating empty array of length : " + pixels.length);
		}
		return pixels;
	}

	@SideOnly(Side.CLIENT)
	public void generateIcons() {
		BetterBarrels.debug("17 - Entering Texture Generation for Structural Tier with Material: " + this.name);
		int terrainTextureId = Minecraft.getMinecraft().renderEngine.getTexture(TextureMap.locationBlocksTexture).getGlTextureId();
		int itemTextureId = Minecraft.getMinecraft().renderEngine.getTexture(TextureMap.locationItemsTexture).getGlTextureId();
		if (terrainTextureId != 0 && itemTextureId != 0) {
			// Store previous texture
			int previousTextureID = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);

			// Copy the block textures we need into arrays
	         int[] labelBorderPixels = baseTexturePixels.labelBorder.clone();
	         BetterBarrels.debug("18 - " + labelBorderPixels.length);
	         int[] labelBackgroundPixels = baseTexturePixels.labelBackground.clone();
	         BetterBarrels.debug("19 - " + labelBackgroundPixels.length);
	         int[] topBorderPixels = baseTexturePixels.topBorder.clone();
	         BetterBarrels.debug("20 - " + topBorderPixels.length);
	         int[] topBackgroundPixels = baseTexturePixels.topBackground.clone();
	         BetterBarrels.debug("21 - " + topBackgroundPixels.length);
	         int[] topLabelBorderPixels = baseTexturePixels.topBorder.clone();
	         BetterBarrels.debug("22 - " + topLabelBorderPixels.length);
	         int[] topLabelBackgroundPixels = baseTexturePixels.topLabel.clone();
	         BetterBarrels.debug("23 - " + topLabelBackgroundPixels.length);
	         int[] sideBorderPixels = baseTexturePixels.sideBorder.clone();
	         BetterBarrels.debug("24 - " + sideBorderPixels.length);
	         int[] sideBackgroundPixels = baseTexturePixels.sideBackground.clone();
	         BetterBarrels.debug("25 - " + sideBackgroundPixels.length);

	         // Copy the item textures we need into arrays
	         int[] itemBasePixels = baseTexturePixels.item.clone();
	         BetterBarrels.debug("26 - " + itemBasePixels.length);
	         int[] itemArrowPixels = baseTexturePixels.itemArrow.clone();
	         BetterBarrels.debug("27 - " + itemArrowPixels.length);
	         int[] itemRomanPixels = StructuralLevel.getPixelsForTexture(true, this.iconItem);
	         BetterBarrels.debug("28 - " + itemRomanPixels.length);

			int[] materialPixels = null;
			boolean foundSourceMaterial = false;
			if (StructuralLevel.structuralColorOverrides[this.level-1] == -1) {
				try {
					Block materialBlock = Block.getBlockFromItem(materialStack.getItem());
					Item materialItem = materialStack.getItem();

					if (materialBlock != Blocks.air && !materialBlock.getUnlocalizedName().equalsIgnoreCase("tile.ForgeFiller")) {
						BetterBarrels.debug("32 - Block found");
						materialPixels = this.getPixelsForTexture(false, (TextureAtlasSprite)materialBlock.getIcon(0, materialStack.getItemDamage()));
						foundSourceMaterial = true;
						BetterBarrels.debug("33 - Loaded texture data for [" + this.name + "]: read an array of length: " + (materialPixels != null ? materialPixels.length: "(null)"));
					} else if (materialItem != null){
						BetterBarrels.debug("30 - Item found, attempting to load");
						materialPixels = this.getPixelsForTexture(true, (TextureAtlasSprite)materialItem.getIconFromDamage(materialStack.getItemDamage()));
						foundSourceMaterial = true;
						BetterBarrels.debug("30 - Loaded texture data for [" + this.name + "]: read an array of length: " + (materialPixels != null ? materialPixels.length: "(null)"));
					}
				} catch (Throwable t) {
					BetterBarrels.debug("34 - MATERIAL LOOKUP ERROR");
					BetterBarrels.log.severe("Error loading resource material texture: " + t.getMessage());
					t.printStackTrace();
				} finally {
					// nothing found, skip out
					if (!foundSourceMaterial) {
						BetterBarrels.log.severe("Encountered an issue while locating the requested source material[" + (this.material.isOreDict() ? this.material.name : (this.material.modDomain + ":" + this.material.name + ":" + this.material.meta)) + "].  Ore Dictionary returned " + materialStack.getUnlocalizedName() + " as the first itemStack for that request.");
					}
				}
			} else {
				materialPixels = new int[1];
				materialPixels[0] = StructuralLevel.structuralColorOverrides[this.level-1];
				foundSourceMaterial = true;
			}

			if (foundSourceMaterial) {
				// PixelARGB color = averageColorFromArray(materialPixels); // This makes iron... more red, kind of a neat rusty look, but meh
				PixelARGB color = averageColorFromArrayB(materialPixels);
	            BetterBarrels.debug("Calculated Color for [" + this.name + "]: {R: " + color.R + ", G: " + color.G + ", B: " + color.B + "}");

				this.textColor = color.YIQContrastTextColor().combined;

				grainMergeArrayWithColor(labelBorderPixels, color);
				grainMergeArrayWithColor(topBorderPixels, color);
				grainMergeArrayWithColor(topLabelBorderPixels, color);
				grainMergeArrayWithColor(sideBorderPixels, color);
				grainMergeArrayWithColor(itemArrowPixels, color);

				this.textColor = averageColorFromArrayB(labelBorderPixels).YIQContrastTextColor().combined;

				try {
					mergeArraysBasedOnAlpha(labelBorderPixels, labelBackgroundPixels);
					mergeArraysBasedOnAlpha(topBorderPixels, topBackgroundPixels);
					mergeArraysBasedOnAlpha(topLabelBorderPixels, topLabelBackgroundPixels);
					mergeArraysBasedOnAlpha(sideBorderPixels, sideBackgroundPixels);
					mergeArraysBasedOnAlpha(itemBasePixels, itemArrowPixels);
					mergeArraysBasedOnAlpha(itemBasePixels, itemRomanPixels);

					GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, terrainTextureId);
					this.iconBlockLabel.replaceTextureData(labelBorderPixels, Minecraft.getMinecraft().gameSettings.mipmapLevels);
					this.iconBlockTop.replaceTextureData(topBorderPixels, Minecraft.getMinecraft().gameSettings.mipmapLevels);
					this.iconBlockTopLabel.replaceTextureData(topLabelBorderPixels, Minecraft.getMinecraft().gameSettings.mipmapLevels);
					this.iconBlockSide.replaceTextureData(sideBorderPixels, Minecraft.getMinecraft().gameSettings.mipmapLevels);

					GL11.glBindTexture(GL11.GL_TEXTURE_2D, itemTextureId);
					this.iconItem.replaceTextureData(itemBasePixels, 0);
					GL11.glBindTexture(GL11.GL_TEXTURE_2D, previousTextureID);
					GL11.glPopAttrib();
				} catch(Exception e) {
					BetterBarrels.log.severe("" + e.getMessage());
				}
			}
		}
	}
}
