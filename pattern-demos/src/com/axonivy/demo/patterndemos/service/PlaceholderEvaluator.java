package com.axonivy.demo.patterndemos.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.axonivy.demo.patterndemos.enums.Placeholder;

public abstract class PlaceholderEvaluator<T extends Placeholder> {
	protected Set<T> missingPh = new HashSet<>();
	protected Map<T, String> phCache = new HashMap<>();
	public abstract String evaluate(T ph);
	public abstract Class<T> getType();

	public String evaluateWithCaching(T ph) {
		String result = null;
		if (ph != null) {
			String value = phCache.get(ph);
			if (value == null && !missingPh.contains(ph)) {
				value = evaluate(ph);
				if (StringUtils.isBlank(value)) {
					missingPh.add(ph);
					value = "";
				}
				phCache.put(ph, value);
			}
			if(value != null) {
				result = value;
			}
		}
		return result;
	}
}

