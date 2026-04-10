package CCPTMT.DoAn.QuanLyCaLam.service;

import java.util.Optional;

import CCPTMT.DoAn.QuanLyCaLam.dto.SessionUserDto;

public interface AuthService {

    Optional<SessionUserDto> authenticate(String email, String rawPassword);
}
