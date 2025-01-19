package com.project.animal.controller;

import com.project.animal.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://58.74.46.219:33333")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // 모든 사용자 가져오기
    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<Map<String, Object>> users = adminService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // 특정 사용자 삭제
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    // 사용자 활성화/비활성화
    @PutMapping("/users/{userId}/status")
    public ResponseEntity<Map<String, String>> toggleUserStatus(
            @PathVariable Long userId,
            @RequestBody Map<String, Boolean> status) {
        boolean isActive = status.getOrDefault("isActive", true);
        adminService.toggleUserStatus(userId, isActive);
        return ResponseEntity.ok(Map.of("message", "User status updated successfully"));
    }

    // 모든 게시글 가져오기
    @GetMapping("/posts")
    public ResponseEntity<List<Map<String, Object>>> getAllPosts() {
        List<Map<String, Object>> posts = adminService.getAllPosts();
        return ResponseEntity.ok(posts);
    }


    // 특정 게시글 삭제
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Map<String, String>> deletePost(@PathVariable Long postId) {
        adminService.deletePost(postId);
        return ResponseEntity.ok(Map.of("message", "Post deleted successfully"));
    }

    // 특정 사용자 활동 기록 가져오기
    @GetMapping("/users/{userId}/activity")
    public ResponseEntity<Map<String, Object>> getUserActivity(@PathVariable Long userId) {
        Map<String, Object> activity = adminService.getUserActivity(userId);
        return ResponseEntity.ok(activity);
    }

}
