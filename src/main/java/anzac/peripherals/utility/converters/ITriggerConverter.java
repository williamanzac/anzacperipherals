package anzac.peripherals.utility.converters;

import java.util.HashMap;
import java.util.Map;

import buildcraft.api.gates.ITrigger;

public class ITriggerConverter implements Converter<ITrigger> {

	private static final String REQUIRES_PARAMETER = "requiresParameter";
	private static final String HAS_PARAMETER = "hasParameter";
	private static final String DESCRIPTION = "description";
	private static final String UNIQUE_TAG = "uniqueTag";

	@Override
	public Object javaToLUA(Object object) {
		final ITrigger trigger = (ITrigger) object;
		final String uniqueTag = trigger.getUniqueTag();
		final String description = trigger.getDescription();
		final boolean hasParameter = trigger.hasParameter();
		final boolean requiresParameter = trigger.requiresParameter();
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put(UNIQUE_TAG, uniqueTag);
		map.put(DESCRIPTION, description);
		map.put(HAS_PARAMETER, hasParameter);
		map.put(REQUIRES_PARAMETER, requiresParameter);
		return map;
	}

	@Override
	public ITrigger luaToJava(Object object) {
		// for now no way to send trigger from lua to java
		return null;
	}

}
