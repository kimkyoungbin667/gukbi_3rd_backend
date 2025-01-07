package com.project.animal.mapper;

import com.project.animal.ResponseData.BoardResponseData;
import com.project.animal.dto.board.*;
import com.project.animal.dto.chat.ChatRoomDTO;
import com.project.animal.dto.chat.ChatRoomDetailDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface BoardMapper {

    // 게시글 목록 불러오기
    List<BoardListResponseDTO> getBoardList(@Param("limit") int limit, @Param("offset") int offset);

    // 게시글 수 불러오기
    Integer getBoardListCount();

    // 게시글 상세보기
    BoardDetailResponseDTO getBoardDetail(@Param("boardIdx") long boardIdx);

    // 게시글 삭제하기
    Integer deleteBoard(BoardIndexResponseDTO boardIndexResponseDTO);

    // 조회수 올리기
    Integer increaseView(@Param("boardIdx") long boardIdx);

    // 게시글 수정하기
    Integer saveEditBoard(BoardEditResponseDTO boardEditResponseDTO);

    // 게시글 작성하기
    Integer writeBoard(BoardWriteResponseDTO boardWriteResponseDTO);

    // 댓글, 대댓글 불러오기
    List<BoardCommentDTO> getBoardComment(Long longBoardIdx);

    // 댓글 작성하기
    Integer writeBoardComment(BoardWriteCommentDTO boardWriteCommentDTO);

    // 대댓글 작성하기
    Integer writeBoardReply(BoardReplyReqDTO boardReplyDTO);
}
