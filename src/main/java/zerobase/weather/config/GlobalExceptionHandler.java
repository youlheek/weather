package zerobase.weather.config;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
/*
 * ControllerAdvice는 전역에 예외처리를 하고
 * ExceptionHandler는 컨트롤러 안의 예외처리만 함
 * 그래서 전역에 예외처리를 할 때는 @RestControllerAdvice를 붙인 클래스를 만들어서
 * 예외가 모이도록 설정을 한다
 * 그래서 그 모인 예외들을 @ExceptionHandler가 처리하게 됨
 */

	// 에러가 발생한 시점이 클라이언트가 서버 API를 호출한 시점이라면? @Response~로 해결
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	// 모든 Exception에 대해서 동작
	@ExceptionHandler(Exception.class)
	public Exception handleAllExceptions() {
		System.out.println("error from GlobalExceptionHandler");
		return new Exception();
	}
}
