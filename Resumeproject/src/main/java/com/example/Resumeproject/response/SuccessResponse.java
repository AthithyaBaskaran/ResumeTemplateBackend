package com.example.Resumeproject.response;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SuccessResponse<T> {
    private int statusCode;
    private String statusMessage;
    private T data;

    public SuccessResponse(HttpStatus status, String statusMessage, T data){
        this.statusCode = status.value();
        this.statusMessage = status.getReasonPhrase();
        if(statusMessage!=null && !statusMessage.isBlank()){
            this.statusMessage = statusMessage;
        }
        this.data = data;
    }

}
