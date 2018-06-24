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
import org.imixs.workflow.engine.ModelService;
import org.imixs.workflow.engine.WorkflowService;
import org.imixs.workflow.exceptions.AccessDeniedException;
import org.imixs.workflow.exceptions.ModelException;
import org.imixs.workflow.exceptions.PluginException;
import org.imixs.workflow.exceptions.ProcessingErrorException;
import org.imixs.workflow.jaxrs.WorkflowRestService;

/**
 * Controller to manage active imixs-workflow instances.
 * 
 * @author rsoika
 *
 */
@Controller
@Path("ticket")
public class TicketController { // extends WorkflowController

	private static Logger logger = Logger.getLogger(TicketController.class.getName());

	@Inject
	org.imixs.application.example.mvc.Model model;

	@Inject
	protected Event<ModelEvent> events;

	@EJB
	protected ModelService modelService;

	@EJB
	WorkflowService workflowService;

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
		ItemCollection workitem = null;
		int iTask = Integer.parseInt(taskid);
		String uid = WorkflowKernel.generateUniqueID();
		workitem = new ItemCollection().model(modelversion).task(iTask);
		workitem.replaceItemValue(WorkflowKernel.UNIQUEID, uid);
		workitem.replaceItemValue("type", "workitem");
		events.fire(new ModelEvent(workitem, ModelEvent.WORKITEM_CREATED));
		model.setWorkitem(workitem);
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
		ItemCollection workitem = workflowService.getWorkItem(uid);
		model.setWorkitem(workitem);
		events.fire(new ModelEvent(workitem, ModelEvent.WORKITEM_CHANGED));
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
	@Path("{uniqueid : ([0-9a-f]{8}-.*|[0-9a-f]{11}-.*)}")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	public String processTicket(@PathParam("uniqueid") String uid, InputStream requestBodyStream) {
		ItemCollection workitem = null;
		try {
			// process the workItem.
			workitem = WorkflowRestService.parseWorkitem(requestBodyStream);
			workitem.replaceItemValue(WorkflowKernel.UNIQUEID, uid);
			workitem = workflowService.processWorkItem(workitem);
			model.setWorkitem(workitem);
		} catch (AccessDeniedException | ProcessingErrorException | PluginException | ModelException e) {
			logger.severe("Unable to process Ticket instance: " + e.getMessage());
		}

		// compute workflow result (see the workflow model for details)
		String result = workitem.getItemValueString("action");
		if (result.isEmpty()) {
			result = "ticket.xhtml";
		}
		logger.finest("...workflow result => " + result);

		return result;
	}

	/**
	 * load task list for the current user
	 * 
	 * @return tasklist.xhtml
	 */
	@GET
	@Path("tasklist")
	public String showTaskList() {
		model.setTasklist(workflowService.getWorkListByAuthor(null, "workitem", 30, 0, "$modified", true));
		return "tasklist.xhtml";
	}

	/**
	 * load status list of current user
	 * 
	 * @return statuslist.xhtml
	 */
	@GET
	@Path("statuslist")
	public String showStatuslist() {
		model.setTasklist(workflowService.getWorkListByCreator(null, "workitem", 30, 0, "$modified", true));
		return "statuslist.xhtml";
	}

	/**
	 * load list of archived workitem
	 * 
	 * @return statuslist.xhtml
	 */
	@GET
	@Path("archive")
	public String showArchive() {
		model.setTasklist(workflowService.getDocumentService().getDocumentsByType("workitemarchive"));
		return "statuslist.xhtml";
	}

}