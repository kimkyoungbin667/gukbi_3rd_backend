package com.project.animal.mapper;

import com.project.animal.dto.board.BoardListResponseDTO;
import com.project.animal.dto.chat.ChatRoomDTO;
import com.project.animal.dto.chat.ChatRoomDetailDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BoardMapper {

    // 게시글 목록 불러오기
    List<BoardListResponseDTO> getBoardList(@Param("limit") int limit, @Param("offset") int offset);

    // 게시글 수 불러오기
    Integer getBoardListCount();
}
