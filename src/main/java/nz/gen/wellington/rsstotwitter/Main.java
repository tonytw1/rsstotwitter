package nz.gen.wellington.rsstotwitter;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.sql.DataSource;
import java.util.Properties;

@EnableScheduling
@EnableAutoConfiguration
@ComponentScan
@Configuration
public class Main {

    private final static Logger log = Logger.getLogger(Main.class);

    private static ApplicationContext ctx;

    public static void main(String[] args) throws Exception {
        ctx = SpringApplication.run(Main.class, args);
    }

    @Bean
    public DataSource dataSourceBean(@Value("${database.host}") String hostname,
                                     @Value("${database.name}") String database,
                                     @Value("${database.username}")String username,
                                     @Value("${database.password}")String password) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://" + "localhost" + "/" + "rsstotwitter" + "?useUnicode=true&amp;characterEncoding=UTF-8");
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setMaxActive(10);
        dataSource.setMaxIdle(5);
        return dataSource;
    }

     /*
    <bean id="sessionFactory" class="org.springframework.orm.hibernate3.LocalSessionFactoryBean" lazy-init="false">
  		<property name="mappingResources">
    		<list>
	    		<value>Tweet.hbm.xml</value>
	    		<value>TwitterAccount.hbm.xml</value>
        		<value>TwitterEvent.hbm.xml</value>
       			<value>Feed.hbm.xml</value>
       			 <value>FeedToTwitterJob.hbm.xml</value>
  	 		</list>
  		</property>
  		<property name="hibernateProperties">
    		<props>
    			<prop key="hibernate.transaction.auto_close_session">false</prop>
      			<prop key="hibernate.current_session_context_class">org.hibernate.context.ThreadLocalSessionContext</prop>
      			<prop key="hibernate.dialect">org.hibernate.dialect.MySQLDialect</prop>
      			<prop key="hibernate.cache.provider_class">org.hibernate.cache.EhCacheProvider</prop>
		      	<prop key="hibernate.cache.use_query_cache">false</prop>
      			<prop key="hibernate.cache.use_second_level_cache">false</prop>
		    </props>
		</property>
 		<property name="dataSource">
    		<ref bean="dataSource"/>
		</property>
	</bean>
     */
    @Bean
    public LocalSessionFactoryBean sessionFactoryBean(DataSource dataSource) {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();

        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        sessionFactory.setHibernateProperties(hibernateProperties);

        sessionFactory.setMappingResources(
                "Tweet.hbm.xml",
                "TwitterAccount.hbm.xml",
                "TwitterEvent.hbm.xml",
                "Feed.hbm.xml",
                "FeedToTwitterJob.hbm.xml");

        sessionFactory.setDataSource(dataSource);
        return sessionFactory;
    }

    @Bean
    public HibernateTemplate hibernateTemplateBean(SessionFactory sessionFactory) {
        return new HibernateTemplate(sessionFactory);
    }

}

