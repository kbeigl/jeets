package org.jeets.jee.web.controller;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import java.io.Serializable;
import java.util.Date;
import org.jeets.ear.ejb.ApplicationBean;
import org.slf4j.Logger;

/** A managed bean used to inspect the Application EJB. */
//@Named("appcontroller")
@ManagedBean(name="appcontroller")
@ApplicationScoped
public class ApplicationController implements Serializable {

	private static final long serialVersionUID = 1L;
//	@Inject
//	private transient Logger logger;	
//	@Inject
//	private FacesContext facesContext;
	@EJB
    private ApplicationBean appBean;
//	@EJB
//	private AdminDAO dbAdmin;

	/**
	 * Currently the page has to be refreshed manually
	 * and the current time is displayed on the panel.
	 */
	public Date getCurrentTime() { return new Date(); }

    /* lookup export_interval in database to preset controlpanel for timer service
	@PostConstruct
	public void init() {
	    System.out.println("TODO: initialize application with DB configuration etc. ...");
	}
	 */

}
