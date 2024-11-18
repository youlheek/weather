package zerobase.weather.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;

@Service
public class DiaryService {
    // 1. open weather map 에서 데이터 받아오기
    // 2. 받아온 날씨 데이터 파싱하기
    // 3. 우리 DB에 저장하기


    @Value("${openweathermap.key}") // application.properties
    private String apiKey;
    // 여기에 key 값을 직접 넣어줄 수도 있는데 왜 properties에 넣어야 하느냐?
    // real, test, local 세 가지의 환경이 다 같은 DB를 바라보지 않기 때문에
    // 실제 DB에 반영이 되면 안됨! -> 환경마다 properties를 다르게 쓴다


    public void createDiary(LocalDate date, String text) {
        // 1. open weather map 에서 데이터 받아오기
        String weatherData = getWeatherString();

        // 2. 받아온 날씨 데이터 파싱하기

        // 3. 우리 DB에 저장하기
    }

    // open weather map 에서 날씨정보를 가져와서 string 으로 변환하려고
    private String getWeatherString() {

        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apiKey;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection =
                    (HttpURLConnection) url.openConnection();
            // url 객체를 사용해 네트워크 연결을 열었다 -> URLConnection이라는 추상 클래스 타입 반환
            // URL 을 HTTP URL 형태로 형변환(캐스팅)
            // HttpURLConnection : HTTP 프로토콜을 통해 특정 URL에 대해 요청을 보내고 응답을 받을 수 있도록 해주는 클래스

            connection.setRequestMethod("GET"); // GET 요청을 보냄
            int responseCode = connection.getResponseCode(); // 응답 코드를 받음

            // 📍왜 BufferedReader를 썼는지 알아보자
            BufferedReader br;
            if(responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                // 응답 객체 받기
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                // 오류 객체 받기
            }

            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null) {
                // bufferedReader 에 넣어둔 것들을 하나하나 읽어들이면서

                response.append(inputLine);
                // response에 결과값들을 쌓음
            }
            br.close();

            return response.toString();
        } catch (Exception e) {
            return "failed to get response";
        }
    }
}