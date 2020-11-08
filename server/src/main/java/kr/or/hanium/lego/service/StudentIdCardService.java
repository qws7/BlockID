package kr.or.hanium.lego.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.or.hanium.lego.domain.StudentIdCard;
import kr.or.hanium.lego.domain.enumeration.CardStatus;
import kr.or.hanium.lego.repository.StudentIdCardRepository;
import kr.or.hanium.lego.vm.FetchIdcardResultVM;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static kr.or.hanium.lego.config.blockchainConfig.sdkUri;

@Service
@RequiredArgsConstructor
@Transactional
public class StudentIdCardService {
    private final StudentIdCardRepository studentIdCardRepository;

    private RestTemplate restTemplate = new RestTemplate();
    private ObjectMapper mapper = new ObjectMapper();

    public FetchIdcardResultVM fetchStudentIdCardWithHolderId(Long holder_id) {
        StudentIdCard idCard = studentIdCardRepository.findByHolder_id(holder_id);

        // 2. 블록체인 원장 조회 - 학생증 검증 (api/getCard)
        Boolean isInBlockchainLedger = false;
        Map<String, String> params = new HashMap<>();
        params.put("card_did", idCard.getCard_did());

        String result = restTemplate.getForObject(
                sdkUri + "/api/getCard?card_did={card_did}",
                String.class,
                params
        );

        System.out.println("========블록체인 원장 조회 - 학생증 검증 (api/getCard)======");
        System.out.println(result);


        try {
            Map<String, String> map = mapper.readValue(result, Map.class);
            System.out.println(map);

            if (map.get("card_did").equals(idCard.getCard_did())) {
                isInBlockchainLedger = true;
            }
        } catch (IOException e) {
        }

        if (LocalDateTime.now().isAfter(idCard.getExpire_date())) {
            idCard.setStatus(CardStatus.EXPIRED);
            studentIdCardRepository.save(idCard);
        }

        return setIdCardToIdCardResultVM(idCard, isInBlockchainLedger);
    }

    public FetchIdcardResultVM activateStudentIdCardWithHolderId(Long holder_id, LocalDateTime expireDate) {
        StudentIdCard idCard = studentIdCardRepository.findByHolder_id(holder_id);
        idCard.setExpire_date(expireDate);
        idCard.setStatus(CardStatus.ACTIVATED);

        studentIdCardRepository.save(idCard);

        // 3. 블록체인 원장 수정 - 학생증 재발급 (api/updateCard)
        Boolean isInBlockchainLedger = false;
        Map<String, String> params = new HashMap<>();
        params.put("card_did", idCard.getCard_did());
        params.put("update_date", LocalDateTime.now().toString());

        String result = restTemplate.getForObject(
                sdkUri + "/api/updateCard?card_did={card_did}&update_date={update_date}",
                String.class,
                params
        );

        System.out.println("========블록체인 원장 수정 - 학생증 재발급 (api/updateCard)======");
        System.out.println(result);

        if (result.equals("success")) {
            isInBlockchainLedger = true;
        }

        return setIdCardToIdCardResultVM(idCard, true);
    }

    private FetchIdcardResultVM setIdCardToIdCardResultVM(StudentIdCard idCard, Boolean isInBlockchainLedger) {
        FetchIdcardResultVM idCardVM = new FetchIdcardResultVM();
        idCardVM.setStudentId(idCard.getHolder().getStudent_id());
        idCardVM.setDepartment(idCard.getHolder().getDepartment());
        idCardVM.setExpireDate(idCard.getExpire_date());
        idCardVM.setStatus(idCard.getStatus());
        idCardVM.setUniversity(idCard.getHolder().getUniversity());
        idCardVM.setHolder_id(idCard.getHolder_id());
        idCardVM.setName(idCard.getHolder().getName());
        idCardVM.setIsInBlockchainLedger(isInBlockchainLedger);

        return idCardVM;
    }
}
