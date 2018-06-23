package org.imixs.application.example.mvc;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.mvc.annotation.Controller;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.imixs.workflow.exceptions.AccessDeniedException;
import org.imixs.workflow.mvc.controller.DocumentController;
import org.imixs.workflow.mvc.controller.WorkitemEvent;

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
	
	@POST
	@Path("{uniqueid}")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	public String saveTeam(@PathParam("uniqueid") String uid, InputStream requestBodyStream) {
		model.setTeam(super.saveDocument(uid, requestBodyStream));
		// update teams....
		model.setTeams(super.findDocumentsByType("team"));
		return "teams.xhtml";
	}



	/**
	 * WorkItemEvent listener to convert team item into a multi value list
	 * 
	 * @param workitemEvent
	 * @throws AccessDeniedException
	 */
	@SuppressWarnings("unchecked")
	public void onWorkflowEvent(@Observes WorkitemEvent workitemEvent) throws AccessDeniedException {
		if (workitemEvent == null)
			return;

		// skip if not a workItem...
		if (workitemEvent.getWorkitem() != null
				&& !workitemEvent.getWorkitem().getItemValueString("type").startsWith("team"))
			return;

		int eventType = workitemEvent.getEventType();

		// convert list to string with newlines
		if (WorkitemEvent.WORKITEM_CHANGED == eventType) {
			List<String> members = workitemEvent.getWorkitem().getItemValue("members");
			String result = "";
			for (String member : members) {
				result += member + "\n";
			}
			workitemEvent.getWorkitem().replaceItemValue("members", result);
		}

		// convert string with newlines to list
		if (WorkitemEvent.WORKITEM_BEFORE_SAVE == eventType) {
			String value = workitemEvent.getWorkitem().getItemValueString("members");
			workitemEvent.getWorkitem().replaceItemValue("members", Arrays.asList(value.split("\\r?\\n")));
		}

	}

}