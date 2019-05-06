package com.hsbc.lu.ao;

import java.util.Date;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailUtil {

	private String SMTPHost = "smtp.163.com", SMTPPort = "25";
	private String emailAccount="someone@163.com", emailPassword="", fromEMailAddress = "someone@163.com", toEMailAddress = "anotherone@chinasofti.com";
	private String emailTitle = "test email API";
	private String emailContent = "亲爱的HSBC:\n这是一封来自于HSBC-Lufax发送的关于AccoutOpen的邮件。\n希望开户的账户种类：卓越理财。\n\n\n\n\n\nHSBC-Lufax";
	//TODO: get the content from properties file
	
	public static void main (String[] arg) {
		EmailUtil em = new EmailUtil();
		em.sendEmail();
	}
	public void sendEmail() {

	      Properties props = new Properties();
	      props.put("mail.smtp.auth", "true");
	      props.put("mail.smtp.starttls.enable", "true");
	      props.put("mail.smtp.host", SMTPHost);
	      props.put("mail.smtp.port", SMTPPort);
	      //props.put("mail.smtp.ssl.trust", SMTPHost);

	      // Get the Session object.
	      Session session = Session.getInstance(props,
	      new Authenticator() {
	         protected PasswordAuthentication getPasswordAuthentication() {
	            return new PasswordAuthentication(emailAccount, emailPassword);
	         }
	      });
	      
	      try {
	          // Create a default MimeMessage object.
	          Message message = new MimeMessage(session);

	          message.setFrom(new InternetAddress(fromEMailAddress));

	          message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEMailAddress));
//	          message.setHeader("X-Mailer", "JavaMail");
	          message.setSubject(emailTitle);

	          Multipart multipart = messageMultipart();
	          message.setContent(multipart);
	          message.setSentDate(new Date());

	          Transport.send(message);

	          System.out.println("Sent message successfully.");

	       } catch (MessagingException e) {
	             throw new RuntimeException(e);
	       }
	}
	
	public Multipart messageMultipart() throws MessagingException {

        // BodyPart to hold message body
        BodyPart messageBody = new MimeBodyPart();
        // Now set the actual message
        messageBody.setText(emailContent);
        
        Multipart multipart = new MimeMultipart();

        // Add Message BodyPart to Multipart
        multipart.addBodyPart(messageBody);

        return multipart;
    }

}
