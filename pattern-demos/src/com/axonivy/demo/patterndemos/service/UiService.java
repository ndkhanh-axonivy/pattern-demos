package com.axonivy.demo.patterndemos.service;

import java.util.List;

import javax.faces.application.FacesMessage;

import ch.ivyteam.ivy.environment.Ivy;

/**
 * Service class for UI operations.
 *
 */
public class UiService {
	private static final UiService INSTANCE = new UiService();

	protected UiService() {
	}

	/**
	 * Get service instance.
	 *
	 * @return Instance of the class
	 */
	public static UiService get() {
		return INSTANCE;
	}
	
	/**
	 * Create Faces Message error.
	 *
	 * @param cmsUri
	 * @param objects
	 * @return FacesMessage
	 */
	public FacesMessage createFacesMessageCmsError(String cmsUri, Object...objects) {
		String msg = Ivy.cms().co(cmsUri, List.of(objects));
		return new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg);
	}

}
