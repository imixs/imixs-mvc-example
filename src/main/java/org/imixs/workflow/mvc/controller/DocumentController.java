package org.imixs.workflow.mvc.controller;

import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.enterprise.event.Event;
import javax.inject.Inject;
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

/**
 * The DocumentController provide a generic controller class to handle document
 * entities managed by the Imixs-Workflow DocumentService.
 * 
 * A MVC 1.0 Controller can be extended from this class. 
 * 
 * @author rsoika
 *
 */
public abstract class DocumentController {

	private static Logger logger = Logger.getLogger(DocumentController.class.getName());

	@EJB
	protected DocumentService documentService;

	@Inject
	protected Event<WorkitemEvent> events;

	/**
	 * Creates a new instance of an Imixs ItemCollection with a $uniqueID and the
	 * specified type property. The method also fires a CDI event to allow other CDI
	 * beans to intercept the creation method.
	 * 
	 * @param type
	 * @return instance of ItemCollection
	 */
	@POST
	public ItemCollection createDocument(String type) {
		String uid = WorkflowKernel.generateUniqueID();
		logger.info("......create new document: " + uid);
		ItemCollection workitem = new ItemCollection();
		workitem.replaceItemValue(WorkflowKernel.UNIQUEID, uid);
		workitem.replaceItemValue("type", type);
		events.fire(new WorkitemEvent(workitem, WorkitemEvent.WORKITEM_CREATED));
		return workitem;
	}

	/**
	 * Saves an instance of an Imixs ItemCollection. The method accepts a $uniqueID
	 * to identify an already existing stored instance and a InputStream to be
	 * parsed for form values provided by a web page.
	 * 
	 * @param uid
	 * @param requestBodyStream
	 * @return updated instance of ItemCollection
	 */
	@POST
	@Path("{uniqueid}")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	public ItemCollection saveDocument(@PathParam("uniqueid") String uid, InputStream requestBodyStream) {

		logger.finest("......postFormWorkitem @POST /workitem  method:postWorkitem....");
		// parse the workItem.
		ItemCollection document = WorkflowRestService.parseWorkitem(requestBodyStream);
		document.replaceItemValue(WorkflowKernel.UNIQUEID, uid);

		// try to load current instance of this document entity
		ItemCollection currentInstance = documentService.load(uid);
		if (currentInstance != null) {
			// merge entity into current instance
			// an instance of this Entity still exists! so we update the
			// new values here....
			currentInstance.replaceAllItems(document.getAllItems());
			document = currentInstance;
		}

		// save workItem ...
		logger.info("......save document uniqueid=" + uid);
		events.fire(new WorkitemEvent(document, WorkitemEvent.WORKITEM_BEFORE_SAVE));
		document = documentService.save(document);
		events.fire(new WorkitemEvent(document, WorkitemEvent.WORKITEM_AFTER_PROCESS));
		logger.finest("......ItemCollection saved");
		return document;
	}

	/**
	 * Finds an instance of ItemCollection by $uniqueID. If not found, the method
	 * returns null.
	 * 
	 * @param uid
	 * @return instance of ItemCollection or null if not found.
	 */
	@GET
	@Path("{uniqueid}")
	public ItemCollection findDocumentByUnqiueID(@PathParam("uniqueid") String uid) {
		logger.info("......load document: " + uid);
		ItemCollection workitem = documentService.load(uid);
		return workitem;
	}

	/**
	 * Finds a collection of ItemCollection instances by type.
	 * 
	 * @param type
	 * @return collection of ItemCollection
	 */
	public List<ItemCollection> findDocumentsByType(String type) {
		logger.info("......load documents.");
		List<ItemCollection> result = documentService.getDocumentsByType(type);
		return result;

	}
}