package edu.forum.server.data;

import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import edu.forum.server.domain.Controller;
import edu.forum.shared.Constants;
import edu.forum.shared.Post;
import edu.forum.shared.User;



public class DataUtils {
	static Logger log = Logger.getLogger(DataUtils.class.getName());
	private static Post mainPost;
	
	public static void populateData(Controller controller) throws RemoteException, InterruptedException{
		log.info("populating data");
		
		setMainPost(new Post("main", "", Constants.MAIN_POST_WRITTER, new Timestamp(0), null));
		getMainPost().setSubForum(true);
		controller.getPosts().put(getMainPost().getTitle() , getMainPost());
		
		for (int i = 0; i < Constants.INITIAL_NUM_OF_USERS; i++) {
			controller.getUsers().put("name-" + i, new User("name-" + i,"pass" + i));
		}
		for (int i = 0; i < Constants.INITIAL_SUB_FORUM_SIZE; i++) {
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			Post toAdd = new Post("sub-forum-" + i, "", Constants.MAIN_POST_WRITTER, timestamp, getMainPost());
			toAdd.setSubForum(true);
			TimeUnit.MILLISECONDS.sleep(1);
			getMainPost().addReply(toAdd);
			controller.getPosts().put("sub-forum-" + i, toAdd);
		}
		for (User user : controller.getUsers().values()) {
			for (int i = 0; i < Constants.INITIAL_SUB_FORUM_SIZE; i++) {
				post(controller.getPosts().get("sub-forum-" + i),
						new Post("title-" + i + " " + user.getUsername(),
								"content", user.getUsername(), new Timestamp(System.currentTimeMillis()),
								controller.getPosts().get("sub-forum-" + i)));
			}
		}
	}

	public static void post(Post father, Post child) throws RemoteException {
		log.trace("posting message: "  + child.getTitle());
		father.addReply(child);
	}

	public static Post getMainPost() {
		return mainPost;
	}

	public static void setMainPost(Post mainPost) {
		DataUtils.mainPost = mainPost;
	}


}
