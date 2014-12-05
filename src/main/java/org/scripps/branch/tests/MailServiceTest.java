package org.scripps.branch.tests;

import java.io.InputStream;

import javax.mail.MessagingException;
import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.scripps.branch.config.ApplicationContextConfig;
import org.scripps.branch.repository.UserRepository;
import org.scripps.branch.service.AttributeService;
import org.scripps.branch.service.AttributeServiceImpl;
import org.scripps.branch.service.MailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ApplicationContextConfig.class }, loader = AnnotationConfigWebContextLoader.class)
@WebAppConfiguration
public class MailServiceTest {
	
	@Autowired
	MailService mailService;
	
	@Autowired
	UserRepository uRepo;

	@Test
	public void sendMail() {
		mailService.startSendResetMail(uRepo.findByEmail("gk@gk.com"));
	}

}
