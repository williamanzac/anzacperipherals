package anzac.peripherals.utility.converters;

public class EnumConverter<E extends Enum<?>> implements Converter<E> {

	@SuppressWarnings("unchecked")
	@Override
	public Object javaToLUA(final Object object) {
		if (object != null) {
			return ((E) object).name();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public E luaToJava(final Object object) {
		final E[] enumConstants = (E[]) object.getClass().getEnumConstants();
		if (object instanceof Number) {
			final int ord = ((Number) object).intValue();
			if (ord >= 0 && ord < enumConstants.length) {
				return enumConstants[ord];
			}
		} else if (object instanceof String) {
			final String name = ((String) object).toUpperCase();
			for (final E e : enumConstants) {
				if (e.name().equals(name)) {
					return e;
				}
			}
		}
		throw new RuntimeException("Unexpected value");
	}
}
