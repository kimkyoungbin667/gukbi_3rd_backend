package com.project.animal.dto.user;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class EmailVerificationDTO {
    private Long id;
    private String email;
    private String code;
    private LocalDateTime expirationTime;
}
