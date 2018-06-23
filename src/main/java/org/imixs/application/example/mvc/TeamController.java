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

import org.imixs.workflow.mvc.controller.DocumentController;

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
public class TeamController extends DocumentController {

	private static Logger logger = Logger.getLogger(TeamController.class.getName());

	@Inject
	Model model;


	/**
	 * load list of teams (default resource).
	 * 
	 * @return teams.xhtml
	 */
	@GET
	public String showTeams() {
		model.setTeams(super.findDocumentsByType("team"));
		return "teams.xhtml";
	}


	/**
	 * create new team...
	 * 
	 * @return
	 */
	@GET
	@Path("create")
	public String createNewTicket() {
		logger.fine("create new team...");
		model.setTeam(super.createDocument("team"));
		return "team.xhtml";
	}

	/**
	 * load team
	 * 
	 * @param uid
	 * @return
	 */
	@GET
	@Path("edit/{uniqueid}")
	public String editTicket(@PathParam("uniqueid") String uid) {

		logger.fine("load team...");
		model.setTeam(super.findDocumentByUnqiueID(uid));
		return "team.xhtml";
	}
	

	@GET
	@Path("/delete/{uniqueid}")
	public String actionDeleteDocument(@PathParam("uniqueid") String uniqueid) {
		logger.finest("......delete document: " + uniqueid);
		this.documentService.remove(super.findDocumentByUnqiueID(uniqueid));
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
	@Path("{uniqueid}")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	public String saveTeam(@PathParam("uniqueid") String uid, InputStream requestBodyStream) {
		model.setTeam(super.saveDocument(uid, requestBodyStream));
		// update teams....
		model.setTeams(super.findDocumentsByType("team"));
		return "teams.xhtml";
	}



	
}