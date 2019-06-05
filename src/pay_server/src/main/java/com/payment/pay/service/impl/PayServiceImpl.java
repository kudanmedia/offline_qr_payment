package com.payment.pay.service.impl;

import com.payment.pay.database.PayMapper;
import com.payment.pay.database.UserMapper;
import com.payment.pay.enums.TxnStatus;
import com.payment.pay.exception.*;
import com.payment.pay.model.dao.ReserveDAO;
import com.payment.pay.model.request.Reserve;
import com.payment.pay.model.request.Temporary;
import com.payment.pay.model.response.ReserveRes;
import com.payment.pay.model.response.TemporaryRes;
import com.payment.pay.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        String payId = UUID.randomUUID().toString();
        temporary.setPayId(payId);

        payMapper.insertTransaction(temporary);

        TemporaryRes temporaryRes = TemporaryRes.builder()
                .payId(payId)
                .build();

        return temporaryRes;

    }

}
