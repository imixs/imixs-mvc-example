package org.imixs.application.example.mvc;

import java.io.InputStream;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.mvc.annotation.Controller;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.WorkflowKernel;
import org.imixs.workflow.engine.DocumentService;
import org.imixs.workflow.jaxrs.WorkflowRestService;

/**
 * The TeamController manages the creation, save and editing of team instances
 * under the resource /teams. The controller inject the CDI-Model bean to store
 * a team instance during a request. The controller extends the
 * org.imixs.workflow.mvc.controller.DocumentController
 * 
 * @author rsoika
 *
 */
@Controller 
@Path("teams")
public class TeamController {

	private static Logger logger = Logger.getLogger(TeamController.class.getName());

	@Inject
	Model model;
	

	@Inject
	protected Event<ModelEvent> events;

	
	@EJB
	protected DocumentService documentService;


	/**
	 * load list of teams (default resource).
	 * 
	 * @return teams.xhtml
	 */
	@GET
	public String showTeams() {
		model.setTeams(documentService.getDocumentsByType("team"));
		return "teams.xhtml";
	}


	/**
	 * Creates a new instance of an Imixs ItemCollection with a $uniqueID and the
	 * specified type property. The method also fires a CDI event to allow other CDI
	 * beans to intercept the creation method.
	 * 
	 * @param type
	 * @return instance of ItemCollection
	 */
	@GET
	@Path("create")
	public String createNewTicket() {
		logger.fine("create new team...");
		
		String uid = WorkflowKernel.generateUniqueID();
		logger.info("......create new document: " + uid);
		ItemCollection workitem = new ItemCollection();
		workitem.replaceItemValue(WorkflowKernel.UNIQUEID, uid);
		workitem.replaceItemValue("type", "team");
		events.fire(new ModelEvent(workitem, ModelEvent.WORKITEM_CREATED));
		
		model.setTeam(workitem);
		return "team.xhtml";
	}

	/**
	 * Finds an instance of ItemCollection by $uniqueID.
	 * 
	 * @param uid
	 * @return
	 */
	@GET
	@Path("edit/{uniqueid}")
	public String editTicket(@PathParam("uniqueid") String uid) {

		logger.fine("load team...");
		model.setTeam(documentService.load(uid));
		return "team.xhtml";
	}
	

	@GET
	@Path("/delete/{uniqueid}")
	public String actionDeleteDocument(@PathParam("uniqueid") String uniqueid) {
		logger.finest("......delete document: " + uniqueid);
		this.documentService.remove(documentService.load(uniqueid));
		return "redirect:teams/";
	}
	
	/**
	 * Save the team instance.
	 * 
	 * @param uid
	 * @param requestBodyStream
	 * @return
	 */
	@POST
	@Path("{uniqueid : ([0-9a-f]{8}-.*|[0-9a-f]{11}-.*)}")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	public String saveTeam(@PathParam("uniqueid") String uid, InputStream requestBodyStream) {
		// parse the workItem.
		ItemCollection document = WorkflowRestService.parseWorkitem(requestBodyStream);
		document.replaceItemValue(WorkflowKernel.UNIQUEID, uid);

		// try to load current instance of this document entity
		ItemCollection currentInstance = documentService.load(uid);
		if (currentInstance != null) {
			// merge entity into current instance
			// an instance of this Entity still exists! so we update the
			// new values here....
			currentInstance.replaceAllItems(document.getAllItems());
			document = currentInstance;
		}

		// save workItem ...
		logger.info("......save document uniqueid=" + uid);
		events.fire(new ModelEvent(document, ModelEvent.WORKITEM_BEFORE_SAVE));
		document = documentService.save(document);
		events.fire(new ModelEvent(document, ModelEvent.WORKITEM_AFTER_SAVE));
		logger.finest("......ItemCollection saved");
		model.setTeam(document);
		
		
		return "redirect:teams";
	}



	
}