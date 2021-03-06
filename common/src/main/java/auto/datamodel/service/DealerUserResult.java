package auto.datamodel.service;

import auto.datamodel.dao.DealerUser;
/**
 * 大B 代理商 -server层返回信息
 */
public class DealerUserResult  {
    
    public DealerUser user;
    
    public DealerUserResult(int errorCode, String errorMsg) {
        /*this.status = false;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;*/
    }
    public String username;//账号
    public String telephone;//电话
    public String nickName;//昵称
    public String favicon;//头像地址
    public int gender;//性别
    public int status;//认证状态  0:未认证  1:审核中  2:未通过  3:已通过
    //返回用户信息
    public DealerUserResult (DealerUser user){
    	this.username=user.getUsername();
    	this.telephone=user.getTelephone();
    	this.nickName=user.getNickName();
    	this.favicon=user.getFavicon();
    	this.gender=user.getGender();
    	this.status=user.getStatus();
    	
    }
    
    public DealerUserResult() {
		
	}
}
