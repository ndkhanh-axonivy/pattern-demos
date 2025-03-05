package com.axonivy.demo.patterndemos.placeholderevaluator;

import com.axonivy.demo.patterndemos.enums.Salutation;

public class PlaceholderEvaluatorBean {

	private String textForReplacement;
	private String title;
	private String firstName;
	private String lastName;
	private String title2;

	private Salutation selectedSalutation;

	public String getTextForReplacement() {
		return textForReplacement;
	}

	/**
	 * @param textForReplacement the textForReplacement to set
	 */
	public void setTextForReplacement(String textForReplacement) {
		this.textForReplacement = textForReplacement;
	}

	public void setSalutationToText() {
		this.textForReplacement = selectedSalutation.getCmsFormat();
	}
	
	public void replacePlaceholders() {
		this.textForReplacement = Salutation.fillDataInSalutation(textForReplacement, title, firstName, lastName, title2);
	}
	
	public void reset() {
		this.textForReplacement = selectedSalutation.getCmsFormat();
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * @return the title2
	 */
	public String getTitle2() {
		return title2;
	}

	/**
	 * @param title2 the title2 to set
	 */
	public void setTitle2(String title2) {
		this.title2 = title2;
	}

	/**
	 * @return the selectedSalutation
	 */
	public Salutation getSelectedSalutation() {
		return selectedSalutation;
	}

	/**
	 * @param selectedSalutation the selectedSalutation to set
	 */
	public void setSelectedSalutation(Salutation selectedSalutation) {
		this.selectedSalutation = selectedSalutation;
	}

	/**
	 * Gets Salutations for the drop down menus
	 *
	 * @return
	 */
	public Salutation[] getSalutations() {
		return Salutation.values();
	}

}
