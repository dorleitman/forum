package edu.forum.server.security;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;

import edu.forum.server.domain.Controller;
import edu.forum.shared.AuthorizationLevel;
import edu.forum.shared.Constants;
import edu.forum.shared.Post;
import edu.forum.shared.User;

public class SecurityUtils {
	static Logger log = Logger.getLogger(SecurityUtils.class.getName());
	
	public static boolean login(Controller controller, String username, String password) throws RemoteException{
		log.info("got request to log from " + username);
		if(controller.getUsers().get(username).getPassword().equals(password)){
			controller.getUsers().get(username).setLoggedIn(true);
			return true;
		}
		log.warn("failed login " + username);
		return false;
	}
	
	public static void logout(Controller controller, User toLogout) throws RemoteException {
		log.info("got request to logout from " + toLogout.getUsername());
		controller.getUsers().get(toLogout.getUsername()).setLoggedIn(false);
	}

	public static boolean isLoggedIn(Controller controller, User toLogout) throws RemoteException {
		return (!(controller.getUsers().get(toLogout.getUsername()).getLevel() == AuthorizationLevel.GUEST)
				&& !(toLogout.getUsername().equals(Constants.GUEST_USER_NAME))
				&& controller.getUsers().get(toLogout.getUsername()).isLoggedIn());
	}

	public static boolean isAuthorizedToPost(Controller controller, Post current, String username) throws RemoteException {
		if(controller.getUsers().containsKey(username))
			return controller.getUsers().get(username).getLevel() != AuthorizationLevel.GUEST;
		else return false;
	}

	public static boolean register(Controller controller, User toRegister) throws RemoteException {
		log.info("got request to log from " + toRegister.getUsername());
		User prev = controller.getUsers().putIfAbsent(toRegister.getUsername(), toRegister);
		if(prev != null)
			return false;
		else
			toRegister.setLevel(AuthorizationLevel.MEMBER);
			toRegister.setLoggedIn(true);
			return true;
	}
}
