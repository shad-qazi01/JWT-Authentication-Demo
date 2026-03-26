package com.smartbiz.client;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<ClientEntity, Long> {

    List<ClientEntity> findByOwnerId(Long ownerId);

    Optional<ClientEntity> findByIdAndOwnerId(Long id, Long ownerId);
}
