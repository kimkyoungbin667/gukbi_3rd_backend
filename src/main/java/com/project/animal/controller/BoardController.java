package com.project.animal.controller;

import com.project.animal.ResponseData.BoardResponseData;
import com.project.animal.ResponseData.ResponseData;
import com.project.animal.dto.board.BoardListResponseDTO;
import com.project.animal.dto.board.BoardListResponseDTO;
import com.project.animal.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/api/board")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000") // 3000 포트의 클라이언트 허용
public class BoardController {
    private final BoardService boardService;

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

            // 2. 데이터 가져오기
            List<BoardListResponseDTO> list = boardService.getBoardList(limit, offset);

            // 3. 리스트가 있을 때
            if (!list.isEmpty()) {
                responseData.setCode("200");
                responseData.setMsg("성공");
                responseData.setData(list);
                responseData.setTotalPages(totalPages); // 총 페이지 수 추가
                return ResponseEntity.ok(responseData);
            }

            // 4. 리스트가 없을 때
            responseData.setCode("204");
            responseData.setMsg("조회된 데이터가 없습니다.");
            responseData.setData(new ArrayList<>()); // 빈 리스트 반환
            responseData.setTotalPages(totalPages);
            return ResponseEntity.status(204).body(responseData);

        } catch (Exception e) {
            // 5. 에러 발생 시
            e.printStackTrace();
            responseData.setCode("500");
            responseData.setMsg("서버 내부 오류가 발생했습니다.");
            responseData.setData(null);
            return ResponseEntity.status(500).body(responseData);
        }
    }
}
