package com.rosetta.face.repository;

import com.rosetta.face.domain.Face;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FaceRepository extends JpaRepository<Face, Long> {

    public List<Face> findByPersonId(Integer personId);

    public List<Face> findByCameraId(String cameraId);

    public List<Face> findByPersonIdAndCameraId(Integer personId, String cameraId);

    public List<Face> findByStatus(Integer status);


}
