package kr.or.hanium.lego.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.or.hanium.lego.domain.Attendance;
import kr.or.hanium.lego.domain.Class;
import kr.or.hanium.lego.domain.Holder;
import kr.or.hanium.lego.domain.enumeration.AttendanceStatus;
import kr.or.hanium.lego.repository.AttendanceRepository;
import kr.or.hanium.lego.repository.ClassRepository;
import kr.or.hanium.lego.repository.HolderRepository;
import kr.or.hanium.lego.vm.AddAttendanceVM;
import kr.or.hanium.lego.vm.BlockAttendanceVM;
import kr.or.hanium.lego.vm.FetchAttendanceResultVM;
import kr.or.hanium.lego.vm.FetchClassesResultVM;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static kr.or.hanium.lego.config.blockchainConfig.sdkUri;

@Service
@Transactional
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final ClassRepository classRepository;
    private final HolderRepository holderRepository;

    private RestTemplate restTemplate = new RestTemplate();
    private ObjectMapper mapper = new ObjectMapper();

    public Long addAttendance(AddAttendanceVM request) {
        Class class_info = classRepository.getOne(request.getClass_id());

        Attendance newAttendance = new Attendance();
        newAttendance.setHolder_id(request.getHolder_id());
        newAttendance.setClass_id(request.getClass_id());
        newAttendance.setTime(LocalDateTime.now());
        newAttendance.setStatus(getAttendanceStatus(class_info.getStart_time(), class_info.getEnd_time()));
        newAttendance.setVerifier_id(0L);

        Attendance addedAttendance = attendanceRepository.save(newAttendance);

        // 4. 블록체인 원장 등록 - 출석 이력 등록 (api/setAttendance)
        Map<String, String> params = new HashMap<>();
        params.put("class_id", addedAttendance.getClass_id().toString());
        params.put("holder_id", addedAttendance.getHolder_id().toString());
        params.put("status", addedAttendance.getStatus().toString());
        params.put("time", addedAttendance.getTime().toString());
        params.put("verifier_id", addedAttendance.getVerifier_id().toString());
        params.put("attendance_id", addedAttendance.getId().toString());

        String result = restTemplate.getForObject(
                sdkUri + "/api/setAttendance?attendance_id={attendance_id}&class_id={class_id}&holder_id={holder_id}&status={status}&time={time}&verifier_id={verifier_id}",
                String.class,
                params
        );
        System.out.println("========블록체인 원장 등록 - 출석 이력 등록 (api/setAttendance)======");
        System.out.println(result);

        return addedAttendance.getId();
    }

    private AttendanceStatus getAttendanceStatus(LocalTime start, LocalTime end) {
        LocalTime now = LocalTime.now();

        if (now.isBefore(start) || now.equals(start)) {
            return AttendanceStatus.PRESENT;
        } else if (now.isBefore(end)) {
            return AttendanceStatus.LATE;
        }
        return AttendanceStatus.ABSENT;
    }

    public List<FetchAttendanceResultVM> fetchAttendanceList(Long class_id, Long holder_id) throws IOException {
        Holder holder = holderRepository.getOne(holder_id);

        // 5. 블록체인 원장 조회 - 출석 이력 조회 (api/getAttendance)
        Map<String, String> params = new HashMap<>();
        params.put("class_id", class_id.toString());
        params.put("holder_id", holder_id.toString());

        String result = restTemplate.getForObject(
                sdkUri + "/api/getAttendance?class_id={class_id}&holder_id={holder_id}",
                String.class, params
        );

        System.out.println("========블록체인 원장 조회 - 출석 이력 조회 (api/getAttendance)======");
        System.out.println(result);

        List<BlockAttendanceVM> blockAttendanceList = mapper.readValue(result,
                mapper.getTypeFactory().constructCollectionType(List.class, BlockAttendanceVM.class));

        List<FetchAttendanceResultVM> attendanceVMList = holder.getAttendanceList().stream()
                .filter(x -> x.getClass_id() == class_id)
                .map(x -> new FetchAttendanceResultVM(x.getId().toString(), x.getStatus().getKrName(), x.getTime()))
                .collect(Collectors.toList());

        for (int i = 0; i < attendanceVMList.size(); i++) {
            String attendanceId = attendanceVMList.get(i).getId();
            for (int j = 0; j < blockAttendanceList.size(); j++) {
                if (!attendanceId.equals(blockAttendanceList.get(j).getAttendance_id())) {
                    attendanceVMList.remove(i);
                    break;
                }
            }
        }
        return attendanceVMList;
    }

    public List<FetchClassesResultVM> fetchClasses() {
        List<Class> classList = classRepository.findAll();

        List<FetchClassesResultVM> classVMList = classList.stream()
                .map(x -> new FetchClassesResultVM(x.getName(), x.getId()))
                .collect(Collectors.toList());

        return classVMList;
    }
}
