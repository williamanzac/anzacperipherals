package anzac.peripherals.init;

import java.util.HashSet;
import java.util.Set;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import anzac.peripherals.upgrades.FurnaceUpgrade;
import anzac.peripherals.upgrades.PeripheralUpgrade;
import anzac.peripherals.utility.UpgradeUtils;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.turtle.ITurtleUpgrade;

public class TurtleUpgrades {
	public static final Set<ITurtleUpgrade> upgrades = new HashSet<ITurtleUpgrade>();

	public static class ForgeHandlers {
		@SubscribeEvent
		@SideOnly(Side.CLIENT)
		public void onPreTextureStitch(final TextureStitchEvent.Pre event) {
			for (final ITurtleUpgrade upgrade : upgrades) {
				if (upgrade instanceof PeripheralUpgrade) {
					((PeripheralUpgrade) upgrade).registerIcons(event.map);
				}
			}
		}
	}

	public static void init() {
		upgrades.add(new FurnaceUpgrade(UpgradeUtils.nextUpgradeId()));
		for (ITurtleUpgrade upgrade : upgrades) {
			ComputerCraftAPI.registerTurtleUpgrade(upgrade);
		}
		MinecraftForge.EVENT_BUS.register(new ForgeHandlers());
	}
}
