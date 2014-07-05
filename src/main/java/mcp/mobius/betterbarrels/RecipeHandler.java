package mcp.mobius.betterbarrels;

import mcp.mobius.betterbarrels.common.items.upgrades.StructuralLevel;
import mcp.mobius.betterbarrels.common.items.upgrades.UpgradeCore;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;

public class RecipeHandler {

	public static RecipeHandler _instance = new RecipeHandler();
	private RecipeHandler(){}
	public static RecipeHandler instance() { return RecipeHandler._instance; }	
	
	public void registerOres(){
		OreDictionary.registerOre("ingotIron",  Items.iron_ingot);
		OreDictionary.registerOre("ingotGold",  Items.gold_ingot);
		OreDictionary.registerOre("slimeball",  Items.slime_ball);
		OreDictionary.registerOre("gemDiamond", Items.diamond);
		OreDictionary.registerOre("gemEmerald", Items.emerald);
		OreDictionary.registerOre("chestWood",  Blocks.chest);
		OreDictionary.registerOre("stickWood",  Items.stick);
		OreDictionary.registerOre("obsidian",   Blocks.obsidian);
		OreDictionary.registerOre("whiteStone", Blocks.end_stone);
		OreDictionary.registerOre("transdimBlock", Blocks.ender_chest);
	}

	private ItemStack upgradeItem = null;

	public void registerRecipes(){
		this.addCoreUpgrade(0, BetterBarrels.blockBarrel);
		this.addCoreUpgrade(1, "transdimBlock");
		this.addCoreUpgrade(2, Blocks.redstone_block);
		this.addCoreUpgrade(3, Blocks.hopper);
		this.addCoreUpgrade(UpgradeCore.VOID.ordinal(), Blocks.obsidian);
		
		this.addSideUpgrade(1, Blocks.hopper);
		this.addSideUpgrade(2, Items.redstone);
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterBarrels.blockBarrel), new Object[]
				 {"W-W", "WCW", "WWW", 
			     Character.valueOf('C'), "chestWood", 
				 Character.valueOf('W'), "logWood",
				 Character.valueOf('-'), "slabWood"}));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterBarrels.itemMover,1,0), new Object[] 
				{"  X", " PX", "XXX",
		     Character.valueOf('X'), "ingotIron", 
			 Character.valueOf('P'), "plankWood"}));
		
		if (BetterBarrels.diamondDollyActive){
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterBarrels.itemMoverDiamond,1,0), new Object[] 
					{"   ", " P ", "XXX",
			     Character.valueOf('X'), "gemDiamond", 
				 Character.valueOf('P'), BetterBarrels.itemMover}));
		}
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterBarrels.itemUpgradeSide, 4, 0), new Object[]
				{" P ","PXP", " P ", 
			 Character.valueOf('P'), Items.paper, 
			 Character.valueOf('X'), "slimeball"}));		
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterBarrels.itemHammer, 1, 0), new Object[]
				{"III","ISI", " S ", 
			 Character.valueOf('I'), "ingotIron", 
			 Character.valueOf('S'), "stickWood"}));	
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterBarrels.itemTuningFork, 1, 0), new Object[]
				{" P "," EP", "P  ", 
			Character.valueOf('P'), "ingotIron",
			Character.valueOf('E'), Items.ender_pearl
				}));		

		UpgradeCore prevStorage = UpgradeCore.STORAGE;
		for (UpgradeCore core : UpgradeCore.values()) {
			if(core.type == UpgradeCore.Type.STORAGE && core.slotsUsed > 1) {
				if (core.slotsUsed > StructuralLevel.LEVELS[StructuralLevel.maxCraftableTier].getMaxCoreSlots())
					break;
				addCoreUpgradeUpgrade(core.ordinal(), prevStorage.ordinal());
				prevStorage = core;
			}
		}
	}

	public void registerLateRecipes() {
		try {
			Utils.Material mat = new Utils.Material(BetterBarrels.upgradeItemStr);
			upgradeItem = mat.getStack();
		} catch (Throwable t) {
			BetterBarrels.log.severe("Requested item with id " + BetterBarrels.upgradeItemStr + " for tier upgrade recipes was not found, using the default of vanilla fence");
			upgradeItem = new ItemStack(Blocks.fence);
		}

		for (int i = 0; i < Math.min(StructuralLevel.LEVELS.length-1, StructuralLevel.maxCraftableTier); i++)
			this.addStructuralUpgrade(i, StructuralLevel.upgradeMaterialsList[i]);
	}

	private void addCoreUpgradeUpgrade(int resultMeta, int sourceMeta) {
      GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(BetterBarrels.itemUpgradeCore, 1, resultMeta),
                                                    new Object[]{new ItemStack(BetterBarrels.itemUpgradeCore, 1, sourceMeta),
                                                                 new ItemStack(BetterBarrels.itemUpgradeCore, 1, sourceMeta),
                                                                 new ItemStack(BetterBarrels.itemUpgradeCore, 1, sourceMeta)}
      ));
      GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(BetterBarrels.itemUpgradeCore, 3, sourceMeta),
                                                    new Object[]{new ItemStack(BetterBarrels.itemUpgradeCore, 1, resultMeta)}
      ));
	}
	
	private void addStructuralUpgrade(int level, String variableComponent){
		String type     = variableComponent.split("\\.")[0];
		String compoStr = variableComponent.split("\\.")[1];
		this.addStructuralUpgrade_(level, compoStr);
		
		/*
		if (type.equals("Ore"))
			this.addStructuralUpgrade_(level, compoStr);

		if (type.equals("Block")){
			try {
				Block compo = (Block)Class.forName("net.minecraft.block.Block").getField(compoStr).get(null);
				this.addStructuralUpgrade_(level, compo);
			} catch (Exception e) {
				BetterBarrels.log.severe("Error while trying to register recipe with material " + variableComponent);
			}
		}

		if (type.equals("Item")){
			try {
				Item compo = (Item)Class.forName("net.minecraft.item.Item").getField(compoStr).get(null);
				this.addStructuralUpgrade_(level, compo);
			} catch (Exception e) {
				BetterBarrels.log.severe("Error while trying to register recipe with material " + variableComponent);
			}
		}
		*/		
		
	}
	
	private void addStructuralUpgrade_(int level, Item variableComponent){
		GameRegistry.addRecipe(new ItemStack(BetterBarrels.itemUpgradeStructural,1,level), new Object[] 
				{"PBP", "B B", "PBP",
				'P', upgradeItem, 
				'B', variableComponent});		
	}

	private void addStructuralUpgrade_(int level, Block variableComponent){
		GameRegistry.addRecipe(new ItemStack(BetterBarrels.itemUpgradeStructural,1,level), new Object[] 
				{"PBP", "B B", "PBP",
				'P', upgradeItem, 
				'B', new ItemStack(variableComponent,1)});		
	}	
	
	private void addStructuralUpgrade_(int level, String variableComponent){
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterBarrels.itemUpgradeStructural,1,level), new Object[] 
				{"PBP", "B B", "PBP",
				Character.valueOf('P'), upgradeItem, 
				Character.valueOf('B'), variableComponent}));		
	}	
	
	private void addCoreUpgrade(int meta, Item variableComponent){
		GameRegistry.addRecipe(new ItemStack(BetterBarrels.itemUpgradeCore,1,meta), new Object[] 
				{" P ", " B ", " P ",
				'P', Blocks.piston, 
				'B', variableComponent});		
	}

	private void addCoreUpgrade(int meta, Block variableComponent){
		GameRegistry.addRecipe(new ItemStack(BetterBarrels.itemUpgradeCore,1,meta), new Object[] 
				{" P ", " B ", " P ",
				'P', Blocks.piston, 
				'B', new ItemStack(variableComponent,1)});		
	}	
	
	private void addCoreUpgrade(int meta, String variableComponent){
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterBarrels.itemUpgradeCore,1,meta), new Object[] 
				{" P ", " B ", " P ",
				Character.valueOf('P'), Blocks.piston,
				Character.valueOf('B'), variableComponent}));		
	}	
	
	private void addSideUpgrade(int meta, Item variableComponent){
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterBarrels.itemUpgradeSide,4,meta), new Object[] 
				{" P ", "PBP", " P ",
				Character.valueOf('P'), "plankWood", 
				Character.valueOf('B'), variableComponent}));		
	}

	private void addSideUpgrade(int meta, Block variableComponent){
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterBarrels.itemUpgradeSide,4,meta), new Object[] 
				{" P ", "PBP", " P ",
				Character.valueOf('P'), "plankWood",
				Character.valueOf('B'), new ItemStack(variableComponent,1)}));		
	}	
	
	private void addSideUpgrade(int meta, String variableComponent){
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterBarrels.itemUpgradeSide,4,meta), new Object[] 
				{" P ", "PBP", " P ",
				Character.valueOf('P'), "plankWood",
				Character.valueOf('B'), variableComponent}));		
	}	
}
