package com.example.enterprisemessaging;

import java.nio.channels.IllegalSelectorException;

import org.springframework.cloud.Cloud;
import org.springframework.cloud.CloudFactory;
import org.springframework.cloud.service.ServiceConnectorConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.sap.cloud.servicesdk.xbem.core.MessagingService;
import com.sap.cloud.servicesdk.xbem.core.MessagingServiceFactory;
import com.sap.cloud.servicesdk.xbem.core.exception.MessagingException;
import com.sap.cloud.servicesdk.xbem.core.impl.MessagingServiceFactoryCreator;
import com.sap.cloud.servicesdk.xbem.extension.sapcp.jms.MessagingServiceJmsConnectionFactory;
import com.sap.cloud.servicesdk.xbem.extension.sapcp.jms.MessagingServiceJmsSettings;

@Configuration
public class EnterpriseMessagingConfig {
	
	@Bean
	public MessagingServiceFactory getMessagingServiceFactory() {
		ServiceConnectorConfig config = null;
		Cloud cloud = new CloudFactory().getCloud();
		MessagingService messagingservice = cloud.getSingletonServiceConnector(MessagingService.class, config);
		if(messagingservice == null) {
			throw new IllegalStateException("Unable to create the messagin service");
			
		}
		
		return MessagingServiceFactoryCreator.createFactory(messagingservice);		
		
	}
	
	@Bean
	public MessagingServiceJmsConnectionFactory getMessagingServicejmsfactory(MessagingServiceFactory messagingServiceFactory) {
		try {
			MessagingServiceJmsSettings settings = new MessagingServiceJmsSettings();
			settings.setMaxReconnectAttempts(5);
			settings.setInitialReconnectDelay(3000);
			settings.setReconnectDelay(3000);
			return messagingServiceFactory.createConnectionFactory(MessagingServiceJmsConnectionFactory.class,settings);
			
		}
		catch(MessagingException e) {
			throw new IllegalStateException("Unable to create the connection factory",e);
		}
		
		
		
	}
	
	
	
	

}
