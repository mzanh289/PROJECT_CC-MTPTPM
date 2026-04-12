package CCPTMT.DoAn.QuanLyCaLam.service.impl;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import CCPTMT.DoAn.QuanLyCaLam.dto.RequestCreateDto;
import CCPTMT.DoAn.QuanLyCaLam.dto.ShiftChangeRequestDto;
import CCPTMT.DoAn.QuanLyCaLam.entity.Request;
import CCPTMT.DoAn.QuanLyCaLam.entity.Shift;
import CCPTMT.DoAn.QuanLyCaLam.entity.User;
import CCPTMT.DoAn.QuanLyCaLam.entity.WorkSchedule;
import CCPTMT.DoAn.QuanLyCaLam.entity.enums.RequestStatus;
import CCPTMT.DoAn.QuanLyCaLam.repository.RequestRepository;
import CCPTMT.DoAn.QuanLyCaLam.repository.ShiftRepository;
import CCPTMT.DoAn.QuanLyCaLam.repository.UserRepository;
import CCPTMT.DoAn.QuanLyCaLam.repository.WorkScheduleRepository;
import CCPTMT.DoAn.QuanLyCaLam.service.RequestService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final WorkScheduleRepository workScheduleRepository;
    private final ShiftRepository shiftRepository;

    @Override
    @Transactional
    public Request createRequest(Integer userId, RequestCreateDto dto) {
        // Validate date range: fromDate <= toDate
        if (!dto.isValidDateRange()) {
            throw new IllegalArgumentException("Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc");
        }

        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên"));

        // Create request with PENDING status and current timestamp
        Request request = Request.builder()
                .user(user)
                .type(dto.getType())
                .fromDate(dto.getFromDate())
                .toDate(dto.getToDate())
                .reason(dto.getReason())
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        return requestRepository.save(request);
    }

    @Override
    public List<Request> getRequestsByUserId(Integer userId) {
        return requestRepository.findByUserUserId(userId);
    }

    @Override
    public List<Request> getAllRequests() {
        return requestRepository.findAll();
    }

    @Override
    @Transactional
    public Request approveRequest(Integer requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu"));

        // Nếu là yêu cầu đổi ca, thực hiện update WorkSchedule
        if (request.getType().name().equals("SHIFT_CHANGE")) {
                        if (request.getWorkDate() == null || request.getTargetShift() == null) {
                                throw new IllegalArgumentException("Yêu cầu đổi ca không hợp lệ (thiếu dữ liệu ca/ngày làm)");
                        }

                        Shift sourceShift = request.getShift();
                        if (sourceShift == null) {
                                throw new IllegalArgumentException("Yêu cầu đổi ca thiếu ca hiện tại");
                        }

                        if (sourceShift.getShiftId().equals(request.getTargetShift().getShiftId())) {
                                throw new IllegalArgumentException("Ca muốn đổi phải khác ca hiện tại");
                        }

            // Tìm WorkSchedule cần update (theo userId, workDate, và shiftId cũ)
            WorkSchedule workSchedule = workScheduleRepository
                    .findByUserUserIdAndWorkDateAndShiftShiftId(
                            request.getUser().getUserId(),
                            request.getWorkDate(),
                                                        sourceShift.getShiftId())
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy lịch làm việc để cập nhật"));

                        boolean alreadyHasTargetShift = workScheduleRepository
                                        .existsByUserUserIdAndWorkDateAndShiftShiftId(
                                                        request.getUser().getUserId(),
                                                        request.getWorkDate(),
                                                        request.getTargetShift().getShiftId());
                        if (alreadyHasTargetShift) {
                                throw new IllegalArgumentException("Nhân viên đã có ca muốn đổi trong ngày này");
                        }
            
            // Update ca làm từ ca cũ (shiftId) sang ca mới (targetShiftId)
            workSchedule.setShift(request.getTargetShift());
            workScheduleRepository.save(workSchedule);
        }

        request.setStatus(RequestStatus.APPROVED);
        return requestRepository.save(request);
    }

    @Override
    @Transactional
    public Request rejectRequest(Integer requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu"));
        request.setStatus(RequestStatus.REJECTED);
        return requestRepository.save(request);
    }

    @Override
    public Request getRequestById(Integer requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu"));
    }

    @Override
    public List<WorkSchedule> getMyWorkSchedules(Integer userId) {
        return workScheduleRepository.findByUserUserIdOrderByWorkDateAsc(userId);
    }

    @Override
    @Transactional
    public Request createShiftChangeRequest(Integer userId, ShiftChangeRequestDto dto) {
        // Validate: ca được chọn phải tồn tại trong WorkSchedules của user
        WorkSchedule existingSchedule = workScheduleRepository
                .findByUserUserIdAndWorkDateAndShiftShiftId(userId, dto.getWorkDate(), dto.getShiftId())
                .orElseThrow(() -> new IllegalArgumentException("Ca làm không tồn tại trong lịch của bạn"));

        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên"));

        // Find shift details
        Shift currentShift = shiftRepository.findById(dto.getShiftId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ca hiện tại"));

        Shift targetShift = shiftRepository.findById(dto.getTargetShiftId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy ca muốn đổi"));

                if (currentShift.getShiftId().equals(targetShift.getShiftId())) {
                        throw new IllegalArgumentException("Ca muốn đổi phải khác ca hiện tại");
                }

                if (workScheduleRepository.existsByUserUserIdAndWorkDateAndShiftShiftId(userId, dto.getWorkDate(), dto.getTargetShiftId())) {
                        throw new IllegalArgumentException("Bạn đã có ca muốn đổi trong ngày này");
                }

        // Create shift change request
        // Note: For SHIFT_CHANGE requests, fromDate and toDate are set to workDate
        // to satisfy database NOT NULL constraints
        Request request = Request.builder()
                .user(user)
                .type(CCPTMT.DoAn.QuanLyCaLam.entity.enums.RequestType.SHIFT_CHANGE)
                .fromDate(dto.getWorkDate())  // Set to workDate
                .toDate(dto.getWorkDate())    // Set to workDate
                .workDate(dto.getWorkDate())
                .shift(currentShift)
                .targetShift(targetShift)
                .reason(dto.getReason())
                .status(RequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        return requestRepository.save(request);
    }
}