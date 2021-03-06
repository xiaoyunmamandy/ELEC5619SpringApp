package au.usyd.elec5619.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import au.usyd.elec5619.domain.Admin;
import au.usyd.elec5619.domain.User;
import au.usyd.elec5619.DAO.*;
import au.usyd.elec5619.service.*;

@Transactional
@Service(value = "usermanager")
public class UserService implements Usercreater {

	@Autowired
	public UserDAO userDAO;
	@Autowired
	public AdminDAO adminDAO;

	// 注册 
	public boolean addUser(User user) {
		User theuser = userDAO.getuserbyemail(user.getEmail());
		if(theuser==null) {
			userDAO.addUser(user);
			return true;
		}
		else {
			return false;
		}
		
	}
	//登录
	public int logincheck(String email, String pwd) {
		User user = userDAO.getuserbyemail(email);
		if(user==null) {
			System.out.println("not exist");
			//用户不存在
			return 1;
		}
		else if(pwd.equals(user.getPassword())) {
			//登录成功
			System.out.println("success");
			return 2;
		}
		else {
			//密码不匹配
			System.out.println("password fail");
			return 0;
		}
	}
	//按email找user
	public User getuserbyemail(String email) {
		User user = userDAO.getuserbyemail(email);
		return user;
	}

	public User getUserById(int id) {
		User user = userDAO.getUserById(id);
		return user;
	}

	public void updateUser(User user) {
		userDAO.updateUser(user);
	}

	public void deleteUserById(String id) {
		userDAO.deleteUserById(id);
	}

	public List<User> getallusers() {
		List<User> userlist = userDAO.getallusers();
		System.out.println("1111");
		return userlist;
	}
	public void trade(int point,int adduserid, int minuserid) {
		if(adduserid != 0) {
			User adduser = userDAO.getUserById(adduserid);
		    int newpoint = adduser.getPoints()+point;
		    adduser.setPoints(newpoint);
		    userDAO.updateUser(adduser);
		}
		if(minuserid!=0) {
			User minuser = userDAO.getUserById(minuserid);
			int newpoint = minuser.getPoints()-point;
			minuser.setPoints(newpoint);
			userDAO.updateUser(minuser);
		}
		
	}
	//管理员登录
	public int adminlogincheck(String adminName, String pwd) {
		Admin admin = adminDAO.getuserbyname(adminName);
		if(admin==null) {
			System.out.println("not exist");
			//用户不存在
			return 1;
		}
		else if(pwd.equals(admin.getPassword())) {
			//登录成功
			System.out.println("success");
			return 2;
		}
		else {
			//密码不匹配
			System.out.println("password fail");
			return 0;
		}
	}

	
}
