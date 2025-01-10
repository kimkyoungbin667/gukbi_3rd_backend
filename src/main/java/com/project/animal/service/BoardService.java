package com.project.animal.service;

import com.project.animal.dto.board.*;
import com.project.animal.mapper.BoardMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class BoardService {

    @Autowired
    private BoardMapper boardMapper;

    @Autowired
    private FileService fileService;

    // 총 게시글 수 찾기
    public int getBoardListCount() { return boardMapper.getBoardListCount();}

    // 모든 게시글 불러오기 (페이지네이션 포함)
    public List<BoardPostListResDTO> getBoardList(int limit, int offset) {return boardMapper.getBoardList(limit, offset);}

    // 게시글 작성하기
    @Transactional
    public Integer createBoardPost(BoardPostCreateReqDTO boardPostCreateReqDTO) {

        // 1. 게시글 저장
        Integer insertPostResult = boardMapper.createBoardPost(boardPostCreateReqDTO);

        // 2. 해당 게시글에 업로드된 이미지 경로를 하나씩 DB에 저장
        if(boardPostCreateReqDTO.getImageFiles() != null && !boardPostCreateReqDTO.getImageFiles().isEmpty()) {

            for (String imagePath : boardPostCreateReqDTO.getImageFiles()) {
                boardMapper.insertBoardImage(boardPostCreateReqDTO.getBoardIdx(), imagePath);
            }
        }

        return insertPostResult;
    }

    // 게시글 상세보기
    public BoardPostReadResDTO readBoardPost(BoardPostReadReqDTO boardPostReadReqDTO) {

        BoardPostReadResDTO boardPostReadResDTO = boardMapper.readBoardPost(boardPostReadReqDTO);

        System.out.println(boardPostReadResDTO);

        // imageFiles를 리스트로 변환
        if (boardPostReadResDTO.getImagePath() != null) {
            String imageUrls = boardPostReadResDTO.getImagePath();
            List<String> imageFiles = Arrays.asList(imageUrls.split(",")); // 콤마로 나누기
            boardPostReadResDTO.setImageFiles(imageFiles);
        }

        return boardPostReadResDTO;

    }

    // 게시글 수정하기
    @Transactional
    public void updateBoardPost(BoardPostUpdateReqDTO boardPostUpdateReqDTO, List<MultipartFile> newImages) {
        Long boardIdx = boardPostUpdateReqDTO.getBoardIdx();

        // Null 체크 및 초기화
        final List<String> existingImages = boardPostUpdateReqDTO.getExistingImages() != null
                ? boardPostUpdateReqDTO.getExistingImages()
                : Collections.emptyList(); // 기존 이미지 리스트 초기화
        System.out.println("기존 이미지: " + existingImages);

        // 1. 게시글에 해당하는 모든 이미지 조회
        List<String> dbImages = boardMapper.findImagesByBoardIdx(boardIdx);
        System.out.println("DB에 저장된 이미지 목록: " + dbImages);

        // 2. 삭제할 이미지 선별 및 삭제
        if (dbImages != null) {
            List<String> imagesToDelete = dbImages.stream()
                    .filter(image -> !existingImages.contains(image))
                    .collect(Collectors.toList());
            System.out.println("삭제할 이미지 목록: " + imagesToDelete);

            if (imagesToDelete != null && !imagesToDelete.isEmpty()) {
                try {
                    boardMapper.deleteImagesByBoardIdxAndImageList(boardIdx, imagesToDelete); // DB에서 삭제
                    if (fileService != null) {
                        fileService.deleteFiles(imagesToDelete); // 파일 시스템에서 삭제
                    } else {
                        System.err.println("fileService가 null입니다.");
                    }
                } catch (Exception e) {
                    throw new RuntimeException("파일 삭제 중 오류 발생: " + e.getMessage(), e);
                }
            } else {
                System.out.println("삭제할 이미지가 없습니다.");
            }
        } else {
            System.err.println("DB에서 조회된 이미지가 없습니다.");
        }

        // 3. 새 이미지 처리 및 DB 추가
        if (newImages != null && !newImages.isEmpty()) {
            try {
                List<String> uploadedImages = fileService.uploadFiles(newImages); // 새 이미지 업로드
                if (uploadedImages != null && !uploadedImages.isEmpty()) {
                    boardMapper.insertImages(boardIdx, uploadedImages); // DB에 새 이미지 경로 저장
                } else {
                    System.out.println("업로드된 새 이미지가 없습니다.");
                }
            } catch (Exception e) {
                throw new RuntimeException("새 이미지 업로드 중 오류 발생: " + e.getMessage(), e);
            }
        } else {
            System.out.println("추가할 새 이미지가 없습니다.");
        }

        // 4. 게시글 제목 및 내용 수정 처리
        try {
            boardMapper.updateBoardPost(boardPostUpdateReqDTO);
            System.out.println("게시글 수정 성공");
        } catch (Exception e) {
            throw new RuntimeException("게시글 수정 중 오류 발생: " + e.getMessage(), e);
        }
    }






    // 게시글 삭제하기
    public Integer deleteBoardPost(BoardPostDeleteReqDTO boardPostDeleteReqDTO) {return boardMapper.deleteBoardPost(boardPostDeleteReqDTO);}


    // 조회수 올리기
    public Integer increaseView(long boardIdx) {return boardMapper.increaseView(boardIdx);}



    // 댓글, 대댓글 조회하기
    public List<BoardPostReadCommentsResDTO> readBoardComments(Long longBoardIdx) {return boardMapper.readBoardComments(longBoardIdx);}

    // 댓글 작성하기
    public Integer createBoardComment(BoardPostCreateCommentReqDTO boardPostCreateCommentReqDTO) {return boardMapper.createBoardComment(boardPostCreateCommentReqDTO);}

    // 대댓글 작성하기
    public Integer createBoardReply(BoardPostCreateReplyReqDTO boardPostCreateReplyReqDTO) {return boardMapper.createBoardReply(boardPostCreateReplyReqDTO);}

    // 좋아요를 누른지 판단
    public Boolean isLikedPost(BoardPostUpLikeReqDTO boardPostUpLikeReqDTO) {return boardMapper.isLikedPost(boardPostUpLikeReqDTO);}

    // 좋아요 +1 하기
    public Integer upBoardPostLike(BoardPostUpLikeReqDTO boardPostUpLikeReqDTO) {return boardMapper.upBoardPostLike(boardPostUpLikeReqDTO);}

    // 좋아요 -1 하기
    public Integer downBoardPostLike(BoardPostUpLikeReqDTO boardPostUpLikeReqDTO) {return boardMapper.downBoardPostLike(boardPostUpLikeReqDTO);}

    // 업로드된 이미지를 서버 upload 폴더에 저장
    @Transactional
    public String saveImage(MultipartFile file) throws IOException {
        // 업로드 경로 설정
        Path uploadDir = Paths.get("src/main/upload").toAbsolutePath();

        // 디렉토리가 없으면 생성
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // 고유한 파일 이름 생성
        String uniqueFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

        // 저장 경로 생성
        Path filePath = uploadDir.resolve(uniqueFileName);

        // 파일 복사 및 저장
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }

        // 상대 경로 반환
        return "upload/" + uniqueFileName;
    }


    public void processCreateBoardPost(BoardPostCreateReqDTO boardPostCreateReqDTO) {

    }

    // 게시글 이미지 테이블에 이미지 저장

}
