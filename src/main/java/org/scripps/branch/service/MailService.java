package org.scripps.branch.service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;

import org.scripps.branch.entity.DatasetRequest;
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
    
    public void startSendDatasetRequestMail(DatasetRequest dr){
    	sendDatasetRequestMail(dr);
    }
    
    @Async
    private void sendDatasetRequestMail(DatasetRequest dr){
    	LOGGER.debug("Thread name {}",Thread.currentThread().getName());
    	String htmlMsg = "Hello Ben"+",<br><br>"
    			+"There is a new dataset request on Branch. <br><br>"+"<table><tbody>"
    			+"<tr>"
    			+"<td>"+"Name"+"</td>"
    			+"<td>"+dr.getFirstName()+" "+dr.getLastName()+"</td>"
    			+"</tr>"
    			+"<tr>"
    			+"<td>"+"Email"+"</td>"
    			+"<td>"+dr.getEmail()+"</td>"
    			+"</tr>"
    			+"<tr>"
    			+"<td>"+"Dataset Description"+"</td>"
    			+"<td>"+dr.getDataDescription()+"</td>"
    			+"</tr>"
    			+"<tr>"
    			+"<td>"+"Why this Data?"+"</td>"
    			+"<td>"+dr.getReason()+"</td>"
    			+"</tr>"
    			+"<tr>"
    			+"<td>"+"Do you want this public?"+"</td>"
    			+"<td>"+dr.getPrivateToken()+"</td>"
    			+"</tr>"
        		+ "</table></tbody><br><br>Best Regards,<br>Admin, Branch";
    	MimeMessage mimeMessage = mailSender.createMimeMessage();
    	MimeMessageHelper helper;
		try {
			mimeMessage.setContent(htmlMsg, "text/html");
			helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
			helper.setTo(from);
	    	helper.setSubject("BioBranch: Dataset Request");
	    	helper.setFrom(from);
	    	mailSender.send(mimeMessage);
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			LOGGER.error("Messaging Exception",e);
		}
    }
    
}