package com.example.enterprisemessaging;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.sap.cloud.servicesdk.xbem.core.exception.MessagingException;
import com.sap.cloud.servicesdk.xbem.extension.sapcp.jms.MessagingServiceJmsConnectionFactory;


@RestController
class SubscriptionController{
	
	 private static final String QUEUE_PATH = "queue/{queuename}";
	 private static final String MESSAGE_PATH = "/message";
	 private static final String MESSAGE_QUEUE_REST_PATH = QUEUE_PATH + MESSAGE_PATH;
	 private static final String QUEUE_PREFIX = "queue:";
	 
	 private MessagingServiceJmsConnectionFactory connectionFactory;
	 
	 
	 @Autowired
	 private SubscriptionController(MessagingServiceJmsConnectionFactory messagingServiceJmsConnectionFactory) {
		 
		 this.connectionFactory = messagingServiceJmsConnectionFactory;
	 }

	
	 @GetMapping(path = "/")
	 public ResponseEntity<String[]> hello() {
	 	
	 	
	 	String [] messages = new String[2];
	 	messages[0] ="Try GET ON ";
	 	messages[1] = "queue/{queuename}/message";
	    return new ResponseEntity<String[]>(messages, HttpStatus.OK);
	 }


@GetMapping(MESSAGE_QUEUE_REST_PATH)
public ResponseEntity<String> receiveMessageFromQueue( @PathVariable String queuename ) throws MessagingException
{
	try {
        queuename = decodeValue(queuename);
    } catch (UnsupportedEncodingException e1) {
        return ResponseEntity.badRequest().body("Unable to decode the queuename");
    }
	
	try(Connection connection = connectionFactory.createConnection(); 
			Session session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE) ){
		connection.start();
		Queue queue =session.createQueue(QUEUE_PREFIX + "saprefapps/msg-client/bupa-1234/abcdef");
		MessageConsumer consumer = session.createConsumer(queue);
		BytesMessage bytemessage = (BytesMessage)consumer.receive();
		byte[] byteData = new byte[(int) bytemessage.getBodyLength()];
		bytemessage.readBytes(byteData);
	    return ResponseEntity.status(HttpStatus.OK).body(new String(byteData)); 
	
	}
	catch(JMSException e){
		
	    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not send message. Error" + e);
		
	}
	
}


private String decodeValue(String value) throws UnsupportedEncodingException{
	
	return URLDecoder.decode(value,StandardCharsets.UTF_8.toString());
	
	
}
	
	


}
