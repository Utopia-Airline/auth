package com.ss.utopia.auth.dao;


import com.ss.utopia.auth.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserDao extends JpaRepository<User, Long> {
  Optional<User> findByUsername(String username);
  
  @Query("FROM User u WHERE (:username is null or username LIKE %:username%) and (:email is null or email LIKE %:email%) and (:role is null or role_id = :role)")
  Page<User> findAll(@Param("username")String username, @Param("email")String email, @Param("role") int role, Pageable pageable);
  
  @Query("FROM User u WHERE (:username is null or username LIKE %:username%) and (:email is null or email LIKE %:email%)")
  Page<User> findAll(@Param("username")String username, @Param("email")String email, Pageable pageable);
  
  @Query("FROM User u WHERE (:username is null or username LIKE %:username%) and (:email is null or email LIKE %:email%) and (:role is null or role_id = :role)")
  List<User> findAll(@Param("username")String username, @Param("email")String email, @Param("role") int role, Sort sort);
  
  @Query("FROM User u WHERE (:username is null or username LIKE %:username%) and (:email is null or email LIKE %:email%)")
  List<User> findAll(@Param("username")String username, @Param("email")String email, Sort sort);
}
