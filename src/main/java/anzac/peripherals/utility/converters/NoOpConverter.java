package anzac.peripherals.utility.converters;

public abstract class NoOpConverter<J> implements Converter<J> {

	@Override
	public Object javaToLUA(final Object object) {
		// no conversion
		return object;
	}

	@SuppressWarnings("unchecked")
	@Override
	public J luaToJava(final Object object) {
		// no conversion
		return (J) object;
	}
}
