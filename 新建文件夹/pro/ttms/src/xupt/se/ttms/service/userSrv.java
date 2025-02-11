package xupt.se.ttms.service;

import java.util.List;

import xupt.se.ttms.idao.DAOFactory;
import xupt.se.ttms.idao.iuserDAO;
import xupt.se.ttms.model.user;

public class userSrv {
	private iuserDAO userDAO=DAOFactory.creatuserDAO();
	
	public int add(user user){
		return userDAO.insert(user); 		
	}

	public String selectuserpassword(String username) {
		return userDAO.selectuserpassword(username);
	}
	
	public List<user> Fetch(String username){
		return userDAO.select(username);		
	}
	
	public List<user> FetchAll(){
		return userDAO.select("");		
	}
	public int delete(String username)
	{
		return userDAO.delete(username);
	}
	public int modify(user user)
	{
		return userDAO.modify(user);
	}
}