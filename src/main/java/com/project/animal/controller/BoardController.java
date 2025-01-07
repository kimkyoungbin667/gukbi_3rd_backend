package com.project.animal.controller;

import com.project.animal.ResponseData.BoardResponseData;
import com.project.animal.ResponseData.ErrorMessage;
import com.project.animal.ResponseData.ResponseData;
import com.project.animal.dto.board.*;
import com.project.animal.dto.board.BoardPostListResDTO;
import com.project.animal.service.BoardService;
import com.project.animal.service.BucketService;
import com.project.animal.util.JwtUtil;
import io.github.bucket4j.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/api/board")
@CrossOrigin(origins = "http://localhost:3000") // 3000 포트의 클라이언트 허용
public class BoardController {

    private static final Logger logger = LoggerFactory.getLogger(BoardController.class);

    @Autowired
    private BoardService boardService;

    @Autowired
    private BucketService bucketService;

    @Autowired
    private JwtUtil jwtUtil;

    // 게시글 리스트 전체 불러오기
    @GetMapping("/getBoardList")
    @ResponseBody
    public ResponseEntity<BoardResponseData> getBoardList(@RequestParam int page, @RequestHeader("Authorization") String token) {
        BoardResponseData responseData = new BoardResponseData();

        try {
            String actualToken = token.replace("Bearer ", "");
            System.out.println("Received token: " + actualToken);

            if (!jwtUtil.validateToken(actualToken)) {
                System.err.println("권한 검증 실패");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            // 유효하지 않은 에러 메세지 일 때
            if (page < 1) {
                responseData.setError(ErrorMessage.INVALID_PAGE_VALUE);
                return ResponseEntity.badRequest().body(responseData);
            }

            int limit = 10; // 한 페이지에 표시할 게시글 수
            int offset = (page - 1) * limit; // 시작 위치 계산
            int totalCount = boardService.getBoardListCount(); // 총 게시글 수 가져오기

            int totalPages = (int) Math.ceil((double) totalCount / limit); // 총 페이지 수 계산

            List<BoardPostListResDTO> list = boardService.getBoardList(limit, offset);

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

    // 게시글 작성하기
    @PostMapping("/createBoardPost")
    public ResponseEntity<ResponseData> createBoardPost(@RequestBody BoardPostCreateReqDTO boardPostCreateReqDTO) {
        ResponseData responseData = new ResponseData();

        try {
            Long userIdx = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            boardPostCreateReqDTO.setAuthorIdx(userIdx);

            Integer writeResult = boardService.createBoardPost(boardPostCreateReqDTO);

            // 게시글 작성 성공 시
            if (writeResult >= 1) {
                return ResponseEntity.ok(responseData);
            }

            // 해당 데이터가 없을 시
            responseData.setError(ErrorMessage.BOARD_NOT_FOUND);
            return ResponseEntity.ok(responseData);

        }
        // 토큰 검증 실패
        catch (ClassCastException e) {
            System.err.println("권한 검증 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
        }
        // 서버 에러 발생 시
        catch (Exception e) {
            logger.error("Error : ", e);
            responseData.setError(ErrorMessage.SERVER_ERROR);
            return ResponseEntity.ok(responseData);
        }
    }

    // 게시글 상세 보기
    @PostMapping("/readBoardPost")
    @ResponseBody
    public ResponseEntity<ResponseData> readBoardPost(@RequestBody BoardPostReadReqDTO boardPostReadReqDTO) {
        ResponseData responseData = new ResponseData();

        try {
            Long userIdx = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            boardPostReadReqDTO.setUserIdx(userIdx);

            System.out.println(boardPostReadReqDTO);
            BoardPostReadResDTO boardPostReadResDTO = boardService.readBoardPost(boardPostReadReqDTO);

            // 조회한 게시글 내용이 있을 때
            if (boardPostReadResDTO.getContent() != null) {
                responseData.setData(boardPostReadResDTO);
                return ResponseEntity.ok(responseData);
            }

            // 해당 데이터가 없을 시
            responseData.setError(ErrorMessage.BOARD_NOT_FOUND);
            return ResponseEntity.ok(responseData);

        }
        // 토큰 검증 실패
        catch (ClassCastException e) {
            System.err.println("권한 검증 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
        }
        // 서버 에러 발생 시
        catch (Exception e) {
            logger.error("Error : ", e);
            responseData.setError(ErrorMessage.SERVER_ERROR);
            return ResponseEntity.ok(responseData);
        }
    }

    // 게시글 수정하기
    @PostMapping("/updateBoardPost")
    public ResponseEntity<ResponseData> updateBoardPost(@RequestBody BoardPostUpdateReqDTO boardPostUpdateReqDTO) {
        ResponseData responseData = new ResponseData();

        System.out.println(boardPostUpdateReqDTO);
        try {
            Long userIdx = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            boardPostUpdateReqDTO.setAuthorIdx(userIdx);
            Integer updateResult = boardService.updateBoardPost(boardPostUpdateReqDTO);

            // 게시글 수정 성공 시
            if (updateResult >= 1) {
                return ResponseEntity.ok(responseData);
            }

            // 해당 데이터가 없을 시
            responseData.setError(ErrorMessage.BOARD_NOT_FOUND);
            return ResponseEntity.ok(responseData);

        }
        // 토큰 검증 실패
        catch (ClassCastException e) {
            System.err.println("권한 검증 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
        }
        // 서버 에러 발생 시
        catch (Exception e) {
            logger.error("Error : ", e);
            responseData.setError(ErrorMessage.SERVER_ERROR);
            return ResponseEntity.ok(responseData);
        }
    }
    
    // 게시글 삭제하기
    @PostMapping("/deleteBoardPost")
    public ResponseEntity<ResponseData> deleteBoardPost(@RequestBody BoardPostDeleteReqDTO boardPostDeleteReqDTO,
                                                        @RequestHeader("Authorization") String token) {
        ResponseData responseData = new ResponseData();

        try {

            String actualToken = token.replace("Bearer ", "");

            if (!jwtUtil.validateToken(actualToken)) {
                System.err.println("권한 검증 실패");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            Integer deleteResult = boardService.deleteBoardPost(boardPostDeleteReqDTO);

            // 게시글 삭제 완료 시
            if (deleteResult >= 1) {
                return ResponseEntity.ok(responseData);
            }

            // 해당 데이터가 없을 시
            responseData.setError(ErrorMessage.BOARD_NOT_FOUND);
            return ResponseEntity.ok(responseData);

        }
        // 토큰 검증 실패
        catch (ClassCastException e) {
            System.err.println("권한 검증 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
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
    public ResponseEntity<ResponseData> increaseView(@RequestBody HashMap<String, Object> requestData, HttpServletRequest request,
                                                     @RequestHeader("Authorization") String token) {

        ResponseData responseData = new ResponseData();
        Long boardIdx = Long.parseLong(requestData.get("boardIdx").toString());

        try {

            String actualToken = token.replace("Bearer ", "");

            if (!jwtUtil.validateToken(actualToken)) {
                System.err.println("권한 검증 실패");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

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
        // 토큰 검증 실패
        catch (ClassCastException e) {
            System.err.println("권한 검증 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
        }
        // 서버 에러 발생 시
        catch (Exception e) {
            logger.error("Error : ", e);
            responseData.setError(ErrorMessage.SERVER_ERROR);
            return ResponseEntity.ok(responseData);
        }
    }


    // 댓글, 대댓글 불러오기
    @GetMapping("/readBoardComments")
    public ResponseEntity<ResponseData> readBoardComments(@RequestParam String boardIdx,
                                                   @RequestHeader("Authorization") String token) {
        ResponseData responseData = new ResponseData();
        try {
            String actualToken = token.replace("Bearer ", "");

            if (!jwtUtil.validateToken(actualToken)) {
                System.err.println("권한 검증 실패");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            Long longBoardIdx = Long.parseLong(boardIdx);
            System.out.println(longBoardIdx);

            // 댓글 목록 가져오기
            List<BoardPostReadCommentsResDTO> commentList = boardService.readBoardComments(longBoardIdx);

            // 조회된 댓글이 없을 때
            if (commentList.isEmpty()) {
                responseData.setError(ErrorMessage.BOARD_NOT_FOUND);
                return ResponseEntity.ok(responseData);
            }

            // 댓글이 있을때
            responseData.setData(commentList);
            return ResponseEntity.ok(responseData);

        }
        // 토큰 검증 실패
        catch (ClassCastException e) {
            System.err.println("권한 검증 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
        }
        // 서버 에러 발생 시
        catch (Exception e) {
            logger.error("Error : ", e);
            responseData.setError(ErrorMessage.SERVER_ERROR);
            return ResponseEntity.ok(responseData);
        }
    }

    // 댓글 작성하기
    @PostMapping("/createBoardComment")
    public ResponseEntity<ResponseData> createBoardComment(@RequestBody BoardPostCreateCommentReqDTO boardPostCreateCommentReqDTO) {
        ResponseData responseData = new ResponseData();

        System.out.println(boardPostCreateCommentReqDTO);
            try {
                Long userIdx = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

                // 댓글 작성
                Integer writeResult = boardService.createBoardComment(boardPostCreateCommentReqDTO);

                // 업데이트 된게 없을 때
                if (writeResult < 1) {
                    responseData.setError(ErrorMessage.BOARD_NOT_FOUND);
                    return ResponseEntity.ok(responseData);
                }

                // 댓글 작성 성공했을 때
                responseData.setData(writeResult);
                return ResponseEntity.ok(responseData);

            }
            // 토큰 검증 실패
            catch (ClassCastException e) {
                System.err.println("권한 검증 실패: " + e.getMessage());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
            }
            // 서버 에러 발생 시
            catch (Exception e) {
                logger.error("Error : ", e);
                responseData.setError(ErrorMessage.SERVER_ERROR);
                return ResponseEntity.ok(responseData);
            }
            
        }

    // 대댓글 작성하기
    @PostMapping("/createBoardReply")
    public ResponseEntity<ResponseData> createBoardReply(@RequestBody BoardPostCreateReplyReqDTO boardPostCreateReplyReqDTO) {
        ResponseData responseData = new ResponseData();

        try {
            Long userIdx = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            Integer writeResult = boardService.createBoardReply(boardPostCreateReplyReqDTO);

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
        // 토큰 검증 실패
        catch (ClassCastException e) {
            System.err.println("권한 검증 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
        }
        // 서버 에러 발생 시
        catch (Exception e) {
            logger.error("Error : ", e);
            responseData.setError(ErrorMessage.SERVER_ERROR);
            return ResponseEntity.ok(responseData);
        }
    }

    // 게시글 좋아요 +1 올리기
    @PostMapping("/upBoardPostLike")
    public ResponseEntity<ResponseData> upBoardPostLike(@RequestBody BoardPostUpLikeReqDTO boardPostUpLikeReqDTO) {
        ResponseData responseData = new ResponseData();

        try {
            Long userIdx = (Long) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            boardPostUpLikeReqDTO.setUserIdx(userIdx);

            Boolean AlreadyLiked = boardService.isLikedPost(boardPostUpLikeReqDTO);
            System.out.println(AlreadyLiked);
            // 좋아요를 누른지 판단
            if(AlreadyLiked) {
                // 좋아요가 있다면
                return ResponseEntity.ok(responseData);
            } else {
                // 없다면 좋아요 +1
                Integer upResult = boardService.upBoardPostLike(boardPostUpLikeReqDTO);
            }


            return ResponseEntity.ok(responseData);

        }
        // 토큰 검증 실패
        catch (ClassCastException e) {
            System.err.println("권한 검증 실패: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseData);
        }
        // 서버 에러 발생 시
        catch (Exception e) {
            logger.error("Error : ", e);
            responseData.setError(ErrorMessage.SERVER_ERROR);
            return ResponseEntity.ok(responseData);
        }

    }


}








