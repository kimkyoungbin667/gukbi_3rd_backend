package com.project.animal.repository;

import com.project.animal.dto.board.BoardListResponseDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;


@Repository
@RequiredArgsConstructor
public class BoardRepository {

    private final SqlSessionTemplate sql;

    public List<BoardListResponseDTO> getBoardList(int limit, int offset) {
        return sql.selectList("Board.getBoardList", Map.of("limit", limit, "offset", offset));
    }

    public int getBoardListCount() {
        return sql.selectOne("Board.getBoardListCount");
    }
}
