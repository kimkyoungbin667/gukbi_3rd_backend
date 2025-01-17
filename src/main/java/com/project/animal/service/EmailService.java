package com.project.animal.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // 인증 코드 생성
    public String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000)); // 6자리 랜덤 코드
    }

    // 인증 이메일 발송
    public void sendVerificationEmail(String email, String code) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email);
        helper.setSubject("회원가입 인증 코드");
        helper.setText("<p>인증 코드: <b>" + code + "</b></p>", true);

        mailSender.send(message);
    }

    // 일정 생성 시 알림 발송
    public void sendEventCreationNotification(String email, String title, String date, String time) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email);
        helper.setSubject("새로운 일정 생성 알림");
        helper.setText(
                "<h1>새로운 일정이 생성되었습니다</h1>" +
                        "<p><b>제목:</b> " + title + "</p>" +
                        "<p><b>날짜:</b> " + date + "</p>" +
                        "<p><b>시간:</b> " + time + "</p>",
                true
        );

        mailSender.send(message);
    }

    // 일정 알림 발송 (알림 시)
    public void sendReminderNotification(String email, String subject, String title, String date, String time) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(
                "<h1>다가오는 일정 알림</h1>" +
                        "<p><b>제목:</b> " + title + "</p>" +
                        "<p><b>날짜:</b> " + date + "</p>" +
                        "<p><b>시간:</b> " + time + "</p>",
                true
        );

        mailSender.send(message);
    }
}
