package kr.or.hanium.lego.service;

import kr.or.hanium.lego.domain.Holder;
import kr.or.hanium.lego.domain.StudentIdCard;
import kr.or.hanium.lego.domain.enumeration.CardStatus;
import kr.or.hanium.lego.repository.HolderRepository;
import kr.or.hanium.lego.repository.StudentIdCardRepository;
import kr.or.hanium.lego.vm.SignupVM;
import lombok.RequiredArgsConstructor;
import org.bitcoinj.core.Base58;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static kr.or.hanium.lego.config.blockchainConfig.sdkUri;

@Service
@RequiredArgsConstructor
public class RegisterService {

    private final HolderRepository holderRepository;
    private final StudentIdCardRepository studentIdCardRepository;

    public Long saveHolder(SignupVM signupVM) {
        Holder newHolder = new Holder();
        newHolder.setName(signupVM.getName());
        newHolder.setStudent_id(signupVM.getStudent_id());
        newHolder.setUniversity(signupVM.getUniversity());
        newHolder.setDepartment(signupVM.getDepartment());
        newHolder.setHolder_did(createDID());

        Holder savedHolder = holderRepository.save(newHolder);

        return savedHolder.getId();
    }

    public Long saveStudentIdCard(Long holder_id, SignupVM signupVM) {
        StudentIdCard newStCard = new StudentIdCard();
        LocalDateTime today = LocalDateTime.now();

        newStCard.setCard_did(createDID());
        newStCard.setVerified_date(today);
        newStCard.setExpire_date(getExpireDate(today));
        newStCard.setStatus(CardStatus.ACTIVATED);
        newStCard.setHolder_id(holder_id);
        newStCard.setIssuer_id(0L);

        StudentIdCard savedStCard = studentIdCardRepository.save(newStCard);

        // 1. 블록체인 원장에 학생증 저장 - (api/setCard)
        Map<String, String> params = new HashMap<>();
        params.put("card_did", savedStCard.getCard_did());
        params.put("holder_id", savedStCard.getHolder_id().toString());
        params.put("issuer_id", savedStCard.getIssuer_id().toString());
        params.put("update_date", LocalDateTime.now().toString());

        RestTemplate restTemplate = new RestTemplate();
        String result = restTemplate.getForObject(
                sdkUri + "/api/setCard?card_did={card_did}&holder_id={holder_id}&issuer_id={issuer_id}&update_date={update_date}",
                String.class,
                params
        );

        System.out.println("========블록체인 원장에 학생증 저장 - (api/setCard)======");
        System.out.println(result);

        return savedStCard.getId();
    }

    public LocalDateTime getExpireDate(LocalDateTime today) {
        int thisYear = today.getYear();

        LocalDateTime thisFirstSemester = LocalDateTime.of(thisYear, 3, 1, 23, 59, 59);
        LocalDateTime thisSecondSemester = LocalDateTime.of(thisYear, 9, 1, 23, 59, 59);
        LocalDateTime nextFirstSemester = LocalDateTime.of(thisYear+1, 3, 1, 23, 59, 59);

        // 1~2월 신청 = 올해 3월 1일까지
       if (today.isBefore(today.withMonth(3))) {
           return thisFirstSemester;
       }
       // 3~8월 신청 = 올해 9월 1일까지
       else if (today.isBefore(today.withMonth(9))) {
           return thisSecondSemester;
       }
       // 9~12월 신청 = 내년 3월 1일까지
       else {
           return nextFirstSemester;
       }
    }

    private String createDID() {
        UUID uuid = UUID.randomUUID();
        byte[] uuidByte = getBytesFromUUID(uuid);

        String did = "did:sov:";
        did += Base58.encode(uuidByte);

        return did;
    }

    private byte[] getBytesFromUUID(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());

        return bb.array();
    }
}
