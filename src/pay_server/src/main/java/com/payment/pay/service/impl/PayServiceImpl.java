package com.payment.pay.service.impl;

import com.payment.pay.database.PayMapper;
import com.payment.pay.database.UserMapper;
import com.payment.pay.enums.TxnStatus;
import com.payment.pay.exception.*;
import com.payment.pay.model.dao.ReserveDAO;
import com.payment.pay.model.dao.TransactionDAO;
import com.payment.pay.model.request.Approve;
import com.payment.pay.model.request.Reserve;
import com.payment.pay.model.request.Temporary;
import com.payment.pay.model.response.ReserveRes;
import com.payment.pay.model.response.TemporaryRes;
import com.payment.pay.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.util.UUID;

@Service
public class PayServiceImpl implements PayService {

    @Autowired
    private PayMapper payMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    public ReserveRes reserve(Reserve reserve, Long merchantId) {

        if(merchantId == null)
            throw new EmptyHeaderException();

        reserve.setMerchantId(merchantId);

        payMapper.insertReserve(reserve);

        ReserveRes response = ReserveRes.builder()
                .reserveId(reserve.getReserveId())
                .build();

        return response;

    }

    @Override
    @Transactional
    public TemporaryRes temporary(Temporary temporary, Long merchantId) {

        if(merchantId == null)
            throw new EmptyHeaderException();

        temporary.setMerchantId(merchantId);

        ReserveDAO reserveDAO = payMapper.getReserve(temporary.getReserveId());

        if(reserveDAO == null)
            throw new InvalidReserveException();
        else if(!reserveDAO.getStatus().equals(TxnStatus.Reserve.getStatus()))
            throw new AlreadyStatusChangeException();
        else if(!temporary.isSameOrder(reserveDAO))
            throw new NotMatchReserveInfoException();

        int methodCheck = userMapper.userPaymentMethodCheck(temporary);

        if (methodCheck != 1)
            throw new InvalidPayMethodException();

        String transactionPw = userMapper.getTransactionPw(temporary);

        if(!transactionPw.equals(temporary.getTransactionPw()))
            throw new NotMatchTransactionPwException();

        String payId = UUID.randomUUID().toString();
        temporary.setPayId(payId);

        try {
            payMapper.updateReserveToTemporary(temporary.getReserveId());
            payMapper.insertTransaction(temporary);
        } catch (Exception e) {
            throw new RuntimeException();
        }

        TemporaryRes temporaryRes = TemporaryRes.builder()
                .payId(payId)
                .reserveId(temporary.getReserveId())
                .productName(temporary.getProductName())
                .amount(temporary.getAmount())
                .count(temporary.getCount())
                .methodType(temporary.getMethodType())
                .methodNum(temporary.getMethodNum())
                .userIdx(temporary.getUserIdx())
                .merchantId(temporary.getMerchantId())
                .build();

        return temporaryRes;

    }

    @Override
    @Transactional
    public void approve(Approve approve, Long merchantId) {

        if(merchantId == null)
            throw new EmptyHeaderException();

        TransactionDAO transaction = payMapper.getTransaction(approve);

        if(transaction == null)
            throw new InvalidPayIdException();
        else if(transaction.getStatus().equals(TxnStatus.Approve.getStatus()))
            throw new AlreadyPaidException();
        else if(transaction.getStatus().equals(TxnStatus.Cancel.getStatus()))
            throw new AlreadyCancelledException();
        else if(!transaction.getMerchantId().equals(merchantId))
            throw new NotMatchMerchantIdException();

        payMapper.approveTransaction(approve);

    }

}
