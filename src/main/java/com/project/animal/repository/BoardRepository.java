package com.project.animal.repository;

import com.kkbproject.dto.BoardDTO;
import lombok.RequiredArgsConstructor;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BoardRepository {

    private final SqlSessionTemplate sql;

    public List<BoardDTO> findAll() {
        return sql.selectList("Board.findAll");
    }

    public BoardDTO detailBoard(int board_idx) {
        return sql.selectOne("Board.detailBoard", board_idx);
    }

    public int writeBoard(BoardDTO boardDTO) {
        return sql.insert("Board.writeBoard", boardDTO);
    }

    public int deleteBoard(int board_Idx) {
        return sql.update("Board.deleteBoard", board_Idx);
    }

    public int reviseBoard(BoardDTO boardDTO) {
        return sql.update("Board.reviseBoard", boardDTO);
    }

    public List<BoardDTO> findTitle(String param) {
        return sql.selectList("Board.findTitle", param);
    }

    public List<BoardDTO> findWriter(String param) {
        return sql.selectList("Board.findWriter", param);
    }

    public int goodBoard(int boardId) {
        return sql.update("Board.goodBoard", boardId);
    }
}
