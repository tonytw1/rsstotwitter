package nz.gen.wellington.rsstotwitter.repositories;

import java.util.List;

import nz.gen.wellington.rsstotwitter.model.TwitterAccount;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.stereotype.Component;

@Component
public class AccountDAO {
	
    private final HibernateTemplate hibernateTemplate;

    @Autowired
	public AccountDAO(HibernateTemplate hibernateTemplate) {	
		this.hibernateTemplate = hibernateTemplate;
	}
	
	public void saveAccount(TwitterAccount account) {	
		hibernateTemplate.saveOrUpdate(account);
	}

	@SuppressWarnings("unchecked")
	public TwitterAccount getUserByTwitterId(long id) {
		List<TwitterAccount> accounts = (List<TwitterAccount>) hibernateTemplate.findByCriteria(DetachedCriteria.forClass(TwitterAccount.class).add(Restrictions.eq("id", id)));
        if (!accounts.isEmpty()) {
        	return accounts.get(0);
        }
        return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<TwitterAccount> getAllTwitterAccounts() {
		return hibernateTemplate.loadAll(TwitterAccount.class);
	}

}
