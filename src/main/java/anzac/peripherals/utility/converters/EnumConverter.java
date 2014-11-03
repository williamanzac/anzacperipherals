package anzac.peripherals.utility.converters;

public abstract class EnumConverter<E extends Enum<?>> implements Converter<E> {

	@SuppressWarnings("unchecked")
	@Override
	public Object javaToLUA(final Object object) {
		if (object != null) {
			return ((E) object).name();
		}
		return null;
	}
}
