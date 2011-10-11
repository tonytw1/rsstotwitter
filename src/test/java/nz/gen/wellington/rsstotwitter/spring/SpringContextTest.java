package nz.gen.wellington.rsstotwitter.spring;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

public class SpringContextTest {
	
	@Test
	public void springContextLoadsCorrectly() throws Exception {
		FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext("src/main/webapp/WEB-INF/rsstotwitter-servlet.xml");
		assertNotNull(context);
	}

}
