package anzac.peripherals.utility.converters;

import net.minecraftforge.common.util.ForgeDirection;

public class DirectionConverter extends EnumConverter<ForgeDirection> {
	@Override
	public ForgeDirection luaToJava(Object object) {
		if (object instanceof Number) {
			return ForgeDirection.getOrientation(((Number) object).intValue());
		} else if (object instanceof String) {
			return ForgeDirection.valueOf(((String) object).toUpperCase());
		}
		throw new RuntimeException("Expected a Direction");
	}
}
