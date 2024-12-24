package com.project.animal.controller;

import com.project.animal.ResponseData.ResponseData;
import com.project.animal.dto.BoardDTO;
import com.project.animal.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/board")
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/findAll")
    @ResponseBody // 반환 데이터를 JSON으로 직렬화
    public List<BoardDTO> findAll(@RequestParam(value = "keyword", required = false, defaultValue = "") String param,
                                  @RequestParam(value = "setting", required = false, defaultValue = "") String param2) {
        System.out.println(param);
        System.out.println(param2);

        List<BoardDTO> list = new ArrayList<>();

        //만약 값이 있으면
        if(param != "") {
            if(param2.equals("title")) {
                list = boardService.findTitle(param);
                return list;
            } else if (param2.equals("writer")) {
                list = boardService.findWriter(param);
                return list;
            }
        }
            list = boardService.findAll();
        return list;
    }

    @GetMapping("/detailBoard")
    @ResponseBody
    public ResponseEntity<ResponseData> detailBoard(int board_idx) {

        ResponseData responseData = new ResponseData();
        BoardDTO detailBoard = boardService.detailBoard(board_idx);
        responseData.setData(detailBoard);

        return ResponseEntity.ok(responseData);
    }

    @PostMapping("/writeBoard")
    @ResponseBody
    public ResponseEntity<ResponseData> writeBoard(@RequestBody BoardDTO boardDTO) {
        ResponseData responseData = new ResponseData();

        int writeBoard = boardService.writeBoard(boardDTO);
        responseData.setData(writeBoard);

        if (writeBoard >= 1) {
            return ResponseEntity.ok(responseData);
        } else {
            responseData.setCode("500");
            responseData.setMsg("게시글 추가 실패");
            return ResponseEntity.ok(responseData);
        }
    }

    @PostMapping("/deleteBoard")
    @ResponseBody
    public ResponseEntity<ResponseData> deleteBoard(@RequestBody Map<String, Object> requestData) {
        ResponseData responseData = new ResponseData();

        int board_idx = Integer.parseInt(requestData.get("board_idx").toString());
        int deleteBoard = boardService.deleteBoard(board_idx);

        if (deleteBoard >= 1) {
            return ResponseEntity.ok(responseData);
        } else {
            responseData.setCode("500");
            responseData.setMsg("게시글 삭제 실패");
            return ResponseEntity.ok(responseData);
        }
    }

    @PostMapping("/modifyBoard")
    @ResponseBody
    public ResponseEntity<ResponseData> reviseBoard(@RequestBody BoardDTO boardDTO) {
        ResponseData responseData = new ResponseData();

        System.out.println(boardDTO);
        int reviseBoard = boardService.reviseBoard(boardDTO);

        if (reviseBoard >= 1) {
            return ResponseEntity.ok(responseData);
        } else {
            responseData.setCode("500");
            responseData.setMsg("게시글 수정 실패");
            return ResponseEntity.ok(responseData);
        }
    }

    @PostMapping("/goodBoard")
    @ResponseBody
    public ResponseEntity<ResponseData> goodBoard(@RequestBody Map<String, Object> requestData) {
        ResponseData responseData = new ResponseData();

        int boardId = Integer.parseInt(requestData.get("boardId").toString());
        System.out.println(boardId);

        int goodBoard = boardService.goodBoard(boardId);

        if (goodBoard >= 1) {
            return ResponseEntity.ok(responseData);
        } else {
            responseData.setCode("500");
            responseData.setMsg("좋아요 수정 실패");
            return ResponseEntity.ok(responseData);
        }
    }





}
