package com.integralblue.hammertime.web.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.smile.SmileFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;
import org.thymeleaf.spring3.SpringTemplateEngine;
import org.thymeleaf.spring3.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

@Configuration
@ComponentScan(basePackages = { "com.integralblue.hammertime.web" })
public class WebMvcConfig extends WebMvcConfigurationSupport {
	
	protected static final MediaType APPLICATION_X_JSON_SMILE = new MediaType("application","x-json-smile");

	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
		configurer.setDefaultTimeout(30*1000L);
	}

	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("welcome");
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**").addResourceLocations("resources/");
	}

	@Override
	protected Map<String, MediaType> getDefaultMediaTypes() {
		Map<String, MediaType> defaultMediaTypes = super.getDefaultMediaTypes();
		defaultMediaTypes.put("smile", APPLICATION_X_JSON_SMILE);
		return defaultMediaTypes;
	}

	@Bean
	public ViewResolver viewResolver() {
		ContentNegotiatingViewResolver contentNegotiatingViewResolver = new ContentNegotiatingViewResolver();
		ThymeleafViewResolver resolver = new ThymeleafViewResolver();
		resolver.setTemplateEngine(templateEngine());
		contentNegotiatingViewResolver.setViewResolvers(Arrays.asList(new ViewResolver[]{resolver}));
		contentNegotiatingViewResolver.setContentNegotiationManager(mvcContentNegotiationManager());
		final List<View> defaultViews = new ArrayList<View>(2);
		defaultViews.add(smileView());
		defaultViews.add(jsonView());
		contentNegotiatingViewResolver.setDefaultViews(defaultViews);
		return contentNegotiatingViewResolver;
	}
	
    @Bean(name = "jsonView")
    public MappingJacksonJsonView jsonView() {
        MappingJacksonJsonView mappingJacksonJsonView = new MappingJacksonJsonView();
        mappingJacksonJsonView.setExtractValueFromSingleKeyModel(true);
        mappingJacksonJsonView.setContentType(MediaType.APPLICATION_JSON_VALUE);
        return mappingJacksonJsonView;
    }
	
    @Bean(name = "smileView")
    public MappingJacksonJsonView smileView() {
        MappingJacksonJsonView mappingJacksonJsonView = new MappingJacksonJsonView();
        mappingJacksonJsonView.setExtractValueFromSingleKeyModel(true);
        mappingJacksonJsonView.setContentType(APPLICATION_X_JSON_SMILE.toString());
        mappingJacksonJsonView.setObjectMapper(smileObjectMapper());
        return mappingJacksonJsonView;
    }

	@Bean
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine engine = new SpringTemplateEngine();
		engine.setTemplateResolver(templateResolver());
		return engine;
	}

	@Bean
	public TemplateResolver templateResolver() {
		ServletContextTemplateResolver resolver = new ServletContextTemplateResolver();
		resolver.setPrefix("/WEB-INF/templates/");
		resolver.setSuffix(".html");
		resolver.setTemplateMode("HTML5");
		resolver.setCacheable(false);
		return resolver;
	}

	@Override
	public void configureMessageConverters(
			List<HttpMessageConverter<?>> converters) {
		addDefaultHttpMessageConverters(converters);
		converters.add(getSmileMappingJacksonHttpMessageConverter());
	}

	@Bean(name = "smileObjectMapper")
	protected ObjectMapper smileObjectMapper(){
		return new ObjectMapper(new SmileFactory());
	}
	
	protected MappingJacksonHttpMessageConverter getSmileMappingJacksonHttpMessageConverter(){
		final MappingJacksonHttpMessageConverter smileMappingJacksonHttpMessageConverter = new MappingJacksonHttpMessageConverter();
		smileMappingJacksonHttpMessageConverter.setObjectMapper(smileObjectMapper());
		smileMappingJacksonHttpMessageConverter.setSupportedMediaTypes(Arrays.asList(new MediaType[]{APPLICATION_X_JSON_SMILE}));
		return smileMappingJacksonHttpMessageConverter;
	}

	@Override
	public void configureContentNegotiation(
			ContentNegotiationConfigurer configurer) {
		configurer.addMediaType("smile", APPLICATION_X_JSON_SMILE);
	}
	
}
