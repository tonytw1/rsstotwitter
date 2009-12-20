package nz.gen.wellington.rsstotwitter.repositories;

import nz.gen.wellington.rsstotwitter.model.TwitterAccount;

import org.springframework.orm.hibernate3.HibernateTemplate;

public class AccountDAO {

	
    private HibernateTemplate hibernateTemplate;

    
	public AccountDAO(HibernateTemplate hibernateTemplate) {	
		this.hibernateTemplate = hibernateTemplate;
	}

	
	public void saveAccount(TwitterAccount account) {	
		hibernateTemplate.saveOrUpdate(account);
	}

}
