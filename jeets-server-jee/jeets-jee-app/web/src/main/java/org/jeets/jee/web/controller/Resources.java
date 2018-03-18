package org.jeets.jee.web.controller;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;

/** Resources for web application only! */
public class Resources {

	@Produces
	@RequestScoped
	public FacesContext produceFacesContext() {
		return FacesContext.getCurrentInstance();
	}
}