package org.imixs.workflow.mvc.controller;

import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

import javax.ejb.EJB;
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
 * The DocumentController provide a generic controller class to handle document entities 
 * managed by the Imixs-Workflow DocumentService.
 * 
 * The DocumentController provides a set of properties to describe a specific document entity.
 * 
 * @author rsoika
 *
 */
public abstract class DocumentController {

	private ItemCollection workitem = new ItemCollection();
	private String documentsView;
	private String documentView;
	private String documentType;


	private static Logger logger = Logger.getLogger(DocumentController.class.getName());
	
	@EJB
	DocumentService documentService;
	
	public String getDocumentType() {
		return documentType;
	}


	public void setDocumentType(String documentType) {
		this.documentType = documentType;
	}




	public String getDocumentsView() {
		return documentsView;
	}


	public void setDocumentsView(String documentsView) {
		this.documentsView = documentsView;
	}


	public String getDocumentView() {
		return documentView;
	}


	public void setDocumentView(String documentView) {
		this.documentView = documentView;
	}


	/**
	 * load list of documents
	 * @return
	 */
	@GET
	public String showDocuments() {
		return getDocumentsView();
	}

	
	@GET
	@Path("{uniqueid}")
	public String getDocumentByUnqiueID(@PathParam("uniqueid") String uid) {
		logger.info("......load document: " + uid);
		workitem =documentService.load(uid);
		return getDocumentView();
	}

	
	@POST
	public String createDocument() {
		String uid = WorkflowKernel.generateUniqueID();
		logger.info("......create new document: " + uid);
		workitem = new ItemCollection();
		workitem.replaceItemValue(WorkflowKernel.UNIQUEID, uid);
		workitem.replaceItemValue("type", getDocumentType());
		return getDocumentView();
	}

	@POST
	@Path("{uniqueid}")
	@Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
	public String saveDocument(@PathParam("uniqueid") String uid, InputStream requestBodyStream) {

		logger.finest("......postFormWorkitem @POST /workitem  method:postWorkitem....");
		// parse the workItem.
		workitem = WorkflowRestService.parseWorkitem(requestBodyStream);
		workitem.replaceItemValue(WorkflowKernel.UNIQUEID, uid);
		
		// try to load current instance of this document entity
		ItemCollection currentInstance = documentService.load(uid);
		if (currentInstance != null) {
			// merge entity into current instance
			// an instance of this Entity still exists! so we update the
			// new values here....
			currentInstance.replaceAllItems(workitem.getAllItems());
			workitem = currentInstance;
		} 
		
		// update doucment type
		if (workitem.getType().isEmpty()) {
			workitem.replaceItemValue("type", getDocumentType());
		}
		
		// save workItem ...
		logger.info("......save document uniqueid=" + uid);
		workitem = documentService.save(workitem);
		logger.finest("......ItemCollection saved");
		return getDocumentsView();
	}

	public ItemCollection getWorkitem() {
		return workitem;
	}

	public void setWorkitem(ItemCollection workitem) {
		this.workitem = workitem;
	}

	public List<ItemCollection> getDocuments() {
		logger.info("......load documents.");
		List<ItemCollection> result = documentService.getDocumentsByType(getDocumentType());
		return result;

	}
}