package anzac.peripherals.utility.converters;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;

public class FluidTankInfoConverter implements Converter<FluidTankInfo> {

	private static final String FLUID = "fluid";
	private static final String CAPACITY = "capacity";
	private FluidStackConverter converter = new FluidStackConverter();

	@Override
	public Object javaToLUA(final Object object) {
		final FluidTankInfo tank = (FluidTankInfo) object;
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put(CAPACITY, tank.capacity);
		map.put(FLUID, converter.javaToLUA(tank.fluid));
		return map;
	}

	@SuppressWarnings("unchecked")
	@Override
	public FluidTankInfo luaToJava(final Object object) {
		final Map<String, Object> map = (Map<String, Object>) object;
		final int capacity = ((Double) map.get(CAPACITY)).intValue();
		final FluidStack fluid = converter.luaToJava(map.get(FLUID));
		final FluidTankInfo tank = new FluidTankInfo(fluid, capacity);
		return tank;
	}
}
