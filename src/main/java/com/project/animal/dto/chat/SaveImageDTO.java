package com.project.animal.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SaveImageDTO {

    private long roomIdx;
    private long senderIdx;
    private String sentAt;
    private String imageUrl;


}
