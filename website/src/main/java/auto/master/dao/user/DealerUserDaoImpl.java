package auto.master.dao.user;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import auto.dao.impl.DaoImpl;
import auto.datamodel.cache.CacheType;
import auto.datamodel.dao.DealerUser;

@Repository("masterDUserDao")
public class DealerUserDaoImpl extends DaoImpl implements IDealerUserDao {
    
	private Criterion getUsernameCriterion(Collection<String> usernames) {
		return Restrictions.or(
				Restrictions.in("username", usernames),
				Restrictions.in("telephone", usernames),
				Restrictions.in("openId", usernames),
				Restrictions.in("wechatId", usernames)
     );
	}
	
	private Criterion getUsernameCriterion(String username) {
		return Restrictions.or(
				Restrictions.eq("username", username),
				Restrictions.eq("telephone", username),
				Restrictions.eq("openId", username),
				Restrictions.eq("wechatId", username)
     );
	}
	
    @Override
    protected <T> void deprecatedCache(T object) {
        if (object == null) return ;
        if(object instanceof DealerUser){
        	DealerUser user=(DealerUser)object;
        	cacheManager.deprecate(CacheType.id2dealerUser, user.getId());
        	List<String> usernames=new ArrayList<String>();
        	if(user.getTelephone()!=null)usernames.add(user.getTelephone());
        	if(user.getWechatId()!=null) usernames.add(user.getWechatId());
        	cacheManager.mdeprecate(CacheType.username2DealerUser, usernames);
        }
    }

	@SuppressWarnings("unchecked")
	@Override
	public DealerUser getDUser(String username) {
		List<DealerUser> users = (List<DealerUser>) getSession().createCriteria(DealerUser.class)
                .add(getUsernameCriterion(username))
                .list();
        if (users.isEmpty()) {
            return null;
        }
        DealerUser minUser = null;
        if (users.size() > 1) {
            for (DealerUser user : users) {
                if (minUser == null || minUser.getId() > user.getId()) {
                    minUser = user;
                }
            }
        } else {
            minUser = users.get(0);
        }
        return minUser;
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<DealerUser> getDUsers(Collection<String> usernames) {
		if (CollectionUtils.isEmpty(usernames)) {
            return Collections.emptyList();
        }
        List<DealerUser> results = (List<DealerUser>)getSession().createCriteria(DealerUser.class)
                .add(getUsernameCriterion(usernames))
                .list();
        return results;
	}

	

	@Override
	public DealerUser createDUser(DealerUser user) {
		Validate.notNull(user);
		user.setModifyTime(new Date());
		save(user);
		return user;
	}
    
}
