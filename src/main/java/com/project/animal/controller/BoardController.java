package com.project.animal.controller;

import com.project.animal.ResponseData.BoardResponseData;
import com.project.animal.ResponseData.ErrorMessage;
import com.project.animal.ResponseData.ResponseData;
import com.project.animal.dto.board.*;
import com.project.animal.dto.board.BoardListResponseDTO;
import com.project.animal.dto.chat.ChatRoomDTO;
import com.project.animal.service.BoardService;
import com.project.animal.service.BucketService;
import com.project.animal.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import io.github.bucket4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/board")
@CrossOrigin(origins = "http://localhost:3000") // 3000 포트의 클라이언트 허용
public class BoardController {

    private static final Logger logger = LoggerFactory.getLogger(BoardController.class);

    @Autowired
    private BoardService boardService;

    @Autowired
    private BucketService bucketService;

    private JwtUtil jwtUtil;

    public BoardController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }


    // 게시글 리스트 불러오기
    @GetMapping("/getBoardList")
    @ResponseBody
    public ResponseEntity<BoardResponseData> getBoardList(@RequestParam int page) {
        BoardResponseData responseData = new BoardResponseData();

        try {
            // 유효하지 않은 에러 메세지 일 때
            if (page < 1) {
                responseData.setError(ErrorMessage.INVALID_PAGE_VALUE);
                return ResponseEntity.badRequest().body(responseData);
            }

            int limit = 10; // 한 페이지에 표시할 게시글 수
            int offset = (page - 1) * limit; // 시작 위치 계산
            int totalCount = boardService.getBoardListCount(); // 총 게시글 수 가져오기

            int totalPages = (int) Math.ceil((double) totalCount / limit); // 총 페이지 수 계산

            List<BoardListResponseDTO> list = boardService.getBoardList(limit, offset);

            // 게시글 목록이 있을 때
            if (!list.isEmpty()) {
                responseData.setData(list);
                responseData.setTotalPages(totalPages); // 총 페이지 수 추가
                return ResponseEntity.ok(responseData);
            }

            // 게시글 목록 조회 실패 시
            responseData.setError(ErrorMessage.BOARD_FETCH_FAILED);
            return ResponseEntity.ok(responseData);

        }
        // 서버 에러 발생 시
        catch (Exception e) {
            logger.error("Error : ", e);
            responseData.setError(ErrorMessage.SERVER_ERROR);
            return ResponseEntity.ok(responseData);
        }
    }

    // 게시글 상세 보기
    @GetMapping("/getBoardDetail")
    @ResponseBody
    public ResponseEntity<ResponseData> getBoardDetail(@RequestParam long boardIdx) {

        ResponseData responseData = new ResponseData();

        try {
            BoardDetailResponseDTO boardDetailResponseDTO = boardService.getBoardDetail(boardIdx);

            // 조회한 게시글 내용이 있을 때
            if (boardDetailResponseDTO.getContent() != null) {
                responseData.setData(boardDetailResponseDTO);
                return ResponseEntity.ok(responseData);
            }

            // 해당 데이터가 없을 시
            responseData.setError(ErrorMessage.BOARD_NOT_FOUND);
            return ResponseEntity.ok(responseData);

        }
        // 서버 에러 발생 시
        catch (Exception e) {
            logger.error("Error : ", e);
            responseData.setError(ErrorMessage.SERVER_ERROR);
            return ResponseEntity.ok(responseData);
        }
    }
    
    // 게시글 삭제하기
    @PostMapping("/boardDelete")
    public ResponseEntity<ResponseData> deleteBoard(@RequestBody BoardIndexResponseDTO boardIndexResponseDTO) {
        ResponseData responseData = new ResponseData();

        try {
            Integer deleteResult = boardService.deleteBoard(boardIndexResponseDTO);

            // 게시글 삭제 완료 시
            if (deleteResult >= 1) {
                return ResponseEntity.ok(responseData);
            }

            // 해당 데이터가 없을 시
            responseData.setError(ErrorMessage.BOARD_NOT_FOUND);
            return ResponseEntity.ok(responseData);

        }
        // 서버 에러 발생 시
        catch (Exception e) {
            logger.error("Error : ", e);
            responseData.setError(ErrorMessage.SERVER_ERROR);
            return ResponseEntity.ok(responseData);
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

            // 토큰이 정상적으로 소비되었을 때
            if (bucket.tryConsume(1)) {

                Integer increaseViewResult = boardService.increaseView(boardIdx);

                if (increaseViewResult >= 1) {
                    return ResponseEntity.ok(responseData);
                }

                // 해당 데이터가 없을 시
                responseData.setError(ErrorMessage.BOARD_NOT_FOUND);
                return ResponseEntity.ok(responseData);
            }

            // 토큰이 부족할 때
            responseData.setError(ErrorMessage.TOO_MANY_REQUESTS);
            return ResponseEntity.ok(responseData);

        }
        // 서버 에러 발생 시
        catch (Exception e) {
            logger.error("Error : ", e);
            responseData.setError(ErrorMessage.SERVER_ERROR);
            return ResponseEntity.ok(responseData);
        }
    }

    // 게시글 수정하기
    @PostMapping("/saveBoardEdit")
    public ResponseEntity<ResponseData> saveEditBoard(@RequestBody BoardEditResponseDTO boardEditResponseDTO) {
        ResponseData responseData = new ResponseData();

        try {

            Integer updateResult = boardService.saveEditBoard(boardEditResponseDTO);

            // 게시글 수정 성공 시
            if (updateResult >= 1) {
                return ResponseEntity.ok(responseData);
            }

            // 해당 데이터가 없을 시
            responseData.setError(ErrorMessage.BOARD_NOT_FOUND);
            return ResponseEntity.ok(responseData);

        }
        // 서버 에러 발생 시
        catch (Exception e) {
            logger.error("Error : ", e);
            responseData.setError(ErrorMessage.SERVER_ERROR);
            return ResponseEntity.ok(responseData);
        }
    }

    // 게시글 작성하기
    @PostMapping("/boardWrite")
    public ResponseEntity<ResponseData> writeBoard(@RequestBody BoardWriteResponseDTO boardWriteResponseDTO) {
        ResponseData responseData = new ResponseData();

        try {

            Integer writeResult = boardService.writeBoard(boardWriteResponseDTO);

            // 게시글 작성 성공 시
            if (writeResult >= 1) {
                return ResponseEntity.ok(responseData);
            }

            // 해당 데이터가 없을 시
            responseData.setError(ErrorMessage.BOARD_NOT_FOUND);
            return ResponseEntity.ok(responseData);

        }
        // 서버 에러 발생 시
        catch (Exception e) {
            logger.error("Error : ", e);
            responseData.setError(ErrorMessage.SERVER_ERROR);
            return ResponseEntity.ok(responseData);
        }
    }


    // 댓글, 대댓글 불러오기
    @GetMapping("/getBoardComment")
    public ResponseEntity<ResponseData> getComment(@RequestParam String boardIdx) {
        ResponseData responseData = new ResponseData();

        Long longBoardIdx = Long.parseLong(boardIdx);

        try {
            // 댓글 목록 가져오기
            List<BoardCommentDTO> commentList = boardService.getBoardComment(longBoardIdx);

            // 조회된 댓글이 없을 때
            if (commentList.isEmpty()) {
                responseData.setError(ErrorMessage.BOARD_NOT_FOUND);
                return ResponseEntity.ok(responseData);
            }

            // 댓글이 있을때
            responseData.setData(commentList);
            return ResponseEntity.ok(responseData);

        }
        // 서버 에러 발생 시
        catch (Exception e) {
            logger.error("Error : ", e);
            responseData.setError(ErrorMessage.SERVER_ERROR);
            return ResponseEntity.ok(responseData);
        }
    }

    // 댓글 작성하기
    @PostMapping("/writeBoardComment")
    public ResponseEntity<ResponseData> writeBoardComment(@RequestBody BoardWriteCommentDTO boardWriteCommentDTO) {
        ResponseData responseData = new ResponseData();

        // 토큰 값 추출
        String token = boardWriteCommentDTO.getAuthorToken();
        token = token.replace("Bearer ", "");

        // 토큰 검증
        if (jwtUtil.validateToken(token)) {
            Long userIdx = jwtUtil.getIdFromToken(token);
            boardWriteCommentDTO.setAuthorIdx(userIdx);
            boardWriteCommentDTO.setAuthorToken(null);
            
        } else {
            throw new RuntimeException("유효하지 않은 토큰 값입니다!");
        }

        try {
            // 댓글 작성
            Integer writeResult = boardService.writeBoardComment(boardWriteCommentDTO);

            // 업데이트 된게 없을 때
            if (writeResult < 1) {
                responseData.setError(ErrorMessage.BOARD_NOT_FOUND);
                return ResponseEntity.ok(responseData);
            }

            // 댓글 작성 성공했을 때
            responseData.setData(writeResult);
            return ResponseEntity.ok(responseData);

        }
        // 서버 에러 발생 시
        catch (Exception e) {
            logger.error("Error : ", e);
            responseData.setError(ErrorMessage.SERVER_ERROR);
            return ResponseEntity.ok(responseData);
        }
    }

    // 대댓글 작성하기
    @PostMapping("/writeBoardReply")
    public ResponseEntity<ResponseData> writeBoardComment(@RequestBody BoardReplyReqDTO boardReplyDTO) {
        ResponseData responseData = new ResponseData();

        System.out.println(boardReplyDTO);
        try {
            // 대댓글 작성하기
            Integer writeResult = boardService.writeBoardReply(boardReplyDTO);

            System.out.println(writeResult);

            // 업데이트 된게 없을 때
            if (writeResult < 1) {
                responseData.setError(ErrorMessage.BOARD_NOT_FOUND);
                return ResponseEntity.ok(responseData);
            }

            // 댓글 작성 성공했을 때
            responseData.setData(writeResult);
            return ResponseEntity.ok(responseData);

        }
        // 서버 에러 발생 시
        catch (Exception e) {
            logger.error("Error : ", e);
            responseData.setError(ErrorMessage.SERVER_ERROR);
            return ResponseEntity.ok(responseData);
        }
    }

}
