package org.imixs.application.example.mvc;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.ItemCollectionComparator;

/**
 * Controller to manage active imixs-workflow instances.
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
	}

	public List<org.imixs.workflow.ItemCollection> getTeams() {
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

	public ItemCollection getWorkitem() {
		return workitem;
	}

	public void setWorkitem(ItemCollection workitem) {
		this.workitem = workitem;
	}

}