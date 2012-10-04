package com.integralblue.hammertime.web;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.integralblue.hammertime.model.Project;
import com.integralblue.hammertime.model.User;

@Controller
public class UserController {
	@PersistenceContext
    private EntityManager entityManager;
	
	@RequestMapping(value="/user/{id}",method=RequestMethod.GET)
	public ModelAndView getUser(@PathVariable String id){
		final User user = entityManager.find(User.class,id);
		return new ModelAndView("user/view", "user", user);
	}
	
	@RequestMapping(value="/user/{id}/owns",method=RequestMethod.GET)
	public ModelAndView getProjectsUserOwns(@PathVariable String id){
		final User user = entityManager.find(User.class,id);
		final List<Project> projects = entityManager.createNamedQuery("Projects.findByOwner", Project.class).setParameter("owner", user).getResultList();
		return new ModelAndView("user/owns", "projects", projects);
	}
	
	@RequestMapping(value="/user/{id}/participating",method=RequestMethod.GET)
	public ModelAndView getProjectsUserParticipating(@PathVariable String id){
		final User user = entityManager.find(User.class,id);
		final List<Project> projects = entityManager.createNamedQuery("Projects.findByParticipant", Project.class).setParameter("participant", user).getResultList();
		return new ModelAndView("user/participating", "projects", projects);
	}
	
	
}
