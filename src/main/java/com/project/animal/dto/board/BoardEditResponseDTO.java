package com.project.animal.dto.board;

import lombok.Data;

@Data
public class BoardEditResponseDTO {

    private Long boardIdx;
    private Long userIdx;
    private String content;
}
