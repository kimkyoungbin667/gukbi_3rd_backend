package com.project.animal.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdminMapper {

    // 모든 사용자 가져오기
    List<Map<String, Object>> getAllUsers();

    // 특정 사용자 삭제
    void deleteUser(@Param("userId") Long userId);

    // 사용자 활성화/비활성화 업데이트
    void updateUserStatus(@Param("userId") Long userId, @Param("isActive") boolean isActive);

    // 모든 게시글 가져오기
    List<Map<String, Object>> getAllPosts();

    // 특정 게시글 삭제
    void deletePost(@Param("postId") Long postId);

    // 특정 사용자가 작성한 게시글 가져오기
    List<Map<String, Object>> getUserPosts(@Param("userId") Long userId);

    // 특정 사용자가 작성한 댓글 가져오기
    List<Map<String, Object>> getUserComments(@Param("userId") Long userId);

}

