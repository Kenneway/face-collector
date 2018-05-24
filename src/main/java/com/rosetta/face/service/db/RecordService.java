package com.rosetta.face.service.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rosetta.face.domain.Record;
import com.rosetta.face.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.List;

@Service
public class RecordService {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private RecordRepository recordRepo;

    public List<Record> getAll() {
        return recordRepo.findAll();
    }

    public Record addRecord(Record record) {
        return recordRepo.save(record);
    }

    public void delRecord(int id) {
        recordRepo.deleteById(id);
    }

    public void setRecord(Record record) {
        recordRepo.save(record);
    }

    public Record getRecord(int id) {
        return recordRepo.getOne(id);
    }



//    @Scheduled(fixedRate = 2000)
    private void test() throws JsonProcessingException {
        Record record = new Record();
        record.setPersonId(12223);
        record.setCameraId("02");
        record.setCreateTime(System.currentTimeMillis());

        Date d=new Date();//获取时间
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");//转换格式
        System.out.println(sdf.format(d));//打印

        java.sql.Date date = new java.sql.Date(System.currentTimeMillis());
        System.out.println(date);
        record.setDate(date);

        java.sql.Date date1 = new java.sql.Date(System.currentTimeMillis());
        System.out.println(date1);

        if (date == date1) System.out.println("date == date1");

        recordRepo.save(record);
        System.out.println(mapper.writeValueAsString(record));

    }

}
