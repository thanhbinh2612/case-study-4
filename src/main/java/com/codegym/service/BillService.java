package com.codegym.service;

import com.codegym.model.Bill;
import com.codegym.model.BillDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BillService {
    Page<Bill> findAll(Pageable pageable);

    void save(Bill bill);

    Bill editById(Long id);

    void deleteById(Long id);

    Bill findByBillId(Long id);

//    List<Bill> findFirst6ByType(String type);
//
//    Page<Bill> findAllByType(String type, Pageable pageable);
}