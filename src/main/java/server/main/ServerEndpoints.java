package server.main;

import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import MessagesBase.ResponseEnvelope;
import server.exceptions.GenericException;

@RestController
@RequestMapping(value = "/games")
public class ServerEndpoints {

	@ExceptionHandler({ GenericException.class })
	public @ResponseBody ResponseEnvelope<?> handleException(GenericException ex, HttpServletResponse response) {
		ResponseEnvelope<?> result = new ResponseEnvelope<>(ex.getErrorName(), ex.getMessage());
		response.setStatus(HttpServletResponse.SC_OK);
		return result;
	}
}
