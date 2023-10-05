package com.example.oneday.repository;

import com.example.oneday.datamodel.Offsets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OffsetsRepository extends JpaRepository<Offsets, UUID> {

    @Query(value = "SELECT off.tempOffset FROM Offsets off where off.fromMeters <= :alt and off.toMeters > :alt")
    Optional<Integer> findByAltitude(final Integer alt);
}
