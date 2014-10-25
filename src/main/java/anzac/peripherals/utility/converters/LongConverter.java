package anzac.peripherals.utility.converters;

public class LongConverter extends NumberConverter<Long> {
	@Override
	protected Long toNumber(Object object) {
		if (object instanceof Number) {
			return ((Number) object).longValue();
		} else if (object instanceof String) {
			return Long.parseLong((String) object);
		}
		throw new RuntimeException("Expected a Number");
	}
}
