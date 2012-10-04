package com.integralblue.hammertime.web;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
	@PersistenceContext
    private EntityManager entityManager;
	
	@RequestMapping(value="/project/create",method=RequestMethod.GET)
	public ModelAndView createProjectForm(Project project){
		return new ModelAndView("project/create");
	}
	
	@RequestMapping(value="/project/create",method=RequestMethod.POST)
	public String createProject(@ModelAttribute @Valid Project project){
		entityManager.persist(project);
		return "redirect:/projects/{name}";
	}
	
	@RequestMapping(value="/project/{name}",method=RequestMethod.PUT)
	public String createProject(@PathVariable String name, @ModelAttribute @Valid Project project){
		entityManager.persist(project);
		return "redirect:/projects/{name}";
	}

	@RequestMapping(value="/project/{name}",method=RequestMethod.POST)
	public ModelAndView updateProject(@PathVariable String name, @ModelAttribute @Valid Project project){
		entityManager.merge(project);
		return new ModelAndView("project/view", "project", project);
	}
	
	@RequestMapping(value="/project/{name}",method=RequestMethod.GET)
	public ModelAndView getProject(@PathVariable String name){
		final Project project = entityManager.createNamedQuery("Project.findByName", Project.class).setParameter("name", name).getSingleResult();
		return new ModelAndView("project/view", "project", project);
	}
	
	@RequestMapping(value="/project/{name}",method=RequestMethod.DELETE)
	@ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT) 
	public void deleteProject(@PathVariable String name){
		final Project project = entityManager.createNamedQuery("Project.findByName", Project.class).setParameter("name", name).getSingleResult();
		entityManager.remove(project);
	}
	
	@RequestMapping(value="/project/{name}",method=RequestMethod.DELETE, consumes="text/html", produces="text/html")
	public String deleteProjectHtml(@PathVariable String name){
		deleteProject(name);
		return "/";
	}
	
	@RequestMapping(value="/project/list", method=RequestMethod.GET)
	public List<Project> list(){
		return entityManager.createNamedQuery("Project.listAll", Project.class).getResultList();
	}
}
