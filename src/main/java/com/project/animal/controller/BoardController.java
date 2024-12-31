package com.project.animal.controller;

import com.project.animal.ResponseData.BoardResponseData;
import com.project.animal.ResponseData.ErrorMessage;
import com.project.animal.ResponseData.ResponseData;
import com.project.animal.dto.board.*;
import com.project.animal.dto.board.BoardListResponseDTO;
import com.project.animal.service.BoardService;
import com.project.animal.service.BucketService;
import lombok.RequiredArgsConstructor;
import io.github.bucket4j.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/api/board")
@CrossOrigin(origins = "http://localhost:3000") // 3000 포트의 클라이언트 허용
public class BoardController {

    @Autowired
    private BoardService boardService;

    @Autowired
    private BucketService bucketService;

    // 게시글 리스트 불러오기
    @GetMapping("/getBoardList")
    @ResponseBody
    public ResponseEntity<BoardResponseData> getBoardList(@RequestParam int page) {
        BoardResponseData responseData = new BoardResponseData();

        try {
            // 1. 페이지 번호 검증
            if (page < 1) {
                responseData.setCode("400");
                responseData.setMsg("유효하지 않은 페이지 번호입니다.");
                return ResponseEntity.badRequest().body(responseData);
            }

            int limit = 10; // 한 페이지에 표시할 게시글 수
            int offset = (page - 1) * limit; // 시작 위치 계산
            int totalCount = boardService.getBoardListCount(); // 총 게시글 수 가져오기


            int totalPages = (int) Math.ceil((double) totalCount / limit); // 총 페이지 수 계산

            List<BoardListResponseDTO> list = boardService.getBoardList(limit, offset);

            if (!list.isEmpty()) {
                responseData.setData(list);
                responseData.setTotalPages(totalPages); // 총 페이지 수 추가
                return ResponseEntity.ok(responseData);
            }

            responseData.setError(ErrorMessage.BOARD_SIZE_ZERO);
            responseData.setCode("204");
            responseData.setMsg("조회된 데이터가 없습니다.");
            responseData.setData(new ArrayList<>()); // 빈 리스트 반환
            responseData.setTotalPages(totalPages);
            return ResponseEntity.status(204).body(responseData);

        } catch (Exception e) {
            e.printStackTrace();
            responseData.setError(ErrorMessage.ALL_ERROR);
            responseData.setData(null);
            return ResponseEntity.status(500).body(responseData);
        }
    }

    // 게시글 상세 보기
    @GetMapping("/getBoardDetail")
    @ResponseBody
    public ResponseEntity<ResponseData> getBoardDetail(@RequestParam long boardIdx) {

        ResponseData responseData = new ResponseData();

        try {
            BoardDetailResponseDTO boardDetailResponseDTO = boardService.getBoardDetail(boardIdx);

            System.out.println(boardDetailResponseDTO);
            if (boardDetailResponseDTO.getContent() != null) {
                responseData.setData(boardDetailResponseDTO);
                return ResponseEntity.ok(responseData);
            }

            responseData.setCode("204");
            responseData.setMsg("조회된 데이터가 없습니다.");
            responseData.setData(null);
            return ResponseEntity.status(204).body(responseData);

        } catch (Exception e) {
            e.printStackTrace();
            responseData.setCode("500");
            responseData.setMsg("서버 내부 오류가 발생했습니다.");
            responseData.setData(null);
            return ResponseEntity.status(500).body(responseData);
        }
    }
    
    // 게시글 삭제하기
    @PostMapping("/boardDelete")
    public ResponseEntity<ResponseData> deleteBoard(@RequestBody BoardIndexResponseDTO boardIndexResponseDTO) {
        ResponseData responseData = new ResponseData();

        try {
            Integer deleteResult = boardService.deleteBoard(boardIndexResponseDTO);

            if (deleteResult >= 1) {
                return ResponseEntity.ok(responseData);
            }

            responseData.setCode("204");
            responseData.setMsg("삭제할 데이터가 없습니다");
            responseData.setData(null);
            return ResponseEntity.status(204).body(responseData);

        } catch (Exception e) {
            e.printStackTrace();
            responseData.setCode("500");
            responseData.setMsg("서버 내부 오류가 발생했습니다.");
            responseData.setData(null);
            return ResponseEntity.status(500).body(responseData);
        }
    }

    // 게시글 조회수 올리기
    @PostMapping("/increaseView")
    public ResponseEntity<ResponseData> increaseView(@RequestBody HashMap<String, Object> requestData, HttpServletRequest request) {

        ResponseData responseData = new ResponseData();
        Long boardIdx = Long.parseLong(requestData.get("boardIdx").toString());

        try {

            String userKey = request.getRemoteAddr(); // 사용자별 고유 키
            Bucket bucket = bucketService.getBucketForUser(userKey);

            if (!bucket.tryConsume(1)) {

                responseData.setError(ErrorMessage.TOO_MUCH_ACCESS);
                return ResponseEntity.ok(responseData);
            }

            Integer increaseViewResult = boardService.increaseView(boardIdx);

            if (increaseViewResult >= 1) {
                return ResponseEntity.ok(responseData);
            }

            responseData.setCode("204");
            responseData.setMsg("삭제할 데이터가 없습니다");
            responseData.setData(null);
            return ResponseEntity.status(204).body(responseData);

        } catch (Exception e) {
            e.printStackTrace();
            responseData.setCode("500");
            responseData.setMsg("서버 내부 오류가 발생했습니다.");
            responseData.setData(null);
            return ResponseEntity.status(500).body(responseData);
        }
    }

    // 게시글 수정하기
    @PostMapping("/saveBoardEdit")
    public ResponseEntity<ResponseData> saveEditBoard(@RequestBody BoardEditResponseDTO boardEditResponseDTO) {
        ResponseData responseData = new ResponseData();

        try {

            Integer updateResult = boardService.saveEditBoard(boardEditResponseDTO);

            if (updateResult >= 1) {
                return ResponseEntity.ok(responseData);
            }

            responseData.setCode("204");
            responseData.setMsg("게시글 수정 오류");
            responseData.setData(null);
            return ResponseEntity.status(204).body(responseData);

        } catch (Exception e) {
            e.printStackTrace();
            responseData.setCode("500");
            responseData.setMsg("서버 내부 오류가 발생했습니다.");
            responseData.setData(null);
            return ResponseEntity.status(500).body(responseData);
        }
    }

    // 게시글 작성하기
    @PostMapping("/boardWrite")
    public ResponseEntity<ResponseData> writeBoard(@RequestBody BoardWriteResponseDTO boardWriteResponseDTO) {
        ResponseData responseData = new ResponseData();

        System.out.println(boardWriteResponseDTO);
        try {

            Integer writeResult = boardService.writeBoard(boardWriteResponseDTO);

            if (writeResult >= 1) {
                return ResponseEntity.ok(responseData);
            }

            responseData.setCode("204");
            responseData.setMsg("게시글 수정 오류");
            responseData.setData(null);
            return ResponseEntity.status(204).body(responseData);

        } catch (Exception e) {
            e.printStackTrace();
            responseData.setCode("500");
            responseData.setMsg("서버 내부 오류가 발생했습니다.");
            responseData.setData(null);
            return ResponseEntity.status(500).body(responseData);
        }
    }
}
