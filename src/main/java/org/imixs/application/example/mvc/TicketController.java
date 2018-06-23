package org.imixs.application.example.mvc;

import java.io.InputStream;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.mvc.annotation.Controller;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.imixs.workflow.exceptions.AccessDeniedException;
import org.imixs.workflow.exceptions.ModelException;
import org.imixs.workflow.exceptions.PluginException;
import org.imixs.workflow.exceptions.ProcessingErrorException;
import org.imixs.workflow.mvc.controller.WorkflowController;

/**
 * Controller to manage active imixs-workflow instances.
 * 
 * @author rsoika
 *
 */
@Controller
@Path("ticket")
public class TicketController extends WorkflowController {

	private static Logger logger = Logger.getLogger(TicketController.class.getName());

	
	@Inject
	Model model;
	
	
	/**
	 * load task list for the current user
	 * 
	 * @return tasklist.xhtml
	 */
	@GET
	@Path("tasklist")
	public String showTaskList() {
		model.setTasklist(super.getTaskList());
		return "tasklist.xhtml";
	}
	
	
	/**
	 * load list of teams (default resource).
	 * 
	 * @return statuslist.xhtml
	 */
	@GET
	@Path("statuslist")
	public String showStatuslist() {
		model.setTasklist(super.getStatusList());
		return "statuslist.xhtml";
	}

	
	
	/**
	 * Create a new ticket.
	 * 
	 * @param modelversion
	 * @param taskid
	 * @return "ticket.xhtml"
	 */
	@GET
	@Path("{modelversion}/{taskid}")
	public String createNewTicket(@PathParam("modelversion") String modelversion, @PathParam("taskid") String taskid) {
		logger.fine("...create ticket");
		try {
			model.setWorkitem(super.createWorkitem(modelversion, taskid));
		} catch (ModelException e) {
			logger.severe("Unable to create new Ticket instance: " + e.getMessage());
		}
		return "ticket.xhtml";
	}


	/**
	 * load an existing ticket 
	 * 
	 * @param uid
	 * @return
	 */
	@GET
	@Path("edit/{uniqueid}")
	public String editTicket(@PathParam("uniqueid") String uid) {

		logger.fine("load ticket...");
		model.setWorkitem(super.findDocumentByUnqiueID(uid));
		return "ticket.xhtml";
	}
	
	
	/**
	 * Process the ticket instance.
	 * 
	 * @param uid
	 * @param requestBodyStream
	 * @return
	 */
	@POST
	@Path("{uniqueid}")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	public String processTicket(@PathParam("uniqueid") String uid, InputStream requestBodyStream) {
		try {
			model.setWorkitem(super.processWorkitem(uid, requestBodyStream));
		} catch (AccessDeniedException | ProcessingErrorException | PluginException | ModelException e) {
			logger.severe("Unable to process Ticket instance: " + e.getMessage());
		}
		// update teams....
		model.setTeams(super.findDocumentsByType("team"));
		return "redirect:home";
	}
	

}