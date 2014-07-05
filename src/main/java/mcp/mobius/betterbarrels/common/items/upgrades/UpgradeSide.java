package mcp.mobius.betterbarrels.common.items.upgrades;

import mcp.mobius.betterbarrels.BetterBarrels;
import net.minecraft.item.Item;

public class UpgradeSide {
	/* UPGRADE VALUES */
	public static int NONE     = 0x0; 
	public static int FRONT    = 0x1;
	public static int STICKER  = 0x2;
	public static int HOPPER   = 0x3;
	public static int REDSTONE = 0x4;
	
	/* UPGRADE META */
	public static int RS_FULL  = 0x0;
	public static int RS_EMPT  = 0x1;
	public static int RS_PROP  = 0x2;
	
	
	public static Item[] mapItem = {
		null,
		null,
		BetterBarrels.itemUpgradeSide,
		BetterBarrels.itemUpgradeSide,
		BetterBarrels.itemUpgradeSide,
	};
	
	public static int[] mapMeta = {
		0,
		0,
		0,
		1,
		2
	};
	
	public static int[] mapRevMeta = {
		STICKER,
		HOPPER,
		REDSTONE,
	};
	
	public static int[] mapReq = {
		-1,
		-1,
		-1,
		UpgradeCore.Type.HOPPER.ordinal(),
		UpgradeCore.Type.REDSTONE.ordinal()
	};
}
