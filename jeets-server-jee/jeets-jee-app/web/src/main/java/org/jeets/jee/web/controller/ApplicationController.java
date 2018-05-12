package org.jeets.jee.web.controller;

import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jeets.ear.ejb.ApplicationBean;
import org.jeets.model.traccar.jpa.Device;

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
	
    public List<Device> getVehicles() {
        return appBean.getVehicles();
    }

    public int getMessageCount() {
        return appBean.getMessageCount();
    }

    public Device getLastMessage() {
        return appBean.getLastMessage();
    }
    
    /** selected VehicleId from dropdown list. */
    private String selectedVehicleId = null;
    public String getSelectedVehicleId() { return selectedVehicleId; }
    public void setSelectedVehicleId(String selectedVehicleId) {
        this.selectedVehicleId = selectedVehicleId;
    }

    /**
     * This Listener is triggered by the selectOneMenu "dropdown"
     * and provides the vehicle for further processing.
     */
    public void selectVehicleListener(AjaxBehaviorEvent event) {

        if (selectedVehicleId == null)      // first entry serves as label
            vehicleList = new ArrayList<Device>();   //  empty list
        else {
            System.out.println("selected vehicle " + selectedVehicleId);

//            vehicleList = dbAdmin.lookupProjectConfigEntities( project );
//            logger.info(vehicleList.size() + " prjCongigs found");
//            if (vehicleList.size() == 0) {
//                String msg = "PrjCatID " + selectedVehicleId + " has no configurations in database!";
//                facesContext.addMessage(null, 
//                        new FacesMessage( FacesMessage.SEVERITY_WARN, msg, null) );
        }
        setModel();
    }

    /**
     * List of Entities for direct database synchronization via EntityManager.
     * List is only holding {@link Device} Entities for the
     * {@code selectedVehicle} of the dropdown component. Only getter supplied,
     * setter is applied via {@link #selectVehicleListener(AjaxBehaviorEvent)}.
     * An empty list has to be supplied for the getter.
     */
    private List<Device> vehicleList;

    /** JSF keeps track of rows with a DataModel */
    private transient DataModel<Device> model;
    public void setModel() {
        vehicleList = appBean.getVehicles();
        model = new ListDataModel<Device>(vehicleList);
    }   
    public DataModel<Device> getModel() { return model; }  

//	@PostConstruct
//	public void init() {
//	    System.out.println("TODO: initialize application with DB configuration etc. ...");
//	}

}
