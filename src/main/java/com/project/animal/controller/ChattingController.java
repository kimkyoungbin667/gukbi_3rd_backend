package com.project.animal.controller;

import com.project.animal.dto.chat.SaveImageDTO;
import com.project.animal.dto.chat.SendImageDTO;
import com.project.animal.dto.chat.SendMessageDTO;
import com.project.animal.dto.chat.TypingStatus;
import com.project.animal.service.ChatService;
import com.project.animal.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@CrossOrigin(origins = "http://58.74.46.219:33333")
public class ChattingController {

    @Autowired
    private ChatService chatService;
    private JwtUtil jwtUtil;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public ChattingController(JwtUtil jwtUtil, ChatService chatService) {
        this.jwtUtil = jwtUtil;
        this.chatService = chatService;
    }

    @MessageMapping("/room/{roomIdx}/join")
    @SendTo("/topic/room/{roomIdx}")
    public SendMessageDTO joinRoom(SendMessageDTO message) {
        message.setMessage("님이 입장했습니다.");
        return message;
    }

    @MessageMapping("/room/{roomIdx}/send")
    @SendTo("/topic/room/{roomIdx}")
    public SendMessageDTO sendMessage(@Payload SendMessageDTO message) {
        System.out.println("받은 메시지: " + message);

        // 토큰 검증
        if (!jwtUtil.validateToken(message.getSenderToken())) {
            throw new RuntimeException("Invalid sender token");
        }

        // 토큰에서 발신자 정보 추출
        Long senderIdx = jwtUtil.getIdFromToken(message.getSenderToken());
        message.setSenderIdx(senderIdx); // 발신자 ID 추가

        message.setSenderProfile(chatService.getUserProfile(senderIdx));
        System.out.println("보낸 사람 : "+ senderIdx);

        // 메시지 저장
        int result = chatService.sendMessage(message);

        // 클라이언트로 반환할 메시지
        return message;
    }

    @MessageMapping("/room/{roomIdx}/typing")
    public void typing(@Payload TypingStatus typingStatus,
                       @DestinationVariable Long roomIdx,
                       SimpMessageHeaderAccessor headerAccessor) {

        String senderSessionId = headerAccessor.getSessionId();  // 보낸 사람의 세션 ID
        Long senderIdx = typingStatus.getSenderIdx();            // 보낸 사람 ID

        // ✅ 모든 사용자에게 전송
        messagingTemplate.convertAndSend(
                "/topic/room/" + roomIdx + "/typing",
                new TypingStatus(senderIdx, roomIdx, typingStatus.isTyping())
        );
    }

    // ✅ 청크 데이터를 임시로 저장할 Map (roomIdx + senderIdx 기준)
    private ConcurrentHashMap<String, StringBuilder> imageChunksMap = new ConcurrentHashMap<>();

    @MessageMapping("/room/{roomIdx}/sendImageChunk")
    @SendTo("/topic/room/{roomIdx}/image")
    public SendImageDTO sendImage(@DestinationVariable String roomIdx, @Payload SendImageDTO message) throws IOException {
        String base64Chunk = message.getChunk();  // 청크 데이터 가져오기
        int chunkIndex = message.getChunkIndex();
        int totalChunks = message.getTotalChunks();

        System.out.println("📩 수신된 메시지: " + message);

        // 청크 데이터를 저장할 키 생성 (roomIdx + senderIdx)
        String key = roomIdx + "_" + message.getSenderIdx();
        StringBuilder imageData = imageChunksMap.getOrDefault(key, new StringBuilder());

        // 청크 데이터 추가
        imageData.append(base64Chunk);
        imageChunksMap.put(key, imageData);

        // 모든 청크가 다 수신되었으면
        if (chunkIndex == totalChunks - 1) {
            // 모든 청크를 하나로 합친 후 image 필드에 저장
            String fullImage = imageData.toString();

            // 저장소 초기화 (다음 전송을 위해 비워둡니다)
            imageChunksMap.remove(key);

            // 업로드 경로 설정 (루트 디렉토리 기준)
            String uploadDirectory = System.getProperty("user.dir") + "/src/main/upload";
            File uploadDir = new File(uploadDirectory);

            // 폴더가 존재하지 않으면 생성
            if (!uploadDir.exists()) {
                boolean created = uploadDir.mkdirs();
                if (!created) {
                    throw new IOException("업로드 폴더 생성 실패");
                }
            }

            // 고유한 파일명 생성
            String fileName = UUID.randomUUID().toString() + ".jpg";
            String filePath = Paths.get(uploadDirectory, fileName).toString();  // 파일 경로 설정
            String datetime = convertTimestampToDatetime(message.getSentAt());

            // DB에 이미지 저장
            SaveImageDTO saveImageDTO = new SaveImageDTO(message.getRoomIdx(), message.getSenderIdx(), datetime, fileName);

            chatService.saveSendImage(saveImageDTO);

            // Base64 데이터를 파일로 저장
            byte[] imageBytes = java.util.Base64.getDecoder().decode(fullImage);
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(imageBytes);
                fos.flush();
            } catch (IOException e) {
                e.printStackTrace();
                throw new IOException("이미지 저장 실패");
            }

            // 이미지 경로와 함께 반환
            System.out.println("✅ 모든 청크 합침 완료. 이미지 저장 경로: " + filePath);
            SendImageDTO sendImageDTO = new SendImageDTO(message.getSenderIdx(), "/upload/" + fileName, "IMAGE", message.getSentAt(), true, chunkIndex, totalChunks);

            return sendImageDTO;
        }

        return null;
    }

    public String convertTimestampToDatetime(long timestamp) {
        Instant instant = Instant.ofEpochMilli(timestamp);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(ZoneId.of("Asia/Seoul"));
        return formatter.format(instant);
    }




}