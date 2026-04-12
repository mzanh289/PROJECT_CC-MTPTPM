package CCPTMT.DoAn.QuanLyCaLam.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<EmployeeShiftItemDto> findByUserIdAndDate(Integer userId, LocalDate date) {
        return workScheduleRepository.findByUserUserIdAndWorkDate(userId, date)
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public List<EmployeeShiftItemDto> findByUserIdAndWeek(Integer userId, LocalDate date) {
        LocalDate startOfWeek = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        return workScheduleRepository.findByUserUserIdAndWorkDateBetween(userId, startOfWeek, endOfWeek)
                .stream()
                .map(this::toDto)
                .toList();
    }

    private EmployeeShiftItemDto toDto(CCPTMT.DoAn.QuanLyCaLam.entity.WorkSchedule item) {
        return EmployeeShiftItemDto.builder()
                .workDate(item.getWorkDate())
                .shiftName(item.getShift().getShiftName())
                .startTime(item.getShift().getStartTime())
                .endTime(item.getShift().getEndTime())
                .build();
    }
}
