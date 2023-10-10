package com.no5619.umirecorder.repository;

import com.no5619.umirecorder.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
	Optional<Role> findByName(String name);
}
