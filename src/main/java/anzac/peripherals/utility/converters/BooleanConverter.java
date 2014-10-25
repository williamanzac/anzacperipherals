package anzac.peripherals.utility.converters;

public class BooleanConverter extends NoOpConverter<Boolean> {

	@Override
	public Boolean luaToJava(final Object object) {
		if (object instanceof Boolean) {
			return ((Boolean) object).booleanValue();
		} else if (object instanceof String) {
			return Boolean.parseBoolean((String) object);
		}
		throw new RuntimeException("Expected a Boolean");
	}
}
