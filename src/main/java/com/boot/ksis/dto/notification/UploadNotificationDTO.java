package com.boot.ksis.dto.notification;

import com.boot.ksis.entity.Notification;
import com.boot.ksis.entity.OriginalResource;
import lombok.*;
import org.modelmapper.ModelMapper;

@Data
@AllArgsConstructor
@Getter
@Setter
@RequiredArgsConstructor
public class UploadNotificationDTO {

    // 계정아이디
    private String account;

    // 메시지
    private String message;

    // 유형
    private String resourceType;

    // 확인 여부
    private Boolean isRead;

    // 엔티티에 자동으로 넣어주는 코드
    private static ModelMapper modelMapper = new ModelMapper();

    public Notification createNewSignage(){
        return modelMapper.map(this, Notification.class);
    }

}
