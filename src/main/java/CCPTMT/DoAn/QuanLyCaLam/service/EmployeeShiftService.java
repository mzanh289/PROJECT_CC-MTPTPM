package CCPTMT.DoAn.QuanLyCaLam.service;

import java.util.List;
import java.time.LocalDate;

import CCPTMT.DoAn.QuanLyCaLam.dto.EmployeeShiftItemDto;

public interface EmployeeShiftService {

    List<EmployeeShiftItemDto> findByUserId(Integer userId);

    List<EmployeeShiftItemDto> findByUserIdAndDate(Integer userId, LocalDate date);

    List<EmployeeShiftItemDto> findByUserIdAndWeek(Integer userId, LocalDate date);
}
