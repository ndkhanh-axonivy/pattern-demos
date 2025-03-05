package com.axonivy.demo.patterndemos.enums;

import com.axonivy.demo.patterndemos.service.PlaceholderEvaluator;
import com.axonivy.utils.persistence.enums.HasCmsName;

public enum SalutationPlaceholder implements HasCmsName, Placeholder {
	TITLE_BEFORE("Titel vorangestellt"),
	FIRSTNAME("Vorname"),
	LASTNAME("Nachname"),
	TITLE_AFTER("Titel nachgestellt");

	private String placeholderName;

	SalutationPlaceholder(String placeholderName) {
		this.placeholderName = placeholderName;
	}

	@Override
	public String getPlaceholderName() {
		return placeholderName;
	}
}

class SalutationPlaceholderEvaluator extends PlaceholderEvaluator<SalutationPlaceholder> {
	private String title;
	private String firstName;
	private String lastName;
	private String title2;

	public SalutationPlaceholderEvaluator(String title, String firstName, String lastName, String title2) {
		super();
		this.title = title;
		this.firstName = firstName;
		this.lastName = lastName;
		this.title2 = title2;
	}

	@Override
	public String evaluate(SalutationPlaceholder ph) {
		String result = null;
		switch (ph) {
		case TITLE_BEFORE:
			result = title;
			break;
		case FIRSTNAME:
			result = firstName;
			break;
		case LASTNAME:
			result = lastName;
			break;
		case TITLE_AFTER:
			result = title2;
			break;
		default:
			break;

		}
		return result;
	}

	@Override
	public Class<SalutationPlaceholder> getType() {
		return SalutationPlaceholder.class;
	}
}

