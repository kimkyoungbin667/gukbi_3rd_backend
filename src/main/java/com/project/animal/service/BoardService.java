package com.project.animal.service;

import com.project.animal.dto.board.BoardListResponseDTO;
import com.project.animal.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.sql.init.SqlDataSourceScriptDatabaseInitializer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final SqlDataSourceScriptDatabaseInitializer dataSourceScriptDatabaseInitializer;

    public int getBoardListCount() {
        return boardRepository.getBoardListCount();
    }

    public List<BoardListResponseDTO> getBoardList(int limit, int offset) {
        return boardRepository.getBoardList(limit, offset);
    }
}
