package anzac.peripherals.utility.converters;

public class IntConverter extends NumberConverter<Integer> {
	@Override
	protected Integer toNumber(Object object) {
		if (object instanceof Number) {
			return ((Number) object).intValue();
		} else if (object instanceof String) {
			return Integer.parseInt((String) object);
		}
		throw new RuntimeException("Expected a Number");
	}
}
