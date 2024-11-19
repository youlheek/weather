package zerobase.weather.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.domain.DateWeather;
import zerobase.weather.domain.Diary;
import zerobase.weather.repository.DateWeatherRepository;
import zerobase.weather.repository.DiaryRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DiaryService {

    @Value("${openweathermap.key}") // application.properties
    private String apiKey;
    // 여기에 key 값을 직접 넣어줄 수도 있는데 왜 properties에 넣어야 하느냐?
    // real, test, local 세 가지의 환경이 다 같은 DB를 바라보지 않기 때문에
    // 실제 DB에 반영이 되면 안됨! -> 환경마다 properties를 다르게 쓴다

    private final DiaryRepository diaryRepository;
    private final DateWeatherRepository dateWeatherRepository;

    public DiaryService(DiaryRepository diaryRepository, DateWeatherRepository dateWeatherRepository) {
        this.diaryRepository = diaryRepository;
        this.dateWeatherRepository = dateWeatherRepository;
    }

    // 매일 새벽 1시에 날씨 데이터를 저장
    @Transactional
    @Scheduled(cron = "0/5 * * * * * ")
    public void saveWeatherDate() {
        dateWeatherRepository.save(getWeatherFromApi());
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createDiary(LocalDate date, String text) {
        // 1. open weather map 에서 데이터 받아오기
        // 2. 받아온 날씨 데이터 파싱하기
        // 1, 2는 saveWeatherDate() 함수에서 저장한 데이터가 있으므로 불필요 -> 삭제

        // 대신, 날씨 데이터 DB에서 가져오기
        DateWeather dateWeather = getDateWeather(date);

        // 3. 우리 DB에 저장하기
        // Diary 객체를 DiaryRepository 를 통해서 DB에 넣을 것임!
        Diary nowDiary = new Diary();

//        nowDiary.setWeather(dateWeather.get("main").toString());
//        nowDiary.setIcon(dateWeather.get("icon").toString());
//        nowDiary.setTemperature((Double) dateWeather.get("temp"));
        // 일일이 set 대신 setDateWeather함수를 사용하자

        nowDiary.setDateWeather(dateWeather);
        nowDiary.setText(text);
        diaryRepository.save(nowDiary);
    }

    // # 01 날씨 일기 조회 API 구현
    @Transactional(readOnly = true)
    public List<Diary> readDiary(LocalDate date) {
        return diaryRepository.findAllByDate(date);
    }

    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate, endDate);
    }

    // # 02 날씨 일기 수정 API 구현
    // 해당 날짜의 일기가 여러개 있을 경우 가장 첫번째 것을 수정
    public void updateDiary(LocalDate date, String text) {
        Diary nowDiary = diaryRepository.getFirstByDate(date);
        nowDiary.setText(text);
        diaryRepository.save(nowDiary);
    }

    // # 03 날씨 일기 삭제 API 구현
    public void deleteDiary(LocalDate date) {
        diaryRepository.deleteAllByDate(date);
    }


    // api를 통해 과거 데이터 가져오기
    private DateWeather getWeatherFromApi() {
        // 1. open weather map 에서 데이터 받아오기
        String weatherData = getWeatherString();

        // 2. 받아온 날씨 데이터 파싱하기
        Map<String, Object> parseWeather = parseWeather(weatherData);

        // 3. 파싱된 데이터 DateWeather 객체에 넣기
        DateWeather dateWeather = new DateWeather();
        dateWeather.setDate(LocalDate.now());
        dateWeather.setWeather(parseWeather.get("main").toString());
        dateWeather.setIcon(parseWeather.get("icon").toString());
        dateWeather.setTemperature((Double) parseWeather.get("temp"));

        return dateWeather;
    }

    // DB를 통해 날씨 정보 가져오기
    private DateWeather getDateWeather(LocalDate date) {
        List<DateWeather> dateWeatherListFromDB = dateWeatherRepository.findAllByDate(date);
        if (dateWeatherListFromDB.size() > 0) {
            return dateWeatherListFromDB.get(0);
        } else {
            // 새로 API에서 날씨 정보를 가져와야 한다
            return getWeatherFromApi();
        }
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

    // JSON 데이터 파싱하기
    private Map<String, Object> parseWeather (String jsonString) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Map<String, Object> resultMap = new HashMap<>();


//        JSONObject weatherData = (JSONObject) jsonObject.get("weather");
        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        JSONObject weatherData = (JSONObject) weatherArray.get(0);
        resultMap.put("main", weatherData.get("main"));
        resultMap.put("icon", weatherData.get("icon"));

        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp", mainData.get("temp"));

        return resultMap;
    }
}
