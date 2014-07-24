package org.scripps.branch.tests;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.scripps.branch.config.ApplicationContext;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationContext.class }, loader = AnnotationConfigWebContextLoader.class)
@WebAppConfiguration
public class AttributeJobTest {

	@Autowired
	private Job job;
	@Autowired
	private JobLauncher jobLauncher;
	private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

	@After
	public void cleanUpStreams() {
		// reset JVM standard
		System.out.println("End ");
	}

	/** Simple Launch Test. */
	@Test
	public void launchJob() throws Exception {
		// launch the job
		JobExecution jobExecution = jobLauncher.run(job, new JobParameters());

		// assert job run status
		assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
	}

	@Before
	public void setUpStreams() {
		// catch system out
		System.out.println("Start");
	}

}