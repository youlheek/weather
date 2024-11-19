package zerobase.weather.error;

public class InvalidDate extends RuntimeException{
	private static final String MESSAGE = "너무 과거 혹은 미래의 날짜입니다";

	public InvalidDate() {
		super(MESSAGE);
		// 부모 클래스 생성자를 호출해서 에러 메시지를 전달
	}
}
