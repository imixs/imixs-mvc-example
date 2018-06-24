package org.imixs.application.example.mvc;

import org.imixs.workflow.ItemCollection;

public class ModelEvent {

	public static final int WORKITEM_CREATED = 1;

	public static final int WORKITEM_CHANGED = 3;

	public static final int WORKITEM_BEFORE_SAVE = 14;
	public static final int WORKITEM_AFTER_SAVE = 15;
	
	
	private int eventType;
	private ItemCollection workitem;

	public ModelEvent(ItemCollection workitem, int eventType) {
		this.eventType = eventType;
		this.workitem = workitem;
	}

	public int getEventType() {
		return eventType;
	}

	public ItemCollection getWorkitem() {
		return workitem;
	}

}
