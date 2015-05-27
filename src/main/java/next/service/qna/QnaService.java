package next.service.qna;

import java.util.List;

import javax.annotation.Resource;

import next.dao.qna.AnswerDao;
import next.dao.qna.QuestionDao;
import next.model.qna.Answer;
import next.model.qna.Question;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Scope("prototype")
public class QnaService {
	@Resource(name = "questionDao")
	private QuestionDao questionDao;
	
	@Resource(name = "answerDao")
	private AnswerDao answerDao;
	
	private Question question;
	
	public Question findById(long questionId) {
		question = questionDao.findById(questionId);
		List<Answer> answers = answerDao.findAllByQuestionId(questionId);
		return question.withAnswers(answers);
	}

	public List<Question> findAll() {
		return questionDao.findAll();
	}

	public void save(Question question) {
		questionDao.insert(question);
	}
	
	public void delete(final long questionId) throws ExistedAnotherUserException {
		question = findById(questionId);
		
		if (!question.canDelete()) {
			throw new ExistedAnotherUserException("다른 사용자가 추가한 댓글이 존재해 삭제할 수 없습니다.");
		}

		questionDao.delete(questionId);
	}

	@Transactional(propagation=Propagation.REQUIRED)
	public void addAnswer(long questionId, Answer answer) {
		answer.setQuestionId(questionId);
		answerDao.insert(answer);
		questionDao.increaseCommentCount(questionId);
	}

	@Transactional(propagation=Propagation.REQUIRED)
	public void deleteAnswer(long questionId, long answerId) {
		answerDao.delete(answerId);
		questionDao.decreaseCommentCount(questionId);
	}

	public void edit(Question question, long questionId) {
		questionDao.edit(question, questionId);
	}
}
