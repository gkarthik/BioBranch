package org.scripps.branch.config;

import javax.persistence.EntityManagerFactory;

import org.scripps.branch.batch.jobs.FeatureProcessor;
import org.scripps.branch.entity.Feature;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.orm.jpa.JpaTransactionManager;

@Configuration
@EnableBatchProcessing
public class FeatureJobConfig {

	@Autowired
	private EntityManagerFactory emf;

	@Autowired
	private JobBuilderFactory jobBuilderFactory;

	@Autowired
	private StepBuilderFactory stepBuilderFactory;

	// tag::jobstep[]
	@Bean
	public Job importUserJob(JobBuilderFactory jobs, Step s1) {
		return jobs.get("importUserJob").incrementer(new RunIdIncrementer())
				.flow(s1).end().build();
	}

	@Bean
	public ItemProcessor<Feature, Feature> processor() {
		return new FeatureProcessor();
	}

	@Bean
	@StepScope
	public FlatFileItemReader<Feature> reader(
			@Value("#{jobParameters[inputPath]}") String pathToFile,
			org.springframework.context.ApplicationContext ctx) {
		FlatFileItemReader<Feature> reader = new FlatFileItemReader<Feature>();

		Resource path = ctx.getResource("file:" + pathToFile);
		reader.setResource(path);
		reader.setLineMapper(new DefaultLineMapper<Feature>() {
			{
				setLineTokenizer(new DelimitedLineTokenizer(
						DelimitedLineTokenizer.DELIMITER_TAB) {
					{
						setNames(new String[] { "", "uniqueid", "short_name", "", "", "", "", "", "description", "", "", "long_name", "", "", "" });
					}
				});
				setFieldSetMapper(new BeanWrapperFieldSetMapper<Feature>() {
					{
						setTargetType(Feature.class);
					}
				});
			}
		});
		return reader;
	}

	@Bean
	public Step step1(StepBuilderFactory stepBuilderFactory,
			ItemReader<Feature> reader, ItemWriter<Feature> writer,
			ItemProcessor<Feature, Feature> processor) {
		return stepBuilderFactory.get("step1")
				.transactionManager(new JpaTransactionManager(emf))
				.<Feature, Feature> chunk(10000).reader(reader)
				.processor(processor).writer(writer).build();
	}

	// end::jobstep[]

	@Bean
	public ItemWriter<Feature> writer(EntityManagerFactory emf) {
		JpaItemWriter<Feature> writer = new JpaItemWriter<Feature>();
		writer.setEntityManagerFactory(emf);
		return writer;
	}

}
