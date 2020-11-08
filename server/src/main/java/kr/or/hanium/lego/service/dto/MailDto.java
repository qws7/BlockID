package kr.or.hanium.lego.service.dto;

import lombok.Data;

@Data
public class MailDto {
    private String toAddress;
    private String title;
    private String contents;
    private String authCode;
}
