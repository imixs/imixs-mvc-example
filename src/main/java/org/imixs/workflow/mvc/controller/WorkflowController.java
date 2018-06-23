package org.imixs.workflow.mvc.controller;

import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.PathParam;

import org.imixs.workflow.ItemCollection;
import org.imixs.workflow.Model;
import org.imixs.workflow.WorkflowKernel;
import org.imixs.workflow.engine.ModelService;
import org.imixs.workflow.engine.WorkflowService;
import org.imixs.workflow.exceptions.AccessDeniedException;
import org.imixs.workflow.exceptions.ModelException;
import org.imixs.workflow.exceptions.PluginException;
import org.imixs.workflow.exceptions.ProcessingErrorException;
import org.imixs.workflow.jaxrs.WorkflowRestService;

/**
 * The DocumentController provide a generic controller class to handle document
 * entities managed by the Imixs-Workflow DocumentService.
 * 
 * The DocumentController provides a set of properties to describe a specific
 * document entity.
 * 
 * @author rsoika
 *
 */
public abstract class WorkflowController {

	private static Logger logger = Logger.getLogger(WorkflowController.class.getName());

	@Inject
	protected Event<WorkitemEvent> events;

	@EJB
	protected ModelService modelService;

	@EJB
	protected WorkflowService workflowService;

	/**
	 * Finds an instance of ItemCollection by $uniqueID. If not found, the method
	 * returns null.
	 * 
	 * @param uid
	 * @return instance of ItemCollection or null if not found.
	 */
	public ItemCollection findWorkitemByUnqiueID(@PathParam("uniqueid") String uid) {
		logger.info("......load workitem: " + uid);
		ItemCollection workitem = workflowService.getWorkItem(uid);
		events.fire(new WorkitemEvent(workitem, WorkitemEvent.WORKITEM_CHANGED));
		return workitem;
	}

	/**
	 * Creates a new process instance based on the given model version
	 * 
	 * @return
	 * @throws ModelException
	 */
	public ItemCollection createWorkitem(@PathParam("modelversion") String modelversion, @PathParam("task") String task)
			throws ModelException {

		ItemCollection workitem = null;
		int iTask = Integer.parseInt(task);
		Model model;

		model = modelService.getModel(modelversion);
		// find task element
		ItemCollection taskElement = model.getTask(iTask);

		String uid = WorkflowKernel.generateUniqueID();
		logger.info("......create new workitem: " + uid);
		workitem = new ItemCollection().model(modelversion).task(iTask);
		workitem.replaceItemValue(WorkflowKernel.UNIQUEID, uid);
		workitem.replaceItemValue("type", "workitem");
		workitem.replaceItemValue(WorkflowKernel.WORKFLOWGROUP, taskElement.getItemValueString("txtworkflowgroup"));

		events.fire(new WorkitemEvent(workitem, WorkitemEvent.WORKITEM_CREATED));

		return workitem;
	}

	/**
	 * Process an instance of an Imixs ItemCollection. The method accepts a
	 * $uniqueID to identify an already existing stored instance and a InputStream
	 * to be parsed for form values provided by a web page.
	 * 
	 * @param uid
	 * @param requestBodyStream
	 * @return updated instance of ItemCollection
	 *
	 * @param uid
	 * @param requestBodyStream
	 * @return
	 * @throws ModelException
	 * @throws PluginException
	 * @throws ProcessingErrorException
	 * @throws AccessDeniedException
	 */
	public ItemCollection processWorkitem(@PathParam("uniqueid") String uid, InputStream requestBodyStream)
			throws AccessDeniedException, ProcessingErrorException, PluginException, ModelException {
		ItemCollection workitem = null;

		logger.info("......postFormWorkitem @POST /workitem  method:postWorkitem....");

		// parse the workItem.
		workitem = WorkflowRestService.parseWorkitem(requestBodyStream);
		workitem.replaceItemValue(WorkflowKernel.UNIQUEID, uid);

		// try to load current instance of this document entity
		ItemCollection currentInstance = workflowService.getWorkItem(uid);
		if (currentInstance != null) {
			// merge entity into current instance
			// an instance of this Entity still exists! so we update the
			// new values here....
			currentInstance.replaceAllItems(workitem.getAllItems());
			workitem = currentInstance;
		}

		// save workItem ...
		logger.info("......process uniqueid=" + uid);
		logger.info("......modelversion=" + workitem.getModelVersion());
		logger.info("......workflowgroup=" + workitem.getItemValueString(WorkflowKernel.WORKFLOWGROUP));
		logger.info("......task=" + workitem.getTaskID());
		logger.info("......event=" + workitem.getEventID());

		events.fire(new WorkitemEvent(workitem, WorkitemEvent.WORKITEM_BEFORE_PROCESS));
		workitem = workflowService.processWorkItem(workitem);
		events.fire(new WorkitemEvent(workitem, WorkitemEvent.WORKITEM_AFTER_PROCESS));

		logger.finest("......ItemCollection saved");
		return workitem;
	}

	public List<ItemCollection> getStatusList() {
		logger.info("......load documents.");
		List<ItemCollection> result = workflowService.getWorkListByCreator(null, "workitem", 30, 0, "$modified", true);
		return result;

	}

	public List<ItemCollection> getTaskList() {
		logger.info("......load documents.");
		List<ItemCollection> result = workflowService.getWorkListByAuthor(null, "workitem", 30, 0, "$modified", true);
		return result;

	}

	public List<ItemCollection> getArchive() {
		logger.info("......load documents.");
		List<ItemCollection> result = workflowService.getDocumentService().getDocumentsByType("workitemarchive");
		return result;

	}

}