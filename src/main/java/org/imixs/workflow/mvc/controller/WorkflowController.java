package org.imixs.workflow.mvc.controller;

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

	private ItemCollection workitem = new ItemCollection();

	private static Logger logger = Logger.getLogger(WorkflowController.class.getName());

	@EJB
	ModelService modelService;

	@EJB
	WorkflowService workflowService;

	@GET
	@Path("{uniqueid}")
	public String getWorkitemByUnqiueID(@PathParam("uniqueid") String uid) {
		logger.info("......load workitem: " + uid);
		workitem = workflowService.getWorkItem(uid);
		return workitem.getItemValueString("txtWorkflowEditorID");
	}

	/**
	 * Creates a new process instance based on the given model version
	 * 
	 * @return
	 */
	@POST
	@Path("{modelversion}/{task}")
	public String createWorkitem(@PathParam("modelversion") String modelversion, @PathParam("task") String task) {
		String resultForm = "";
		int iTask = Integer.parseInt(task);
		Model model;
		try {
			model = modelService.getModel(modelversion);

			// find task
			ItemCollection taskElement = model.getTask(iTask);

			// get Form2
			resultForm = taskElement.getItemValueString("txteditorid");

			String uid = WorkflowKernel.generateUniqueID();
			logger.info("......create new workitem: " + uid);
			workitem = new ItemCollection();
			workitem.replaceItemValue(WorkflowKernel.UNIQUEID, uid);
			workitem.replaceItemValue("type", "workitem");
			workitem.replaceItemValue(WorkflowKernel.MODELVERSION, modelversion);
			workitem.replaceItemValue(WorkflowKernel.PROCESSID, iTask);

			workitem.replaceItemValue(WorkflowKernel.WORKFLOWGROUP, taskElement.getItemValueString("txtworkflowgroup"));

		} catch (ModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.info("createWorkitem outcome=" + resultForm);
		return resultForm;
	}

	@POST
	@Path("{uniqueid}")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	public String processWorkitem(@PathParam("uniqueid") String uid, InputStream requestBodyStream) {
		try {
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
			logger.info("......task=" + workitem.getProcessID());
			logger.info("......event=" + workitem.getActivityID());

			workitem = workflowService.processWorkItem(workitem);
		} catch (AccessDeniedException | ProcessingErrorException | PluginException | ModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.finest("......ItemCollection saved");
		return workitem.getItemValueString("txtWorkflowEditorID");
	}

	public ItemCollection getWorkitem() {
		return workitem;
	}

	public void setWorkitem(ItemCollection workitem) {
		this.workitem = workitem;
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
}