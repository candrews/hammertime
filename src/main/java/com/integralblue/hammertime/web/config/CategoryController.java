package com.integralblue.hammertime.web.config;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.integralblue.hammertime.model.Category;
import com.integralblue.hammertime.model.Project;

@Controller
public class CategoryController {
	@PersistenceContext
    private EntityManager entityManager;
	
	@RequestMapping(value="/category/{name}",method=RequestMethod.GET)
	public ModelAndView getCategory(@PathVariable String name){
		final Category category = entityManager.createNamedQuery("Category.findByName", Category.class).setParameter("name", name).getSingleResult();
		return new ModelAndView("category/view", "category", category);
	}
	
	@RequestMapping(value="/category/{name}/projects",method=RequestMethod.GET)
	public ModelAndView getProjectsInCategory(@PathVariable String name){
		final Category category = entityManager.createNamedQuery("Category.findByName", Category.class).setParameter("name", name).getSingleResult();
		final List<Project> projects = entityManager.createNamedQuery("Project.findByCategory", Project.class).setParameter("category", category).getResultList();
		return new ModelAndView("category/viewProjects", "projects", projects);
	}
	
	@RequestMapping(value="/category/list", method=RequestMethod.GET)
	public List<Category> list(){
		return entityManager.createNamedQuery("Category.listAll", Category.class).getResultList();
	}
}
