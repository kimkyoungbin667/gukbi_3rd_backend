package com.project.animal.mapper;

import com.project.animal.ResponseData.BoardResponseData;
import com.project.animal.dto.board.BoardDetailResponseDTO;
import com.project.animal.dto.board.BoardListResponseDTO;
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
    Integer deleteBoard(@Param("boardIdx") long boardIdx);

    // 조회수 올리기
    Integer increaseView(@Param("boardIdx") long boardIdx);
}
