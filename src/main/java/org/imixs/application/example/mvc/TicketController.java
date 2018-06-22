package org.imixs.application.example.mvc;

import java.util.logging.Logger;

import javax.inject.Named;
import javax.mvc.annotation.Controller;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.imixs.workflow.mvc.controller.WorkflowController;

/**
 * Controller to manage active imixs-workflow instances.
 * 
 * @author rsoika
 *
 */
@Controller
@Path("ticket")
@Named
public class TicketController extends WorkflowController {

	private static Logger logger = Logger.getLogger(TicketController.class.getName());

	/**
	 * Initialize TeamController
	 */
	public TicketController() {
		super();
		
	}
	
	
	@GET
	@Path("{modelversion}/{taskid}")
	public String createNewTicket(@PathParam("modelversion") String modelversion, @PathParam("taskid") String taskid) {
		
		super.createWorkitem(modelversion, taskid);
		return "ticket.xhtml";
	}


}