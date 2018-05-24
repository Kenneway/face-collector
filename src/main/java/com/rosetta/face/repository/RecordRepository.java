package com.rosetta.face.repository;

import com.rosetta.face.domain.Record;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<Record, Integer> {

}
