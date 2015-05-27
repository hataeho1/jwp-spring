package next.dao;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import core.jdbc.AbstractJdbcDaoSupport;

@Component
public class DBInitializer extends AbstractJdbcDaoSupport{
	private static final Logger logger = LoggerFactory.getLogger(DBInitializer.class);

	@PostConstruct
	public void DBInit() {
		ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		populator.addScript(new ClassPathResource("jwp.sql"));
		DatabasePopulatorUtils.execute(populator, getDataSource());
		
		logger.info("Initialized Database Schema!");
	}
}
