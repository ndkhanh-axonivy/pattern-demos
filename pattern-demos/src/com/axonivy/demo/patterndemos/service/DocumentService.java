package com.axonivy.demo.patterndemos.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.axonivy.demo.patterndemos.enums.Placeholder;

public class DocumentService {
	private static final DocumentService INSTANCE = new DocumentService();
	public static final Pattern pattern = Pattern.compile("\\{(.*?)\\}");
	
	/**
	 * Gets the service instance.
	 *
	 * @return DocumentService
	 */
	public static DocumentService get() {
		return INSTANCE;
	}
	
	/**
	 * Replace placeholders in a textblock and return the replaced text and a collection of missing placeholders.
	 *
	 * @param content
	 * @param evaluator
	 * @return replaced String
	 */
	public <T extends Placeholder> String replacePlaceholders(String content, PlaceholderEvaluator<T> evaluator) {
		StringBuffer buffer = new StringBuffer();
		Matcher replaceMatcher = pattern.matcher(content);
		while (replaceMatcher.find()) {
			T ph = Placeholder.findByPlaceholderName(evaluator.getType(), replaceMatcher.group(1));
			replaceMatcher.appendReplacement(buffer, (ph != null) ? evaluator.evaluateWithCaching(ph) : replaceMatcher.group(0));
		}
		replaceMatcher.appendTail(buffer);
		return buffer.toString();
	}
}
