package com.keanusantos.personalfinancemanager.domain.payment;

import com.keanusantos.personalfinancemanager.domain.payment.dto.request.CreatePaymentDTO;
import com.keanusantos.personalfinancemanager.domain.payment.dto.response.ResponsePaymentDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;


    @GetMapping
    public List<ResponsePaymentDTO> findAll() {
        return  paymentService.findAll();
    }

    @GetMapping(value = "/{id}")
    public ResponsePaymentDTO findById(@PathVariable Long id) {
        return paymentService.findById(id);
    }

    @PostMapping(value = "/installments/{installmentId}")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponsePaymentDTO insert(@PathVariable Long installmentId, @RequestBody @Valid CreatePaymentDTO dto) {
        return paymentService.insert(installmentId, dto);
    };

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        paymentService.delete(id);
    }
}
