package nz.gen.wellington.rsstotwitter.repositories.mysql;

import java.util.List;

import nz.gen.wellington.rsstotwitter.model.TwitterAccount;

import nz.gen.wellington.rsstotwitter.repositories.AccountDAO;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.stereotype.Component;

@Component
public class MysqlAccountDAO implements AccountDAO {
	
    private final HibernateTemplate hibernateTemplate;

    @Autowired
	public MysqlAccountDAO(HibernateTemplate hibernateTemplate) {
		this.hibernateTemplate = hibernateTemplate;
	}
	
	@Override
	public void saveAccount(TwitterAccount account) {
		hibernateTemplate.saveOrUpdate(account);
	}

	@Override
	@SuppressWarnings("unchecked")
	public TwitterAccount getUserByTwitterId(long id) {
		List<TwitterAccount> accounts = (List<TwitterAccount>) hibernateTemplate.findByCriteria(DetachedCriteria.forClass(TwitterAccount.class).add(Restrictions.eq("id", id)));
        if (!accounts.isEmpty()) {
        	return accounts.get(0);
        }
        return null;
	}

}
