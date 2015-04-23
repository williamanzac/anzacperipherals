package anzac.peripherals.init;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import anzac.peripherals.utility.LogHelper;
import cpw.mods.fml.common.Loader;

public class ModFluids {

	// public static Fluid xpJuice;

	public static void init() {
		if (Loader.isModLoaded("OpenBlocks")) {
			LogHelper.info("XP Juice regististration left to Open Blocks.");
		} else {
			LogHelper.info("XP Juice registered by Anzac Peripherals.");
			final Fluid xpJuice = new Fluid("xpjuice").setLuminosity(10).setDensity(800).setViscosity(1500)
					.setUnlocalizedName("anzac.xpjuice");
			FluidRegistry.registerFluid(xpJuice);
		}
		// xpJuice = FluidRegistry.getFluid("xpjuice");
	}
}
