package org.imixs.application.example.mvc;

import java.util.logging.Logger;

import javax.mvc.annotation.Controller;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * WelcomeController opens application start page. 
 * 
 * @author rsoika
 *
 */
@Controller
@Path("/home")
public class WelcomeController {
	private static Logger logger = Logger.getLogger(WelcomeController.class.getName());

	@GET
	public String home() {
		logger.fine("navigate home..");
		return "index.xhtml";
	}

}