package next.model.qna;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

public class Question {
	private long questionId;

	@Size(min = 2, max = 30)
	private String writer;

	@Size(min = 2, max = 50)
	private String title;

	@NotEmpty
	private String contents;

	private Date createdDate;

	private int countOfComment;

	private List<Answer> answers;

	public Question() {
		this.createdDate = new Date();
	}

	public Question(String writer, String title, String contents) {
		this(0, writer, title, contents, new Date(), 0);
	}

	public Question(long questionId, String writer, String title, String contents, Date createdDate, int countOfComment) {
		this(questionId, writer, title, contents, new Date(), countOfComment, null);
	}

	private Question(long questionId, String writer, String title, String contents, Date createdDate, int countOfComment, List<Answer> answers) {
		this.questionId = questionId;
		this.writer = writer;
		this.title = title;
		this.contents = contents;
		this.createdDate = createdDate;
		this.countOfComment = countOfComment;
		this.answers = answers;
	}

	public long getQuestionId() {
		return questionId;
	}

	public void setQuestionId(long questionId) {
		this.questionId = questionId;
	}

	public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public long getTimeFromCreateDate() {
		return this.createdDate.getTime();
	}

	public int getCountOfComment() {
		return countOfComment;
	}

	public List<Answer> getAnswers() {
		return answers;
	}

	public Question withAnswers(List<Answer> answers) {
		return new Question(questionId, writer, title, contents, createdDate, countOfComment, answers);
	}
	
	public boolean canDelete() {
		if (answers == null || answers.isEmpty()) {
			return true;
		}
		
		List<Answer> anotherAnswers = answers.stream()
				.filter(a -> !a.isSameUser(writer))
				.collect(Collectors.toList());
		return anotherAnswers.isEmpty();
	}

	@Override
	public String toString() {
		return "Question [questionId=" + questionId + ", writer=" + writer + ", title=" + title + ", contents=" + contents + ", createdDate="
				+ createdDate + ", countOfComment=" + countOfComment + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (questionId ^ (questionId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Question other = (Question) obj;
		if (questionId != other.questionId)
			return false;
		return true;
	}
}
