package kr.or.hanium.lego.service;

import kr.or.hanium.lego.service.dto.MailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

    private String getAuthCode(int size) {
        Random random = new Random();
        StringBuffer buffer = new StringBuffer();
        int num = 0;

        while (buffer.length() < size) {
            num = random.nextInt(10);
            buffer.append(num);
        }

        return buffer.toString();
    }

    public MailDto createEmail(String address) {
        MailDto mailDto = new MailDto();
        String authCode = getAuthCode(6);

        String msg = "";
        msg += "<div style='margin:100px;'>";
        msg += "<h1> 안녕하세요 BLOCKID입니다 :) </h1> <br>";
        msg += "<p>회원가입을 위하여 아래의 인증 번호를 확인하신 후, 회원가입 창에 입력하여 주시기바랍니다.<p> <br>";
        msg += "<div align='center' style='border:1px solid black; font-family:verdana';>";
        msg += "<h3 style='color:blue;'>인증 번호입니다.</h3>";
        msg += "<div style='font-size:130%'>";
        msg += "<strong>" + authCode + "</strong><div><br/> </div>";

        mailDto.setToAddress(address);
        mailDto.setTitle("[BLOCKID] 회원 가입 인증 메일 안내");
        mailDto.setContents(msg);
        mailDto.setAuthCode(authCode);

        return mailDto;
    }

    public String sendAuthEmail(MailDto mailDto) {
        MimeMessage msg = mailSender.createMimeMessage();
        try {
            msg.addRecipients(Message.RecipientType.TO, mailDto.getToAddress());
            msg.setSubject(mailDto.getTitle());
            msg.setText(mailDto.getContents(), "utf-8", "html");
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        mailSender.send(msg);

        return mailDto.getAuthCode();
    }
}
