package com.integralblue.hammertime.web;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Valid;

import org.springframework.social.NotAuthorizedException;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.web.FacebookCookieValue;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.integralblue.hammertime.NotLoggedInException;
import com.integralblue.hammertime.model.Project;
import com.integralblue.hammertime.model.User;

@Controller
public class ProjectController {
	@PersistenceContext
    private EntityManager entityManager;
	
	@Inject
	Facebook facebook;
	
	/** Gets the current user. If the user isn't in the database, it creates a database entry for them.
	 * @return
	 */
	private User getCurrentUser(){
		String userId = facebook.userOperations().getUserProfile().getId();
		User user = entityManager.find(User.class, userId);
		if(user==null){
			user = new User();
			user.setId(userId);
			entityManager.persist(user);
		}
		return user;
	}
	
	@RequestMapping(value="/project/create",method=RequestMethod.GET)
	public ModelAndView createProjectForm(Project project, @FacebookCookieValue(value="access_token") String accessToken){
		if(! facebook.isAuthorized()) throw new NotLoggedInException(); // makes sure the user is logged in
		return new ModelAndView("project/create");
	}
	
	@Transactional
	@RequestMapping(value="/project/create",method=RequestMethod.POST)
	public String createProject(@ModelAttribute @Valid Project project){
		if(! facebook.isAuthorized()) throw new NotLoggedInException();
		project.setOwner(getCurrentUser());
		entityManager.persist(project);
		entityManager.flush();
		return "redirect:/project/" + project.getName();
	}

	@Transactional
	@RequestMapping(value="/project/{name}",method=RequestMethod.PUT)
	public String createProject(@PathVariable String name, @ModelAttribute @Valid Project project){
		if(! facebook.isAuthorized()) throw new NotLoggedInException();
		project.setOwner(getCurrentUser());
		entityManager.persist(project);
		return "redirect:/project/" + project.getName();
	}

	@Transactional
	@RequestMapping(value="/project/{name}",method=RequestMethod.POST)
	public ModelAndView updateProject(@PathVariable String name, @ModelAttribute @Valid Project project){
		if(! facebook.isAuthorized()) throw new NotLoggedInException();
		entityManager.merge(project);
		return new ModelAndView("project/view", "project", project);
	}
	
	@RequestMapping(value="/project/{name}",method=RequestMethod.GET)
	public ModelAndView getProject(@PathVariable String name){
		final Project project = entityManager.createNamedQuery("Project.findByName", Project.class).setParameter("name", name).getSingleResult();
		return new ModelAndView("project/view", "project", project);
	}

	@Transactional
	@RequestMapping(value="/project/{name}",method=RequestMethod.DELETE)
	@ResponseStatus(org.springframework.http.HttpStatus.NO_CONTENT) 
	public void deleteProject(@PathVariable String name){
		if(! facebook.isAuthorized()) throw new NotLoggedInException();
		final Project project = entityManager.createNamedQuery("Project.findByName", Project.class).setParameter("name", name).getSingleResult();
		if(! project.getOwner().getId().equals(getCurrentUser().getId())){
			// TODO use a better exception
			throw new RuntimeException("Can only delete projects you own");
		}
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
