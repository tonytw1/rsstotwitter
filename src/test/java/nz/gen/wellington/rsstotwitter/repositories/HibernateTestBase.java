package nz.gen.wellington.rsstotwitter.repositories;

import nz.gen.wellington.rsstotwitter.model.Tweet;
import nz.gen.wellington.rsstotwitter.model.TwitterAccount;
import nz.gen.wellington.rsstotwitter.model.TwitterEvent;
import nz.gen.wellington.rsstotwitter.model.TwitteredFeed;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.springframework.orm.hibernate3.HibernateTemplate;

import junit.framework.TestCase;

// TODO make these rollback tests
public class HibernateTestBase extends TestCase {

    protected HibernateTemplate hibernateTemplate;
    
    protected void setUp() throws Exception { 
        Configuration configuration = new Configuration();
        configuration.setProperty(Environment.DRIVER, "com.mysql.jdbc.Driver");
        configuration.setProperty(Environment.URL, "jdbc:mysql://dev.local/rsstotwittertest?useUnicode=true&amp;characterEncoding=UTF-8");
        configuration.setProperty(Environment.USER, "www");
        configuration.setProperty(Environment.PASS, "www");
        configuration.setProperty(Environment.DIALECT, "org.hibernate.dialect.MySQLDialect");
        configuration.setProperty(Environment.SHOW_SQL, "true");

        configuration.addClass(TwitteredFeed.class);
        configuration.addClass(TwitterAccount.class);
        configuration.addClass(TwitterEvent.class);
        configuration.addClass(Tweet.class);
        
        SessionFactory sessionFactory = configuration.buildSessionFactory();        
        hibernateTemplate = new HibernateTemplate(sessionFactory);        
    }

}
