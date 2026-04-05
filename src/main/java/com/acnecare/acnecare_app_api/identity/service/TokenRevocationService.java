package com.acnecare.acnecare_app_api.identity.service;

import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.acnecare.acnecare_app_api.identity.entity.InvalidatedToken;
import com.acnecare.acnecare_app_api.identity.repository.InvalidatedRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TokenRevocationService {

    InvalidatedRepository invalidatedRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void revokeImmediately(String jti, Date expiryTime) {
        invalidatedRepository.save(
                InvalidatedToken.builder().id(jti).expiryTime(expiryTime).build());
    }
}
