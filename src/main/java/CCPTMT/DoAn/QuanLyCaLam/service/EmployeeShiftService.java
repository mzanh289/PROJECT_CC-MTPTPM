package CCPTMT.DoAn.QuanLyCaLam.service;

import java.util.List;

import CCPTMT.DoAn.QuanLyCaLam.dto.EmployeeShiftItemDto;

public interface EmployeeShiftService {

    List<EmployeeShiftItemDto> findByUserId(Integer userId);
}
