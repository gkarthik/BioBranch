package org.scripps.branch.config;

import java.io.IOException;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.scripps.branch.globalentity.DatasetMap;
import org.scripps.branch.utilities.HibernateAwareObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.jolbox.bonecp.BoneCPDataSource;

@Configuration
@ComponentScan(basePackages = { "org.scripps.branch.entity",
		"org.scripps.branch.service", "org.scripps.branch.repository",
		"org.scripps.branch.utilities", "org.scripps.branch.globalentity",
		"org.scripps.branch.viz", "org.scripps.branch.controller" })
@Import({ WebApplicationContext.class, SecurityContext.class, SocialContext.class, FeatureJobConfig.class })
@EnableJpaRepositories(basePackages = { "org.scripps.branch.repository" })
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
public class ApplicationContextConfig {

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
	
	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}

	Properties additionalProperties() {
		Properties properties = new Properties();
		properties.setProperty("hibernate.hbm2ddl.auto",
				env.getProperty("hibernate.hbm2ddl.auto"));
		properties.setProperty("hibernate.dialect",
				env.getProperty("hibernate.dialect"));
		return properties;
	}
	
//	@Bean
//    public BoneCPDataSource boneCPDataSource() {
// 
//        BoneCPDataSource boneCPDataSource = new BoneCPDataSource();
//        boneCPDataSource.setDriverClass(env.getProperty("db.driver"));
//        boneCPDataSource.setJdbcUrl(env.getProperty("db.url"));
//        boneCPDataSource.setUsername(env.getProperty("db.username"));
//        boneCPDataSource.setPassword(env.getProperty("db.password"));
//        boneCPDataSource.setIdleConnectionTestPeriodInMinutes(60);
//        boneCPDataSource.setIdleMaxAgeInMinutes(420);
//        boneCPDataSource.setMaxConnectionsPerPartition(30);
//        boneCPDataSource.setMinConnectionsPerPartition(10);
//        boneCPDataSource.setPartitionCount(3);
//        boneCPDataSource.setAcquireIncrement(5);
//        boneCPDataSource.setStatementsCacheSize(100);
//        boneCPDataSource.setReleaseHelperThreads(3);
// 
//        return boneCPDataSource;
// 
//    }
	
//	private DatabasePopulator createDatabasePopulator(ApplicationContext ctx) {
//		ResourceDatabasePopulator databasePopulator = new ResourceDatabasePopulator();
//		databasePopulator.setContinueOnError(true);
//		databasePopulator.addScript(new ClassPathResource("/WEB-INF/data/schema-postgresql.sql"));
//		return databasePopulator;
//	}
	
	@Bean
	public DataSource dataSource(ApplicationContext ctx) {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(env.getProperty("db.driver"));
		dataSource.setUrl(env.getProperty("db.url"));
		dataSource.setUsername(env.getProperty("db.username"));
		dataSource.setPassword(env.getProperty("db.password"));
//		DatabasePopulatorUtils.execute(createDatabasePopulator(ctx), dataSource);
		return dataSource;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource ds) throws IOException {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setDataSource(ds);
		em.setPackagesToScan(new String[] { "org.scripps.branch.entity" });
		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		em.setJpaVendorAdapter(vendorAdapter);
		em.setJpaProperties(additionalProperties());
		return em;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	@Bean
	public PlatformTransactionManager transactionManager(
			EntityManagerFactory emf) {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(emf);
		return transactionManager;
	}
	
	@Bean(name = "globalWeka")
	public DatasetMap initWekaInApplicationContext() {
		return new DatasetMap();
	}

}
