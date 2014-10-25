package anzac.peripherals.utility.converters;

public class StringConverter extends NoOpConverter<String> {

	@Override
	public String luaToJava(final Object object) {
		if (object != null) {
			// convert to string
			return object.toString();
		}
		return null;
	}
}
