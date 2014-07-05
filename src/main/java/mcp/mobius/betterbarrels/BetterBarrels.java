package mcp.mobius.betterbarrels;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

import mcp.mobius.betterbarrels.bspace.BSpaceStorageHandler;
import mcp.mobius.betterbarrels.common.BaseProxy;
import mcp.mobius.betterbarrels.common.blocks.BlockBarrel;
import mcp.mobius.betterbarrels.common.blocks.TileEntityBarrel;
import mcp.mobius.betterbarrels.common.items.ItemBarrelHammer;
import mcp.mobius.betterbarrels.common.items.ItemTuningFork;
import mcp.mobius.betterbarrels.common.items.dolly.ItemBarrelMover;
import mcp.mobius.betterbarrels.common.items.dolly.ItemDiamondMover;
import mcp.mobius.betterbarrels.common.items.upgrades.ItemUpgradeCore;
import mcp.mobius.betterbarrels.common.items.upgrades.ItemUpgradeSide;
import mcp.mobius.betterbarrels.common.items.upgrades.ItemUpgradeStructural;
import mcp.mobius.betterbarrels.common.items.upgrades.StructuralLevel;
import mcp.mobius.betterbarrels.network.BarrelPacketHandler;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStoppingEvent;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid=BetterBarrels.modid, name="JABBA", version="1.1.4", dependencies="after:Waila;after:NotEnoughItems")
public class BetterBarrels {

	private static boolean DEBUG_TEXTURES = false || Boolean.parseBoolean(System.getProperty("mcp.mobius.debugJabbaTextures","false"));
	public static void debug(String msg) {
		if (DEBUG_TEXTURES)
			log.log(Level.WARNING, msg);
	}

	public static final String modid = "JABBA";
	
	public static Logger log = Logger.getLogger(modid);	

    // The instance of your mod that Forge uses.
	@Instance(modid)
	public static BetterBarrels instance;
	
	// Says where the client and server 'proxy' code is loaded.
	@SidedProxy(clientSide="mcp.mobius.betterbarrels.client.ClientProxy", serverSide="mcp.mobius.betterbarrels.common.BaseProxy")
	public static BaseProxy proxy;

	/* CONFIG PARAMS */
	private static Configuration config = null;

	public static boolean  fullBarrelTexture  = true;
	public static boolean  highRezTexture     = true;
	public static boolean  showUpgradeSymbols = true;
	public static boolean  diamondDollyActive = true;	
	public static int[] colorOverrides        = new int[]{0, 0};
	public static int stacksSize              = 64;
	public static String upgradeItemStr       = "minecraft:fence";

	public static Block blockBarrel      = null;
	public static Block blockMiniBarrel  = null;
	public static Block blockBarrelShelf = null;	
	public static Item itemUpgradeStructural = null;
	public static Item itemUpgradeCore   = null;
	public static Item itemUpgradeSide   = null;
	public static Item itemMover         = null;
	public static Item itemMoverDiamond  = null;
	public static Item itemTuningFork    = null;
	public static Item itemLockingPlanks = null;
	public static Item itemHammer = null;
	
	public static long limiterDelay = 500;	
	
	public static int blockBarrelRendererID = -1;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		config = new Configuration(event.getSuggestedConfigurationFile());
		
		try {
			config.load();

			diamondDollyActive  = config.get(Configuration.CATEGORY_GENERAL, "diamondDollyActive", true).getBoolean(true);
			limiterDelay        = config.get(Configuration.CATEGORY_GENERAL, "packetLimiterDelay", 500, "Controls the minimum delay (in ms) between two server/client sync. Lower values mean closer to realtime, and more network usage.").getInt();			
			
			StructuralLevel.upgradeMaterialsList = config.get(Configuration.CATEGORY_GENERAL, "materialList", StructuralLevel.upgradeMaterialsList, "A structural tier will be created for each material in this list, even if not craftable").getStringList();
			if(StructuralLevel.upgradeMaterialsList.length > 18) {
				String[] trimedList = new String[18];
				for(int i=0;i<18;i++)
					trimedList[i] = StructuralLevel.upgradeMaterialsList[i];
				StructuralLevel.upgradeMaterialsList = trimedList;
				config.get(Configuration.CATEGORY_GENERAL, "materialList", trimedList).set(trimedList);
			}
			debug("00 - Loaded materials list: " + Arrays.toString(StructuralLevel.upgradeMaterialsList));
			StructuralLevel.maxCraftableTier = Math.min(18, Math.min(StructuralLevel.upgradeMaterialsList.length, config.get(Configuration.CATEGORY_GENERAL, "maxCraftableTier", StructuralLevel.upgradeMaterialsList.length, "Maximum tier to generate crafting recipes for").getInt()));
			StructuralLevel.maxCraftableTier = Math.min(18, Math.min(StructuralLevel.upgradeMaterialsList.length, config.get(Configuration.CATEGORY_GENERAL, "maxCraftableTier", StructuralLevel.upgradeMaterialsList.length).getInt()));
			debug("01 - Max craftable tier: " + StructuralLevel.maxCraftableTier);

			colorOverrides = config.get(Configuration.CATEGORY_GENERAL, "colorOverrides", BetterBarrels.colorOverrides, "This list contains paired numbers: first is the tier level this color applies to, second is the color. The color value is the RGB color as a single int").getIntList();
			if (colorOverrides != null) {
				if (colorOverrides.length % 2 == 0) {
					StructuralLevel.structuralColorOverrides = new int[StructuralLevel.upgradeMaterialsList.length];
					for(int i = 0; i < StructuralLevel.structuralColorOverrides.length; i++)
						StructuralLevel.structuralColorOverrides[i] = -1;
					for(int i = 0; i < colorOverrides.length; i += 2) {
						if(colorOverrides[i] == 0) continue;
						if(colorOverrides[i] > 0 && colorOverrides[i] < StructuralLevel.structuralColorOverrides.length) {
							StructuralLevel.structuralColorOverrides[colorOverrides[i]-1] = (0xFF << 24) | colorOverrides[i+1];
						} else {
							BetterBarrels.log.warning("Attempting to override the structural tier color for non existant tier: " + colorOverrides[i]);
						}
					}
				} else {
					BetterBarrels.log.warning("Color override list is not formatted in pairs, ignoring");
				}
			}
			stacksSize = config.get(Configuration.CATEGORY_GENERAL, "stacksSize", BetterBarrels.stacksSize, "How many stacks the base barrel and each upgrade will provide").getInt();
			upgradeItemStr = config.get(Configuration.CATEGORY_GENERAL, "tierUpgradeItem", BetterBarrels.upgradeItemStr, "The name of the item to use for the strutural tier upgrade recipes. Default is \"minecraft:fence\" for Vanilla Fence. The format is Ore.name for an ore dictionary lookup, or itemDomain:itemname[:meta] for a direct item, not this is case-sensitive.").getString();

			//fullBarrelTexture  = config.get(Configuration.CATEGORY_GENERAL, "fullBarrelTexture", true).getBoolean(true);
			//highRezTexture     = config.get(Configuration.CATEGORY_GENERAL, "highRezTexture", false).getBoolean(false);
			//showUpgradeSymbols = config.get(Configuration.CATEGORY_GENERAL, "showUpgradeSymbols", false).getBoolean(false);
		} catch (Exception e) {
			FMLLog.log(org.apache.logging.log4j.Level.ERROR, e, "BlockBarrel has a problem loading it's configuration");
			FMLLog.severe(e.getMessage());	
		} finally {
			if (config.hasChanged())
				config.save();
		}
		
		RecipeHandler.instance().registerOres();
		proxy.registerEventHandler();	
		
		//log.setLevel(Level.FINEST);
		blockBarrel           = new BlockBarrel();
		itemUpgradeStructural = new ItemUpgradeStructural();
		itemUpgradeCore       = new ItemUpgradeCore();
		itemUpgradeSide       = new ItemUpgradeSide();
		itemMover             = new ItemBarrelMover();
		itemMoverDiamond      = new ItemDiamondMover();
		itemHammer            = new ItemBarrelHammer();
		itemTuningFork        = new ItemTuningFork();
		
		GameRegistry.registerBlock(blockBarrel, "barrel");
		//GameRegistry.registerBlock(blockMiniBarrel);
		//GameRegistry.registerBlock(blockBarrelShelf);		
		//GameRegistry.registerTileEntity(TileEntityMiniBarrel.class,  "TileEntityMiniBarrel");
		//GameRegistry.registerTileEntity(TileEntityBarrelShelf.class, "TileEntityBarrelShelf");

		GameRegistry.registerItem(itemUpgradeStructural, "upgradeStructural");
		GameRegistry.registerItem(itemUpgradeCore, "upgradeCore");
		GameRegistry.registerItem(itemUpgradeSide, "upgradeSide");
		GameRegistry.registerItem(itemMover, "mover");
		GameRegistry.registerItem(itemMoverDiamond, "moverDiamond");
		GameRegistry.registerItem(itemHammer, "hammer");
		GameRegistry.registerItem(itemTuningFork, "tuningFork");

		BarrelPacketHandler.INSTANCE.ordinal();
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		StructuralLevel.createLevelArray();
		RecipeHandler.instance().registerRecipes();
		GameRegistry.registerTileEntity(TileEntityBarrel.class, "TileEntityBarrel");
		FMLCommonHandler.instance().bus().register(ServerTickHandler.INSTANCE); 
		proxy.registerRenderers();
        FMLInterModComms.sendMessage("Waila", "register", "mcp.mobius.betterbarrels.BBWailaProvider.callbackRegister");        
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		RecipeHandler.instance().registerLateRecipes();
		StructuralLevel.initializeStructuralMaterials();
		proxy.postInit();
	}	

	@EventHandler
	public void serverStopping(FMLServerStoppingEvent event) {
		BSpaceStorageHandler.instance().writeToFile();
	}
}
