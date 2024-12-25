package com.project.animal.controller;

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
    public ResponseEntity<ResponseData> getBoardList() {

        List<BoardListResponseDTO> list = new ArrayList<>();
        ResponseData responseData = new ResponseData();

        try {
            list = boardService.getBoardList();

            // 리스트가 있을 때
            if (!list.isEmpty()) {
                System.out.println(list);
                responseData.setData(list);
                return ResponseEntity.ok(responseData);
            }

            // 리스트가 없을때
            responseData.setCode("204");
            responseData.setMsg("조회된 데이터가 없습니다.");
            responseData.setData(list); // 빈 리스트
            return ResponseEntity.status(204).body(responseData);

        } catch (Exception e) {
            // 3. 에러 발생했을 때
            responseData.setCode("500");
            responseData.setMsg("서버 내부 오류가 발생했습니다.");
            responseData.setData(null);
            return ResponseEntity.status(500).body(responseData);
        }


    }

}
