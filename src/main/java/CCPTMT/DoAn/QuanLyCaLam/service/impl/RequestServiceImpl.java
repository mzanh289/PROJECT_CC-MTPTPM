package CCPTMT.DoAn.QuanLyCaLam.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import CCPTMT.DoAn.QuanLyCaLam.dto.RequestCreateDto;
import CCPTMT.DoAn.QuanLyCaLam.entity.Request;
import CCPTMT.DoAn.QuanLyCaLam.entity.User;
import CCPTMT.DoAn.QuanLyCaLam.entity.enums.RequestStatus;
import CCPTMT.DoAn.QuanLyCaLam.repository.RequestRepository;
import CCPTMT.DoAn.QuanLyCaLam.repository.UserRepository;
import CCPTMT.DoAn.QuanLyCaLam.service.RequestService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public Request createRequest(Integer userId, RequestCreateDto dto) {
        // Validate date range
        if (!dto.isValidDateRange()) {
            throw new IllegalArgumentException("Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc");
        }

        // Find user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên"));

        // Create request
        Request request = Request.builder()
                .user(user)
                .type(dto.getType())
                .fromDate(dto.getFromDate())
                .toDate(dto.getToDate())
                .reason(dto.getReason())
                .status(RequestStatus.PENDING)
                .build();

        return requestRepository.save(request);
    }

    @Override
    public List<Request> getRequestsByUserId(Integer userId) {
        return requestRepository.findByUserUserId(userId);
    }
}