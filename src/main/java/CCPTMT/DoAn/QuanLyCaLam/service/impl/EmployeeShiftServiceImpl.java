package CCPTMT.DoAn.QuanLyCaLam.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import CCPTMT.DoAn.QuanLyCaLam.dto.EmployeeShiftItemDto;
import CCPTMT.DoAn.QuanLyCaLam.repository.WorkScheduleRepository;
import CCPTMT.DoAn.QuanLyCaLam.service.EmployeeShiftService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeShiftServiceImpl implements EmployeeShiftService {

    private final WorkScheduleRepository workScheduleRepository;

    @Override
    public List<EmployeeShiftItemDto> findByUserId(Integer userId) {
        return workScheduleRepository.findByUserUserIdOrderByWorkDateAsc(userId)
                .stream()
                .map(item -> EmployeeShiftItemDto.builder()
                        .workDate(item.getWorkDate())
                        .shiftName(item.getShift().getShiftName())
                        .startTime(item.getShift().getStartTime())
                        .endTime(item.getShift().getEndTime())
                        .build())
                .toList();
    }
}
