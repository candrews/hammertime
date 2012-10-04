package com.integralblue.hammertime.web;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.integralblue.hammertime.model.Project;

@Controller
public class ProjectController {
	@RequestMapping(value="/project/{name}",method=RequestMethod.GET, consumes="text/html", produces="text/html")
	public ModelAndView createProject(){
		return new ModelAndView("createProject");
	}
	
	@RequestMapping(value="/project/{name}",method=RequestMethod.PUT)
	public String createProject(@PathVariable String name, @ModelAttribute @Valid Project project){
		return "redirect:/projects/{name}";
	}

	@RequestMapping(value="/project/{name}",method=RequestMethod.POST)
	public ModelAndView updateProject(@PathVariable String name, @ModelAttribute @Valid Project project){
		return new ModelAndView("project", "project", project);
	}
	
	@RequestMapping(value="/project/{name}",method=RequestMethod.GET)
	public ModelAndView getProject(@PathVariable String name){
		final Project project = new Project();
		project.setName(name);
		return new ModelAndView("project", "project", project);
	}
	
	@RequestMapping(value="/project/{name}",method=RequestMethod.DELETE)
	@ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT) 
	public Project deleteProject(@PathVariable String name){
		return null;
	}
	
	@RequestMapping(value="/project/{name}",method=RequestMethod.DELETE, consumes="text/html", produces="text/html")
	public String deleteProjectHtml(@PathVariable String name){
		return "/";
	}
}
