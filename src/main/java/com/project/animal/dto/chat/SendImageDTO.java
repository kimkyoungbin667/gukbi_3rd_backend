package com.project.animal.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
public class SendImageDTO {
    private long senderIdx;  // 송신자 ID
    private String image;      // 이미지 데이터 (Base64)
    private String type;       // 메시지 유형 (IMAGE 등)
    private long sentAt;       // 전송 시간
    private boolean isLastChunk;  // 마지막 청크 여부
    private int chunkIndex;    // 현재 청크 인덱스
    private int totalChunks;   // 전체 청크 개수
    private String chunk;      // 청크 데이터 (Base64 인코딩 된 이미지의 일부)
    private long roomIdx;

    public SendImageDTO(long senderIdx, String image, String type, long sentAt, boolean isLastChunk, int chunkIndex, int totalChunks) {
        this.senderIdx = senderIdx;
        this.image = image;
        this.type = type;
        this.sentAt = sentAt;
        this.isLastChunk = isLastChunk;
        this.chunkIndex = chunkIndex;
        this.totalChunks = totalChunks;
    }

}