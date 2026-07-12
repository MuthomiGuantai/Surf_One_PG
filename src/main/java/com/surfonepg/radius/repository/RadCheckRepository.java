package com.surfonepg.radius.repository;

import com.surfonepg.radius.entity.RadCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RadCheckRepository extends JpaRepository<RadCheck, Long> {

    @Modifying
    @Query("DELETE FROM RadCheck r WHERE r.username = :username")
    void deleteByUsername(@Param("username") String username);
}
