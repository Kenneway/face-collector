package com.rosetta.face.service.cache;

import com.rosetta.face.domain.Face;
import com.rosetta.face.domain.Record;
import com.rosetta.face.service.db.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EnableAsync
@EnableScheduling
@Service
public class RecordFaceCacheService {

    private RecordService recordService;

    private volatile Map<Integer, Face> cachedFaces = new ConcurrentHashMap<>();

    private Long flushDelay = 10000l;

    @Autowired
    public RecordFaceCacheService(RecordService recordService) {
        this.recordService = recordService;
        flushExpired();
    }

    @Async
    @Scheduled(fixedRate = 10000)
    protected void flushExpired() {
        if (cachedFaces.size() == 0) return;
        for (Map.Entry<Integer, Face> entry : cachedFaces.entrySet()) {
            Face face = entry.getValue();
            Long updateTime = face.getUpdateTime();
            if (updateTime == null) cachedFaces.remove(entry.getKey());
            if (System.currentTimeMillis()-updateTime > flushDelay) {
                recordFace(face);
                cachedFaces.remove(entry.getKey());
            }
        }
        System.out.println("RecogService: Cache Layer has been flushed.");
    }

    public boolean containsKey(Integer key) {
        return cachedFaces.containsKey(key);
    }

    public void put(Integer key, Face face) {
        cachedFaces.put(key, face);
    }

    private void recordFace(Face face) {
        Integer personId = face.getPersonId();
        if (personId < 0) return;
        Record record = new Record();
        record.setPersonId(personId);
        record.setCameraId(face.getCameraId());
        long now = System.currentTimeMillis();
        record.setCreateTime(now);
        record.setDate(new Date(now));
        recordService.addRecord(record);
    }

}
