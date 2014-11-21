package anzac.peripherals.utility.converters;

import java.util.HashMap;
import java.util.Map;

import anzac.peripherals.peripherals.Target;
import anzac.peripherals.utility.Position;

public class TargetConverter implements Converter<Target> {

	@Override
	public Object javaToLUA(final Object object) {
		final Target target = (Target) object;
		final Map<String, Integer> table = new HashMap<String, Integer>();
		table.put("dimension", target.dimension);
		table.put("x", target.position.x);
		table.put("y", target.position.y);
		table.put("z", target.position.z);
		return table;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Target luaToJava(final Object object) {
		final Target target = new Target();
		final Map<String, Double> table = (Map<String, Double>) object;
		target.dimension = table.get("dimension").intValue();
		final int x = table.get("x").intValue();
		final int y = table.get("y").intValue();
		final int z = table.get("z").intValue();
		target.position = new Position(x, y, z);
		return target;
	}
}
