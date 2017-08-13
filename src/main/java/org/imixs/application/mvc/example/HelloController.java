package org.imixs.application.mvc.example;

import javax.inject.Inject;
import javax.mvc.Models;
import javax.mvc.annotation.Controller;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Controller
@Path("/hello")

public class HelloController {

	
	
	@Inject
	private Models models;
	
	
	@GET
	public String render() {
		models.put("message", "Hello world!");
		return "/WEB-INF/views/hello.jsp";
	}
}
