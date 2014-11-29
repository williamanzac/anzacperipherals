package anzac.peripherals.utility;

import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class FluidUtils {

	public static FluidTankInfo[] contents(final IFluidHandler handler, final ForgeDirection from) {
		return handler.getTankInfo(from);
	}
}
