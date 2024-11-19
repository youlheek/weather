package zerobase.weather.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import zerobase.weather.domain.Diary;
import zerobase.weather.service.DiaryService;

import java.time.LocalDate;
import java.util.List;

@RestController
// RestController는 HTTP 응답의 상태코드 (200, 404 등)를 지정해서 내려줄 수 있음
public class DiaryController {
    private final DiaryService diaryService;

    public DiaryController(DiaryService diaryService) {
        this.diaryService = diaryService;
    }

    @Operation(summary = "일기 텍스트와 날씨를 이용해서 DB에 일기를 저장합니다", description = "이게 노트인가")
    @PostMapping("/create/diary") // 보통 조회할 때는 get, 저장할 때는 post를 씀
    void createDiary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestBody String text) {

        diaryService.createDiary(date, text);

    }

    // # 01 날씨 일기 조회 API 구현
    @Operation(summary = "선택한 날짜의 모든 일기를 가져옵니다")
    @GetMapping("/read/diary")
    List<Diary> readDiary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(description = "날짜 형식 : YYYY-MM-DD", example = "2020-01-01") LocalDate date) {

        return diaryService.readDiary(date);
    }

    @Operation(summary = "선택한 기간의 모든 일기를 가져옵니다")
    @GetMapping("/read/diaries")
    List<Diary> readDiaries(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(description = "조회할 기간의 첫번째 날 입니다") LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @Parameter(description = "조회할 기간의 마지막 날 입니다") LocalDate endDate) {

        return diaryService.readDiaries(startDate, endDate);
    }

    // # 02 날씨 일기 수정 API 구현

    @PutMapping("/update/diary")
    void updateDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                     @RequestBody String text) {

       diaryService.updateDiary(date, text);
    }

    // # 03 날씨 일기 삭제 API 구현
    @DeleteMapping("/delete/diary")
    void deleteDiary(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        diaryService.deleteDiary(date);
    }
}
