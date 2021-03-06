package auto.master.service.user;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import auto.util.UserUtils;
import auto.datamodel.AuthStatus;
import auto.datamodel.Status;
import auto.datamodel.dao.ProxyUser;
import auto.master.dao.user.IProxyUserDao;
import auto.util.CommonUtils;

@Service("masterPUserService")
public class ProxyUserServiceImpl implements IProxyUserService {
    
    @Autowired
    private IProxyUserDao puserDao;

	@Override
	public ProxyUser updateInfo(String username, Map<String, Object> info) {
		ProxyUser user=puserDao.getPUser(username);
		if(user!=null){
			CommonUtils.updateInfo(user, info);
			puserDao.update(user);
		}
		return user;
	}

	@Override
	public ProxyUser createPUser(String telephone, String wechatId,
			String wechatName, String wechatFavicon,String openId) {
		ProxyUser user=new ProxyUser();
		
		user.setUsername(UserUtils.generateUsername());
		
		Date date=new Date();
		user.setCreateTime(date);
		
		user.setTelephone(telephone);
		user.setWechatId(wechatId);
		user.setWechatName(wechatName);
		user.setWechatFavicon(wechatFavicon);
		user.setOpenId(openId);
		//认证状态（0-未认证  1-审核中  2-未通过  3-已通过）
		user.setStatus(AuthStatus.UNKNOWN.ordinal());
		//数据状态（正常  或者  已删除）
		user.setDataStatus(Status.NORMAL.ordinal());
		
		puserDao.createPUser(user);
		
		return user;		
	}
}
