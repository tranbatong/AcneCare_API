package com.acnecare.acnecare_app_api.identity.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;
import com.acnecare.acnecare_app_api.identity.entity.InvalidatedToken;

@Repository
public interface InvalidatedRepository extends JpaRepository<InvalidatedToken, String> {
    List<InvalidatedToken> findAllByExpiryTimeBefore(Date now);
}
