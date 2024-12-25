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

    public List<BoardListResponseDTO> getBoardList() {
        return boardRepository.getBoardList();
    }
}
