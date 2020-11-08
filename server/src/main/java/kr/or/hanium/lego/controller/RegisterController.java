package kr.or.hanium.lego.controller;

import io.swagger.annotations.ApiOperation;
import kr.or.hanium.lego.service.dto.MailDto;
import kr.or.hanium.lego.service.EmailService;
import kr.or.hanium.lego.service.RegisterService;
import kr.or.hanium.lego.vm.SendEmailResultVM;
import kr.or.hanium.lego.vm.SendEmailVM;
import kr.or.hanium.lego.vm.SignupVM;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class RegisterController {
    private final RegisterService registerService;
    private final EmailService emailService;

    @ApiOperation(value = "이메일 인증코드 보내기")
    @PostMapping("/email/send")
    public SendEmailResultVM sendEmail(@RequestBody SendEmailVM request){
        MailDto mailDto = emailService.createEmail(request.getEmail());

        SendEmailResultVM sendEmailResultVM = new SendEmailResultVM();
        sendEmailResultVM.setAuthCode(emailService.sendAuthEmail(mailDto));

        return sendEmailResultVM;
    }

    @ApiOperation(value = "Holder 회원가입과 학생증 생성")
    @PostMapping("/signup")
    public Long signUp(@RequestBody SignupVM request){
        Long holderId = registerService.saveHolder(request);
        Long stCardId = registerService.saveStudentIdCard(holderId, request);

        return holderId;
    }


}
