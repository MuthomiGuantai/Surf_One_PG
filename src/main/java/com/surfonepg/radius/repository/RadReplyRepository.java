package com.surfonepg.radius.repository;

import com.surfonepg.radius.entity.RadReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RadReplyRepository extends JpaRepository<RadReply, Long> {

    @Modifying
    @Query("DELETE FROM RadReply r WHERE r.username = :username")
    void deleteByUsername(@Param("username") String username);
}
