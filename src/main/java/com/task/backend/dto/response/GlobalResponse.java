package com.task.backend.dto.response;

import lombok.*;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class GlobalResponse<T> {
    private String respCode;
    private String respMsg;
    private T data;

    public GlobalResponse(String respCode, String respMsg) {
        this.respCode = respCode;
        this.respMsg = respMsg;
    }
}
