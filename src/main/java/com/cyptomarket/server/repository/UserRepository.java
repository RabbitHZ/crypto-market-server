package com.cyptomarket.server.repository;

import com.cyptomarket.server.entity.Order;
import com.cyptomarket.server.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface UserRepository extends JpaRepository<User, Long> {

}
