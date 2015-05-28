package next.web.qna;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import next.model.qna.Question;
import next.service.qna.ExistedAnotherUserException;
import next.service.qna.QnaService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value={"", "/questions"})
public class QuestionController {
	private static final Logger logger = LoggerFactory.getLogger(QuestionController.class);
	
	@Resource(name = "qnaService")
	private QnaService qnaService;

	@RequestMapping("")
	public String list(Model model) {
		model.addAttribute("questions", qnaService.findAll());
		return "qna/list";
	}
	
	@RequestMapping("/{id}")
	public String show(@PathVariable long id, Model model) {
		model.addAttribute("question", qnaService.findById(id));
		return "qna/show";
	}
	
	@RequestMapping("/form")
	public String form(Model model) {
		model.addAttribute("question", new Question());
		model.addAttribute("mode", "write");
		return "qna/form";
	}
	
	@RequestMapping("/{questionId}/form")
	public String editPage(@PathVariable("questionId") Long questionId, Model model) {
		model.addAttribute("question", qnaService.findById(questionId));
		model.addAttribute("mode", "edit");
		return "qna/form";
	}
	
	@RequestMapping(value="", method=RequestMethod.POST)
	public String save(@Valid Question question, BindingResult bindingResult, Model model) {
		logger.debug("Question : {}", question);
		if (bindingResult.hasFieldErrors()) {
			List<FieldError> errors = bindingResult.getFieldErrors();
			for (FieldError error : errors) {
				logger.debug("field : {}, error code : {}", error.getField(), error.getCode());
			}
			model.addAttribute("mode", "write");
			return "qna/form";
		}
		qnaService.save(question);
		return "redirect:/";
	}
	
	@RequestMapping(value="/{id}/edit", method=RequestMethod.POST)
	public String edit(@Valid Question question, BindingResult bindingResult, @PathVariable("id") long questionId, Model model) {
		logger.debug("Question : {}", question);
		if (bindingResult.hasFieldErrors()) {
			List<FieldError> errors = bindingResult.getFieldErrors();
			for (FieldError error : errors) {
				logger.debug("field : {}, error code : {}", error.getField(), error.getCode());
			}
			model.addAttribute("mode", "edit");
			question.setQuestionId(questionId);
			model.addAttribute("question", question);
			return "qna/form";
		}
		qnaService.edit(question, questionId);
		return "redirect:/";
	}
	
	@RequestMapping(value="/{id}/delete", method=RequestMethod.GET)
	public void delete(@PathVariable("id") long questionId, Model model, HttpServletResponse response, HttpServletRequest request) throws IOException {
		logger.debug("delete target Question : {}", questionId);
		response.setContentType("text/html; charset=utf-8");
		String referrer = request.getHeader("referer");
		PrintWriter out = response.getWriter();
		String script;
		try {
			qnaService.delete(questionId);
		} catch (ExistedAnotherUserException e) {
			logger.debug("댓글 존재");
			
			script = "<script>alert('댓글이 존재해서 삭제할 수 없어요'); location.href='" + referrer + "'</script>";
			out.print(script);
			out.flush();
			out.close();
		}
		script = "<script>location.href='http://" +referrer.split("/")[2].split("/")[0] + "';</script>";
		out.print(script);
		out.flush();
		out.close();
	}
}
