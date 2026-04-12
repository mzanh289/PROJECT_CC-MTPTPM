package CCPTMT.DoAn.QuanLyCaLam.service;

import java.util.List;

import CCPTMT.DoAn.QuanLyCaLam.dto.RequestCreateDto;
import CCPTMT.DoAn.QuanLyCaLam.dto.ShiftChangeRequestDto;
import CCPTMT.DoAn.QuanLyCaLam.entity.Request;
import CCPTMT.DoAn.QuanLyCaLam.entity.WorkSchedule;

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

    /**
     * Lấy tất cả yêu cầu (dành cho Admin)
     * @return Danh sách tất cả yêu cầu
     */
    List<Request> getAllRequests();

    /**
     * Duyệt yêu cầu
     * @param requestId ID của yêu cầu
     * @return Request đã duyệt
     */
    Request approveRequest(Integer requestId);

    /**
     * Từ chối yêu cầu
     * @param requestId ID của yêu cầu
     * @return Request đã từ chối
     */
    Request rejectRequest(Integer requestId);

    /**
     * Lấy chi tiết một yêu cầu
     * @param requestId ID của yêu cầu
     * @return Request
     */
    Request getRequestById(Integer requestId);

    /**
     * Lấy danh sách ca làm của nhân viên (để sử dụng khi tạo yêu cầu đổi ca)
     * @param userId ID của nhân viên
     * @return Danh sách WorkSchedule sắp xếp theo ngày
     */
    List<WorkSchedule> getMyWorkSchedules(Integer userId);

    /**
     * Tạo yêu cầu đổi ca
     * @param userId ID của nhân viên
     * @param dto Dữ liệu yêu cầu đổi ca
     * @return Request đã tạo
     */
    Request createShiftChangeRequest(Integer userId, ShiftChangeRequestDto dto);
}