package next.service.user;

import static next.model.user.UserAction.CREATE;
import static next.model.user.UserAction.LOGIN_FAILED;
import static next.model.user.UserAction.LOGIN_SUCCESS;
import static next.model.user.UserAction.LOGIN_TRY;
import static next.model.user.UserAction.UPDATE;

import javax.annotation.Resource;

import next.dao.user.UserDao;
import next.model.audit.AuditObject;
import next.model.user.User;
import next.service.audit.AuditService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UserService {
	private static Logger log = LoggerFactory.getLogger(UserService.class);

	@Resource(name = "userDao")
	private UserDao userDao;

	@Resource(name = "auditService")
	private AuditService auditService;

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public User join(User user) throws ExistedUserException {
		log.debug("User : {}", user);

		User existedUser = userDao.findByUserId(user.getUserId());
		if (existedUser != null) {
			throw new ExistedUserException(user.getUserId());
		}

		userDao.insert(user);
		auditService.log(new AuditObject(user.getUserId(), CREATE));
		return user;
	}

	public User login(String userId, String password) throws PasswordMismatchException {
		auditService.log(new AuditObject(userId, LOGIN_TRY));

		User existedUser = userDao.findByUserId(userId);
		if (existedUser == null) {
			auditService.log(new AuditObject(userId, LOGIN_FAILED));
			throw new PasswordMismatchException();
		}

		if (!existedUser.matchPassword(password)) {
			auditService.log(new AuditObject(userId, LOGIN_FAILED));
			throw new PasswordMismatchException();
		}

		auditService.log(new AuditObject(userId, LOGIN_SUCCESS));
		return existedUser;
	}

	public User findByUserId(String userId) {
		return userDao.findByUserId(userId);
	}

	public void update(String userId, User updateUser) throws PasswordMismatchException {
		User existedUser = userDao.findByUserId(userId);
		if (existedUser == null) {
			throw new NullPointerException(userId + " user doesn't existed.");
		}
		existedUser.update(updateUser);
		userDao.update(existedUser);
		auditService.log(new AuditObject(existedUser.getUserId(), UPDATE));
	}
}
