package com.integralblue.hammertime.web.config;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.smile.SmileFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.facebook.connect.FacebookOAuth2Template;
import org.springframework.social.facebook.web.FacebookCookieParser;
import org.springframework.social.facebook.web.FacebookOAuth2CookieParser;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.OAuth2Template;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
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

import com.integralblue.hammertime.NotLoggedInException;

@Configuration
@ComponentScan(basePackages = { "com.integralblue.hammertime.web" })
@PropertySource("classpath:/social.properties")
public class WebMvcConfig extends WebMvcConfigurationSupport {
	
	protected static final MediaType APPLICATION_X_JSON_SMILE = new MediaType("application","x-json-smile");
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	@Override
	protected void configureHandlerExceptionResolvers(
			List<HandlerExceptionResolver> exceptionResolvers) {
		exceptionResolvers.add(new HandlerExceptionResolver() {
			
			@Override
			public ModelAndView resolveException(HttpServletRequest request,
					HttpServletResponse response, Object handler, Exception ex) {
				if(ex instanceof NoResultException){
					try {
						response.sendError(HttpServletResponse.SC_NOT_FOUND);
						return new ModelAndView();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
				if(ex instanceof NotLoggedInException){
					try {
						response.sendError(HttpServletResponse.SC_FORBIDDEN);
						return new ModelAndView();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
				return null;
			}
		});
		addDefaultHandlerExceptionResolvers(exceptionResolvers);
	}
	
	@Bean
	@Scope(proxyMode=ScopedProxyMode.INTERFACES,value="request")
	public Facebook facebook(@Value("${social.facebook.appid}") String appId, @Value("${social.facebook.appsecret}") String appSecret){
		String accessToken = getAccessTokenFromCurrentRequest(appId, appSecret);
		if(accessToken == null){
			return new FacebookTemplate();
		}else{
			return new FacebookTemplate(accessToken);
		}
	}
	
	@Bean
	public FacebookOAuth2Template facebookOAuth2Template(@Value("${social.facebook.appid}") String appId, @Value("${social.facebook.appsecret}") String appSecret){
		return new FacebookOAuth2Template(appId, appSecret);
	}
	
	private String getAccessTokenFromCurrentRequest(String appId, String appSecret){
		ServletRequestAttributes sra = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
	    HttpServletRequest req = sra.getRequest();

	    String accessToken = null;
        Map<String,String> fbCookie = FacebookOAuth2CookieParser.parseFacebookCookie(req.getCookies(), appId, appSecret);
        if(fbCookie!=null && fbCookie.containsKey("code")){
            String authorizationCode = fbCookie.get("code");
            AccessGrant accessGrant;
			try {
				accessGrant = facebookOAuth2Template(appId, appSecret).exchangeForAccess(URLEncoder.encode(authorizationCode,"UTF-8"), "", null);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
            if(accessGrant!=null){
                accessToken = accessGrant.getAccessToken(); 
            }
        }
	    
		if (accessToken == null) {
			// cookie value isn't there
			// API users will use an API header - check that
			accessToken = req.getHeader("X-Facebook-Access-Token");
		}
		return accessToken;
	}

	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
		configurer.setDefaultTimeout(30*1000L);
	}

	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/").setViewName("welcome");
		registry.addViewController("/signin");
		registry.addViewController("/signout");
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
