package auto.qr.dao.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import auto.dao.impl.ReadonlyDaoImpl;
import auto.datamodel.cache.CacheType;
import auto.datamodel.cache.PrimitiveCacheable;
import auto.datamodel.dao.CustomUser;
import auto.datamodel.dao.DealerUser;
import lombok.extern.apachecommons.CommonsLog;

@Repository
@SuppressWarnings("unchecked")
@CommonsLog
public class CustomUserDaoImpl extends ReadonlyDaoImpl implements ICustomUserDao {
	
	private Criterion getUsernameCriterion(String username) {
		return Restrictions.or(
				Restrictions.eq("username", username),
				Restrictions.eq("telephone", username),
				Restrictions.eq("openId", username),
				Restrictions.eq("wechatId", username)
     );
	}
	
	@Override
	public CustomUser getCUser(long id) {
		CustomUser user=cacheManager.get(CacheType.id2CustomUser, id);
		if(user==null){
			user=get(CustomUser.class, id);
			if(user==null){
				user=CustomUser.EMPTY;
			}
			cacheManager.set(CacheType.id2CustomUser, id, user);
		}
		return user.isEmpty()?null:user;
	}


	@Override
    public CustomUser getCUser(String username) {
        if (username == null) return null;
        PrimitiveCacheable value = cacheManager.get(CacheType.username2CustomUser, username);
        if (value != null) {
            if (value.isEmpty()) {
                return null;
            }
            Long id = value.get();
            return getCUser(id);
        } else {
            List<CustomUser> users = (List<CustomUser>) getSession().createCriteria(CustomUser.class)
                    .add(getUsernameCriterion(username))
                    .list();
            if (users.isEmpty()) {
                cacheManager.set(CacheType.username2CustomUser, username, PrimitiveCacheable.EMPTY);
                return null;
            }
            CustomUser minUser = users.get(0);
            cacheManager.set(CacheType.username2CustomUser, username, new PrimitiveCacheable(minUser.getId()));
            cacheManager.set(CacheType.id2CustomUser, minUser.getId(), minUser);
            return minUser;
        }
    }


	@Override
	public List<CustomUser> getCUsers(List<String> usernames) {
        if (CollectionUtils.isEmpty(usernames)) {
            return Collections.emptyList();
        }
        //get users from cache
        List<PrimitiveCacheable> values = cacheManager.mget(CacheType.username2CustomUser, usernames);
        List<Long> hitIds = new ArrayList<Long>();
        List<String> missUsernames = new ArrayList<String>();
        int i = 0;
        for (String username : usernames) {
            PrimitiveCacheable value = values.get(i++);
            if (value == null) {
                missUsernames.add(username);
            } else if (!value.isEmpty()) {
                Long id = value.get();
                hitIds.add(id);
            }
        }
        List<CustomUser> users = new ArrayList<CustomUser>();
        List<Long> missIds = new ArrayList<Long>();
        if (!hitIds.isEmpty()) {
            List<CustomUser> hitUsers = cacheManager.mget(CacheType.id2CustomUser, hitIds);
            for (i = 0; i < hitIds.size(); i++) {
                Long id = hitIds.get(i);
                CustomUser user = hitUsers.get(i);
                if (user == null) {
                    missIds.add(id);
                } else if (!user.isEmpty()) {
                    users.add(user);
                }
            }
        }
        //get miss users from database
        if (!CollectionUtils.isEmpty(missUsernames)) {
            List<CustomUser> missUsers = (List<CustomUser>)getSession().createCriteria(CustomUser.class)
                    .add(Restrictions.in("username", missUsernames))
                    .list();
            Map<String, CustomUser> name2User = new HashMap<String, CustomUser>();
            for (CustomUser user : missUsers) {
                //name2User.put(user.getUsername(), user);
                if (user.getTelephone() != null) name2User.put(user.getTelephone(), user);
            }
            Map<String, PrimitiveCacheable> username2Id = new HashMap<String, PrimitiveCacheable>();
            Map<Long, CustomUser> id2User = new HashMap<Long, CustomUser>();
            for (String username : missUsernames) {
                CustomUser user = name2User.get(username);
                if (user != null) {
                    username2Id.put(username, new PrimitiveCacheable(user.getId()));
                    id2User.put(user.getId(), user);
                    users.add(user);
                } else {
                    username2Id.put(username, PrimitiveCacheable.EMPTY);
                }
            }
            cacheManager.mset(CacheType.username2CustomUser, username2Id);
            cacheManager.mset(CacheType.id2CustomUser, id2User);
        }
        if (!CollectionUtils.isEmpty(missIds)) {
            List<CustomUser> missUsers = (List<CustomUser>)getSession().createCriteria(CustomUser.class)
                    .add(Restrictions.in("id", missIds))
                    .list();
//            log.info("mysql: " + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date().getTime()) + ", class User, method getUsers is one list, list size=" + missUsers.size()); 
            Map<Long, CustomUser> id2User = new HashMap<Long, CustomUser>();
            for (CustomUser user : missUsers) {
                id2User.put(user.getId(), user);
            }
            for (Long id : missIds) {
                CustomUser user = id2User.get(id);
                if (user != null) {
                    users.add(user);
                } else {
                    id2User.put(id, CustomUser.EMPTY);
                }
            }
            cacheManager.mset(CacheType.id2CustomUser, id2User);
        }
        
        return users;
	}

	@Override
	public CustomUser getUserByToken(String token) {
		PrimitiveCacheable value = cacheManager.get(CacheType.customeToken2Id, token);
        if (value != null) {
            if (value.isEmpty()) {
                return null;
            }
            Long id = value.get();
            return getCUser(id);
        }
        List<CustomUser> users = (List<CustomUser>) getSession().createCriteria(CustomUser.class)
                .add(Restrictions.eq("token", token))
                .list();
        if (users.size() > 1) {
            log.error("duplicated token! token = " + token);
        }
        if (CollectionUtils.isEmpty(users)) {
            cacheManager.set(CacheType.customeToken2Id, token, PrimitiveCacheable.EMPTY);
            return null;
        }
        CustomUser user = users.get(0);
        cacheManager.set(CacheType.customeToken2Id, token, new PrimitiveCacheable(user.getId()));
        cacheManager.set(CacheType.id2CustomUser, user.getId(), user);
        return user;
	}
    
}
