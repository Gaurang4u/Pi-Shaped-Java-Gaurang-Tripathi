package com.pishaped.circuit.breaker.controlllers;

import com.pishaped.circuit.breaker.services.PaymentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/process")
    public String processPayment(@RequestParam(defaultValue = "false") boolean fail,
                                 @RequestParam(defaultValue = "false") boolean delay) {
        return paymentService.processPayment(fail, delay);
    }
}