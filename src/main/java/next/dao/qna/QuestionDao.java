package next.dao.qna;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import next.model.qna.Question;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import core.jdbc.AbstractJdbcDaoSupport;

@Repository
public class QuestionDao extends AbstractJdbcDaoSupport {
	private static final Logger logger = LoggerFactory.getLogger(QuestionDao.class);
	
	public void insert(Question question) {
		String sql = "INSERT INTO QUESTIONS (writer, title, contents, createdDate, countOfComment) VALUES (?, ?, ?, ?, ?)";
		getJdbcTemplate().update(sql, 
				question.getWriter(), 
				question.getTitle(), 
				question.getContents(),
				new Timestamp(question.getTimeFromCreateDate()), 
				question.getCountOfComment());
	}
	
	public List<Question> findAll() {
		String sql = "SELECT questionId, writer, title, createdDate, countOfComment FROM QUESTIONS "
				+ "order by questionId desc";
		
		RowMapper<Question> rm = new RowMapper<Question>() {
			@Override
			public Question mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new Question(rs.getLong("questionId"),
						rs.getString("writer"), rs.getString("title"), null,
						rs.getTimestamp("createdDate"),
						rs.getInt("countOfComment"));
			}
			
		};
		
		return getJdbcTemplate().query(sql, rm);
	}

	public Question findById(long questionId) {
		String sql = "SELECT questionId, writer, title, contents, createdDate, countOfComment FROM QUESTIONS "
				+ "WHERE questionId = ?";
		
		RowMapper<Question> rm = new RowMapper<Question>() {
			@Override
			public Question mapRow(ResultSet rs, int rowNum) throws SQLException {
				return new Question(rs.getLong("questionId"),
						rs.getString("writer"), rs.getString("title"),
						rs.getString("contents"),
						rs.getTimestamp("createdDate"),
						rs.getInt("countOfComment"));
			}
			
		};
		
		return getJdbcTemplate().queryForObject(sql, rm, questionId);
	}
	
	public void increaseCommentCount(long questionId) {
		updateCommentCount(questionId, "+ 1");
	}
	
	public void decreaseCommentCount(long questionId) {
		updateCommentCount(questionId, "- 1");
	}
	
	public void updateCommentCount(long questionId, String value) {
		String sql = String.format("UPDATE QUESTIONS set countOfComment = countOfComment %s WHERE questionId = ?", value);
		logger.debug("sql : {}", sql);
		getJdbcTemplate().update(sql, questionId);
	}
	
	public void delete(long questionId) {
		String sql = "DELETE FROM QUESTIONS WHERE questionId = ?";
		getJdbcTemplate().update(sql, questionId);
	}

	public void edit(Question question, long questionId) {
		String sql = "UPDATE QUESTIONS SET writer=?, title=?, contents=? WHERE questionId = ?";
		getJdbcTemplate().update(sql, question.getWriter(), question.getTitle(), question.getContents(), questionId);
	}
}
