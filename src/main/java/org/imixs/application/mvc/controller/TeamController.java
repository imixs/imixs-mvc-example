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
import org.imixs.workflow.mvc.controller.DocumentController;

/**
 * Controller to manage active imixs-workflow instances.
 * 
 * @author rsoika
 *
 */
@Controller
@Path("teams")
@Named
public class TeamController extends DocumentController {

	private static Logger logger = Logger.getLogger(TeamController.class.getName());

	/**
	 * Initialize TeamController
	 */
	public TeamController() {
		super();
		setDocumentType("team");
		setDocumentView("team.xhtml");
		setDocumentsView("teams.xhtml");
	}

}