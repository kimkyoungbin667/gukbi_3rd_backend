package com.project.animal.service;

import com.project.animal.mapper.AdminMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    private final AdminMapper adminMapper;

    public AdminService(AdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }

    // 모든 사용자 가져오기
    public List<Map<String, Object>> getAllUsers() {
        return adminMapper.getAllUsers();
    }

    // 특정 사용자 삭제
    public void deleteUser(Long userId) {
        adminMapper.deleteUser(userId);
    }

    // 사용자 활성화/비활성화
    public void toggleUserStatus(Long userId, boolean isActive) {
        adminMapper.updateUserStatus(userId, isActive);
    }

    // 모든 게시글 가져오기
    public List<Map<String, Object>> getAllPosts() {
        return adminMapper.getAllPosts();
    }

    // 특정 게시글 삭제
    public void deletePost(Long postId) {
        adminMapper.deletePost(postId);
    }

    public Map<String, Object> getUserActivity(Long userId) {
        List<Map<String, Object>> userPosts = adminMapper.getUserPosts(userId);
        List<Map<String, Object>> userComments = adminMapper.getUserComments(userId);
        return Map.of("posts", userPosts, "comments", userComments);
    }
}
