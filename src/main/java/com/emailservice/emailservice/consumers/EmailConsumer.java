package com.emailservice.emailservice.consumers;

import com.emailservice.emailservice.consumers.dto.EmailFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import java.util.Properties;

@Service
public class EmailConsumer {

    @Autowired
    ObjectMapper objectMapper;


    @KafkaListener(topics = "sendEmail", id="emailConsumerGroup")
    public void sendEmail(String message){

        EmailFormat emailMessage = null;
        try {
            emailMessage = objectMapper.readValue(message, EmailFormat.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        sendActualEmail(emailMessage);
    }



    private void sendActualEmail(EmailFormat emailMessage) {

        /*
          SMTP -> Simple Mail Transfer Protocal
         */

        final String fromEmail = "scalers94@gmail.com"; //requires valid gmail id
        final String password = "pxuviyhfqboncnki"; // correct password for gmail id
        final String toEmail = "scalers94@gmail.com"; // can be any email id

        System.out.println("TLSEmail Start");
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); //SMTP Host
        props.put("mail.smtp.port", "587"); //TLS Port
        props.put("mail.smtp.auth", "true"); //enable authentication
        props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS

        //create Authenticator object to pass in Session.getInstance argument
        Authenticator auth = new Authenticator() {
            //override the getPasswordAuthentication method
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        };
        Session session = Session.getInstance(props, auth);

        EmailUtil.sendEmail(session, toEmail,"TLSEmail Testing Subject", "TLSEmail Testing Body");

    }

}
