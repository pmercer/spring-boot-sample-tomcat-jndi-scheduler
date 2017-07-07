/*
 * Copyright 2017 the original author or authors.
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

package sample.tomcat.jndi.utils;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SampleUtil {

	private static final Logger log = LoggerFactory.getLogger(SampleUtil.class);	
	
	@Autowired
	private DataSource dataSource;
	
	public String getDataSourcefromFactoryBean() throws NamingException {
		log.debug("getting DataSource from FactoryBean ...");
		
		String response =
				String.format("DataSource retrieved from JNDI using JndiObjectFactoryBean: {%s}", dataSource);
		
		log.debug(response);
		
		return response;
	}
	
	public String getDataSourcefromJNDI() throws NamingException {
		log.debug("getting DataSource directly from JNDI ...");
		
		DataSource dataSource =
				(DataSource) new InitialContext().lookup("java:comp/env/jdbc/myDataSource");

		String response =
				String.format("DataSource retrieved directly from JNDI: {%s}", dataSource);
		
		log.debug(response);
		
		return response;
	}
	
}
