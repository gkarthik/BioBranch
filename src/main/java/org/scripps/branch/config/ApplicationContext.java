package org.scripps.branch.config;

import org.scripps.branch.globalentity.WekaObject;
import org.scripps.branch.utilities.HibernateAwareObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = { "org.scripps.branch.entity",
		"org.scripps.branch.service", "org.scripps.branch.repository",
		"org.scripps.branch.utilities", "org.scripps.branch.globalentity",
		"org.scripps.branch.viz" })
@Import({ WebApplicationContext.class, PersistenceJPAConfig.class,
		SecurityContext.class, SocialContext.class, FeatureJobConfig.class })
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
public class ApplicationContext {

	private static final String MESSAGE_SOURCE_BASE_NAME = "i18n/messages";
	
	@Autowired
	Environment env;
	
	@Bean String setUploadsDir(){
		return env.getProperty("uploads.dir");
	}
	
	@Bean
	public HibernateAwareObjectMapper initHibernateAwareObjectMapper() {
		return new HibernateAwareObjectMapper();
	}

	@Bean(name = "globalWeka")
	public WekaObject initWekaInApplicationContext() {
		return new WekaObject();
	}

	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

		messageSource.setBasename(MESSAGE_SOURCE_BASE_NAME);
		messageSource.setUseCodeAsDefaultMessage(true);

		return messageSource;
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertyPlaceHolderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

}
