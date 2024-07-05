package com.example.EnasiniEmsin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RequestDto {

    private String message;

    private boolean success;

    private Object data;

    public RequestDto(String message, boolean success){
        this.message = message;
        this.success = success;
    }

    public RequestDto(boolean success){
        this.success = success;
    }
}
