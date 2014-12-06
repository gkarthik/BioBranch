package org.scripps.branch.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;

import org.scripps.branch.entity.User;
import org.scripps.branch.tests.MailServiceTest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service("mailService")
@Transactional
public class MailService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MailService.class);
    
    @Autowired
    private JavaMailSender mailSender;
    
    private String from = "bgood@scripps.edu";
    
    public void startSendResetMail(User u){
    	sendResetPasswordMail(u);
    }
    
    @Async
    private void sendResetPasswordMail(User u){
    	LOGGER.debug("Thread name {}",Thread.currentThread().getName());
    	String htmlMsg = "Hello "
        		+ u.getFirstName()+",<br><br>"
        		+ "Please visit <a href=\"http://biobranch.org/authenticate/"+u.getToken().getUid()+"/\" target=\"_blank\">http://biobranch.org/authenticate/"+u.getToken().getUid()+"</a> to reset your password.<br><br>"
        		+ "Best Regards,<br>Ben";
    	MimeMessage mimeMessage = mailSender.createMimeMessage();
    	MimeMessageHelper helper;
		try {
			mimeMessage.setContent(htmlMsg, "text/html");
			helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
			helper.setTo(u.getEmail());
	    	helper.setSubject("BioBranch: Reset Password Instructions");
	    	helper.setFrom(from);
	    	mailSender.send(mimeMessage);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			LOGGER.error("Messaging Exception",e);
		}
    }
    
}