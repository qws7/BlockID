package kr.or.hanium.lego.controller;

import io.swagger.annotations.ApiOperation;
import kr.or.hanium.lego.service.AttendanceService;
import kr.or.hanium.lego.vm.AddAttendanceVM;
import kr.or.hanium.lego.vm.FetchAttendanceResultVM;
import kr.or.hanium.lego.vm.FetchClassesResultVM;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;


    @ApiOperation(value = "QR코드 출석체크")
    @PostMapping("")
    public Long addAttendance(@RequestBody AddAttendanceVM request) {
        Long attendanceId = attendanceService.addAttendance(request);

        return attendanceId;
    }

    @ApiOperation(value = "출석 내역 조회")
    @GetMapping(value = "/list")
    public List<FetchAttendanceResultVM> fetchAttendance(@RequestParam(value = "class_id") Long class_id,
                                                         @RequestParam(value = "holder_id") Long holder_id) throws IOException {
        List<FetchAttendanceResultVM> attendanceVMList = attendanceService.fetchAttendanceList(class_id, holder_id);
        
        return attendanceVMList;
    }

    @ApiOperation(value = "수업 목록 조회")
    @GetMapping(value = "/classes")
    public List<FetchClassesResultVM> fetchClasses() {
        List<FetchClassesResultVM> fetchClassesResultVM = attendanceService.fetchClasses();

        return fetchClassesResultVM;
    }

}
