package ufc.quixada.npi.contest.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController implements org.springframework.boot.autoconfigure.web.ErrorController {
	
	private final static String ERROR_PATH = "/error";
	
	@Override
	public String getErrorPath() {
		return ERROR_PATH;
	}
	
	@RequestMapping(value = ERROR_PATH)
    public String errorHtml(HttpServletRequest request) {
		
		switch (getStatus(request)) {
			case NOT_FOUND:
				return "/error/404";
			case FORBIDDEN:
				return "/error/403";
			case INTERNAL_SERVER_ERROR:
				return "/error/500";
			default:
				return "/error/erro";
		}
    }
	public HttpStatus getStatus(HttpServletRequest request) {
		Integer statusCode = (Integer) request
				.getAttribute("javax.servlet.error.status_code");
		if (statusCode == null) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
		try {
			return HttpStatus.valueOf(statusCode);
		}
		catch (Exception ex) {
			return HttpStatus.INTERNAL_SERVER_ERROR;
		}
	}
}
