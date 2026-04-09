package CCPTMT.DoAn.QuanLyCaLam.service;

import java.util.List;

import CCPTMT.DoAn.QuanLyCaLam.dto.RequestCreateDto;
import CCPTMT.DoAn.QuanLyCaLam.entity.Request;

public interface RequestService {

    /**
     * Tạo yêu cầu mới cho nhân viên
     * @param userId ID của nhân viên
     * @param dto Dữ liệu yêu cầu
     * @return Request đã tạo
     */
    Request createRequest(Integer userId, RequestCreateDto dto);

    /**
     * Lấy danh sách yêu cầu của một nhân viên
     * @param userId ID của nhân viên
     * @return Danh sách yêu cầu
     */
    List<Request> getRequestsByUserId(Integer userId);
}