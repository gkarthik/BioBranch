package org.scripps.branch.config;

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.sitemesh.config.ConfigurableSiteMeshFilter;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.DispatcherServlet;

public class ApplicationConfig implements WebApplicationInitializer {
	private static final String DISPATCHER_SERVLET_MAPPING = "/";
	private static final String DISPATCHER_SERVLET_NAME = "dispatcher";

	@Override
	public void onStartup(ServletContext servletContext)
			throws ServletException {
		// If you want to use the XML configuration, comment the following two
		// lines out.
		AnnotationConfigWebApplicationContext rootContext = new AnnotationConfigWebApplicationContext();
		rootContext.register(ApplicationContext.class);

		servletContext.addFilter("OpenEntityManagerInViewFilter",
				OpenEntityManagerInViewFilter.class).addMappingForUrlPatterns(
				null, false, "/*");

		// If you want to use the XML configuration, uncomment the following
		// lines.
		// XmlWebApplicationContext rootContext = new
		// XmlWebApplicationContext();
		// rootContext.setConfigLocation("classpath:exampleApplicationContext.xml");

		ServletRegistration.Dynamic dispatcher = servletContext.addServlet(
				DISPATCHER_SERVLET_NAME, new DispatcherServlet(rootContext));
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping(DISPATCHER_SERVLET_MAPPING);

		EnumSet<DispatcherType> dispatcherTypes = EnumSet.of(
				DispatcherType.REQUEST, DispatcherType.FORWARD);

		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		characterEncodingFilter.setForceEncoding(true);

		FilterRegistration.Dynamic characterEncoding = servletContext
				.addFilter("characterEncoding", characterEncodingFilter);
		characterEncoding.addMappingForUrlPatterns(dispatcherTypes, true, "/*");

		FilterRegistration.Dynamic security = servletContext.addFilter(
				"springSecurityFilterChain", new DelegatingFilterProxy());
		security.addMappingForUrlPatterns(dispatcherTypes, true, "/*");

		FilterRegistration.Dynamic sitemesh = servletContext.addFilter(
				"sitemesh", new ConfigurableSiteMeshFilter());
		sitemesh.addMappingForUrlPatterns(dispatcherTypes, true, "*.jsp");

		servletContext.addListener(new ContextLoaderListener(rootContext));

		// public MultipartConfigElement(java.lang.String location,long
		// maxFileSize,long maxRequestSize, int fileSizeThreshold)
		dispatcher.setMultipartConfig(new MultipartConfigElement("/tmp",
				1073741824, 1073741824, 1024 * 1024));

	}
}