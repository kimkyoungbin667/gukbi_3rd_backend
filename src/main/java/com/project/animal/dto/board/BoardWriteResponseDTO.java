package com.project.animal.dto.board;

import lombok.Data;

@Data
public class BoardWriteResponseDTO {
    
    private long userIdx;
    private String content;
    private String title;

}
