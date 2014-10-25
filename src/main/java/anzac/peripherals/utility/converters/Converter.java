package anzac.peripherals.utility.converters;

public interface Converter<J extends Object> {
	public Object javaToLUA(final Object object);

	public J luaToJava(final Object object);
}
