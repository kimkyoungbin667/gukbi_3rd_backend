package com.project.animal.repository;

import com.project.animal.dto.board.BoardListResponseDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BoardRepository {

    private final SqlSessionTemplate sql;

    public List<BoardListResponseDTO> getBoardList() {
        return sql.selectList("Board.getBoardList");
    }
}
