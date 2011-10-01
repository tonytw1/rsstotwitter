package nz.gen.wellington.rsstotwitter.repositories;

import java.util.List;

import nz.gen.wellington.rsstotwitter.model.TwitterAccount;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate3.HibernateTemplate;

public class AccountDAO {
	
    private HibernateTemplate hibernateTemplate;
    
	public AccountDAO(HibernateTemplate hibernateTemplate) {	
		this.hibernateTemplate = hibernateTemplate;
	}
	
	public void saveAccount(TwitterAccount account) {	
		hibernateTemplate.saveOrUpdate(account);
	}

	@SuppressWarnings("unchecked")
	public TwitterAccount getUserByTwitterId(int id) {
        List<TwitterAccount> accounts = hibernateTemplate.findByCriteria(DetachedCriteria.forClass( TwitterAccount.class ).add( Restrictions.eq( "id", id)));
        if (accounts.isEmpty()) {
        	return accounts.get(0);
        }
        return null;
	}

}
