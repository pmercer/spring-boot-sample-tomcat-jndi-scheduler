/*
 * Copyright 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sample.tomcat.jndi;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.util.descriptor.web.ContextResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import sample.tomcat.jndi.utils.SampleUtil;

@SpringBootApplication
@EnableScheduling
public class SampleTomcatJndiApplication {

	private static final Logger log = LoggerFactory.getLogger(SampleTomcatJndiApplication.class);	
	
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
	
	@Autowired
	private SampleUtil sampleUtil;
    
	public static void main(String[] args) {
		SpringApplication.run(SampleTomcatJndiApplication.class, args);
	}

	@Scheduled(
			initialDelay = 1000,
			fixedRate = 60000
			)
    public void run() {
        log.info("Running scheduled task at {}", dateFormat.format(new Date()));

        try {
        	/**
        	 * throws the following error:
        	 * 
        	 * javax.naming.NameNotFoundException: Name [comp/env/jdbc/myDataSource] is not bound in this Context. Unable to find [comp].
        	 * 
        	 */
			log.info(sampleUtil.getDataSourcefromJNDI());
        	
        	/**
        	 * throws the following error:
        	 * 
        	 * org.springframework.jndi.JndiLookupFailureException: JndiObjectTargetSource failed to obtain new target object; nested exception is javax.naming.NameNotFoundException: Name [comp/env/jdbc/myDataSource] is not bound in this Context. Unable to find [comp].
        	 * 
        	 */
			// log.info(sampleUtil.getDataSourcefromFactoryBean());

        } catch (NamingException e) {
			e.printStackTrace();
		}
    }
	
	@Bean
	public TomcatEmbeddedServletContainerFactory tomcatFactory() {
		return new TomcatEmbeddedServletContainerFactory() {

			@Override
			protected TomcatEmbeddedServletContainer getTomcatEmbeddedServletContainer(
					Tomcat tomcat) {
				tomcat.enableNaming();
				return super.getTomcatEmbeddedServletContainer(tomcat);
			}

			@Override
			protected void postProcessContext(Context context) {
				ContextResource resource = new ContextResource();
				resource.setName("jdbc/myDataSource");
				resource.setType(DataSource.class.getName());
				resource.setProperty("driverClassName", "your.db.Driver");
				resource.setProperty("url", "jdbc:yourDb");
				context.getNamingResources().addResource(resource);
			}
		};
	}

	@Bean(destroyMethod="")
	public DataSource jndiDataSource() throws IllegalArgumentException, NamingException {
		JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
		bean.setJndiName("java:comp/env/jdbc/myDataSource");
		bean.setProxyInterface(DataSource.class);
		bean.setLookupOnStartup(false);
		bean.afterPropertiesSet();
		return (DataSource)bean.getObject();
	}
	
}
