package org.imixs.application.mvc.controller;

import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.inject.Named;
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
 * Controller to manage active imixs-workflow instances.
 * 
 * @author rsoika
 *
 */
@Controller
@Path("teams")
@Named
public class TeamController {

	public static final String WORKITEM_TYPE = "team";

	private static Logger logger = Logger.getLogger(TeamController.class.getName());
	private ItemCollection workitem = new ItemCollection();

	@EJB
	DocumentService documentService;

	@GET
	public String showTeams() {
		return "teams.xhtml";
	}

	
	@GET
	@Path("{uniqueid}")
	public String getTeam(@PathParam("uniqueid") String uid) {
		
		logger.info("......load team: " + uid);
		workitem =documentService.load(uid);
		return "team.xhtml";
	}

	
	@POST
	public String createTeam() {
		String uid = WorkflowKernel.generateUniqueID();
		logger.info("......create new team: " + uid);
		workitem = new ItemCollection();
		workitem.replaceItemValue(WorkflowKernel.UNIQUEID, uid);
		workitem.replaceItemValue("type", WORKITEM_TYPE);
		return "team.xhtml";
	}

	@POST
	@Path("{uniqueid}")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	public String saveTeam(@PathParam("uniqueid") String uid, InputStream requestBodyStream) {

		logger.finest("......postFormWorkitem @POST /workitem  method:postWorkitem....");
		// parse the workItem.
		workitem = WorkflowRestService.parseWorkitem(requestBodyStream);
		logger.info("......save team uniqueid=" + uid);
		// save workItem ...
		workitem.replaceItemValue("type", WORKITEM_TYPE);
		workitem = documentService.save(workitem);
		logger.finest("......ItemCollection saved");
		return "teams.xhtml";
	}

	public ItemCollection getWorkitem() {
		return workitem;
	}

	public void setWorkitem(ItemCollection workitem) {
		this.workitem = workitem;
	}

	public List<ItemCollection> getTeams() {
		logger.info("......load teams.");
		List<ItemCollection> result = documentService.getDocumentsByType(WORKITEM_TYPE);
		return result;

	}
}