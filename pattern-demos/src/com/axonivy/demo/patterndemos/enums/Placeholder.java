package com.axonivy.demo.patterndemos.enums;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Placeholder {
	public static final Map<Class<? extends Placeholder>, Map<String, ? extends Placeholder>> NAME_MAP = new HashMap<>();
	public String getPlaceholderName();

	@SuppressWarnings("unchecked")
	public static <T extends Placeholder> T findByPlaceholderName(Class<T> type, String name) {
		Map<String, ? extends Placeholder> map = NAME_MAP.get(type);
		if(map == null) {
			map = Stream.of(type.getEnumConstants()).collect(Collectors.toMap(e -> e.getPlaceholderName(), e -> e));
			NAME_MAP.put(type, map);
		}
		return (T)map.get(name);
	}
}