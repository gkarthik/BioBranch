package org.scripps.branch.config;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.scripps.branch.batch.jobs.FeatureProcessor;
import org.scripps.branch.entity.Feature;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@EnableBatchProcessing
public class FeatureJobConfig {
	
	@Autowired
	  private JobBuilderFactory jobBuilderFactory;

	  @Autowired
	  private StepBuilderFactory stepBuilderFactory;
	  
	  @Autowired
	  private EntityManagerFactory emf;

    // tag::readerwriterprocessor[]
    @Bean
    public ItemReader<Feature> reader(org.springframework.context.ApplicationContext ctx) {
        FlatFileItemReader<Feature> reader = new FlatFileItemReader<Feature>();
        Resource path = ctx.getResource("/WEB-INF/data/mapping_oslo/features.txt");
        reader.setResource(path);
        reader.setLineMapper(new DefaultLineMapper<Feature>() {{
            setLineTokenizer(new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB) {{
                setNames(new String[] { "", "uniqueid", "short_name", "", "", "", "", "", "description", "", "", "long_name", "", "", ""});
            }});
            setFieldSetMapper(new BeanWrapperFieldSetMapper<Feature>() {{
                setTargetType(Feature.class);
            }});
        }});
        return reader;
    }

    @Bean
    public ItemProcessor<Feature, Feature> processor() {
        return new FeatureProcessor();
    }

    @Bean
    public ItemWriter<Feature> writer(EntityManagerFactory emf) {
    	JpaItemWriter<Feature> writer = new JpaItemWriter<Feature>();
    	writer.setEntityManagerFactory(emf);
        return writer;
    }
    // end::readerwriterprocessor[]

    // tag::jobstep[]
    @Bean
    public Job importUserJob(JobBuilderFactory jobs, Step s1) {
        return jobs.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .flow(s1)
                .end()
                .build();
    }
    
    @Bean
    public Step step1(StepBuilderFactory stepBuilderFactory, ItemReader<Feature> reader,
            ItemWriter<Feature> writer, ItemProcessor<Feature, Feature> processor) {
        return stepBuilderFactory.get("step1")
        		.transactionManager(new JpaTransactionManager(emf))
                .<Feature, Feature> chunk(10)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .build();
    }
    // end::jobstep[]

}
