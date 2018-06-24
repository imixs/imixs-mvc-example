package org.imixs.application.example.mvc;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.ItemCollectionComparator;
import org.imixs.workflow.engine.WorkflowService;
import org.imixs.workflow.exceptions.AccessDeniedException;
import org.imixs.workflow.exceptions.ModelException;

/**
 * CDI-Model holds team and workitem value objects.
 * 
 * @author rsoika
 *
 */
@RequestScoped
@Named
public class Model implements Serializable {

	private static final long serialVersionUID = 1L;

	private static Logger logger = Logger.getLogger(Model.class.getName());

	ItemCollection team;
	List<org.imixs.workflow.ItemCollection> teams;

	ItemCollection workitem;
	List<org.imixs.workflow.ItemCollection> tasklist;

	
	@Inject
	protected Event<ModelEvent> events;

	
	@EJB
	WorkflowService workflowService;

	/**
	 * Initialize model
	 */
	public Model() {
		super();

	}

	public ItemCollection getTeam() {
		
		return team;
	}

	public void setTeam(ItemCollection team) {
		this.team = team;
		events.fire(new ModelEvent(this.team, ModelEvent.WORKITEM_CHANGED));
	}

	public List<org.imixs.workflow.ItemCollection> getTeams() {
		if (teams == null) {
			teams = workflowService.getDocumentService().getDocumentsByType("team");
		}
		return teams;
	}

	public void setTeams(List<org.imixs.workflow.ItemCollection> teams) {
		// sort team list by name
		if (teams != null) {
			logger.finest("......sort teamlist by name");
			Collections.sort(teams, new ItemCollectionComparator("txtname", true));
		}

		this.teams = teams;
	}

	public List<ItemCollection> getTasklist() {
		return tasklist;
	}

	public void setTasklist(List<ItemCollection> taskList) {
		this.tasklist = taskList;
	}

	public ItemCollection getWorkitem() {
		return workitem;
	}

	public void setWorkitem(ItemCollection workitem) {
		this.workitem = workitem;
	}

	/**
	 * This method returns the events for the current workitem instance.
	 * 
	 * @return
	 */
	public List<ItemCollection> getEvents() {
		List<ItemCollection> events = new ArrayList<ItemCollection>();
		try {
			if (workitem != null) {
				events = workflowService.getEvents(workitem);
			}
		} catch (ModelException e) {
			logger.severe("unable to get workfow event: " + e.getMessage());
		}
		return events;
	}

	/**
	 * WorkItemEvent listener to convert team item into a multi value list
	 * 
	 * @param workitemEvent
	 * @throws AccessDeniedException
	 */
	@SuppressWarnings("unchecked")
	public void onWorkflowEvent(@Observes ModelEvent workitemEvent) throws AccessDeniedException {
		if (workitemEvent == null || workitemEvent.getWorkitem() == null) {
			return;
		}

		int eventType = workitemEvent.getEventType();

		// convert team member list...
		if (workitemEvent.getWorkitem().getItemValueString("type").startsWith("team")) {

			if (ModelEvent.WORKITEM_CHANGED == eventType) {
				// convert list to string with newlines
				List<String> members = workitemEvent.getWorkitem().getItemValue("members");
				String result = "";
				for (String member : members) {
					result += member + "\n";
				}
				workitemEvent.getWorkitem().replaceItemValue("members", result);
			}

			if (ModelEvent.WORKITEM_BEFORE_SAVE == eventType) {
				// convert input string with newlines into a list
				String value = workitemEvent.getWorkitem().getItemValueString("members");
				workitemEvent.getWorkitem().replaceItemValue("members", Arrays.asList(value.split("\\r?\\n")));
			}
		}

	}

}