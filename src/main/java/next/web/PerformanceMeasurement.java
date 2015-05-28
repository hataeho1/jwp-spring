package next.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class PerformanceMeasurement extends HandlerInterceptorAdapter{
	private static final Logger LOG = LoggerFactory.getLogger(PerformanceMeasurement.class);

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		long startTime = getCurrentTime();
		request.setAttribute("startTime", startTime);
		return true;
	}

	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
		long startTIme = (long) request.getAttribute("startTime");
		long endTime = getCurrentTime();
		LOG.debug("Class : {} // executeTime : {}", handler.getClass(), endTime - startTIme);
		super.afterCompletion(request, response, handler, ex);
	}

	private long getCurrentTime(){
		return System.currentTimeMillis();
	}
}
