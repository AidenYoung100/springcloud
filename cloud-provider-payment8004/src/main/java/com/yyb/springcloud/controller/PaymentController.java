package com.yyb.springcloud.controller;

import cn.hutool.core.lang.UUID;
import com.yyb.springcloud.entity.CommonResult;
import com.yyb.springcloud.entity.Payment;
import com.yyb.springcloud.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/payment")
@Slf4j
public class PaymentController {
    @Resource
    private PaymentService paymentService;

    @Resource
    private DiscoveryClient discoveryClient;

    @Value("${server.port}")
    private String serverPort;

    @PostMapping("/addPayment")
    public CommonResult addPayment(@RequestBody Payment payment) {
        int i = paymentService.addPayment(payment);
        if (i > 0) {
            return new CommonResult(200, "添加成功,端口号：" + serverPort);
        } else {
            return new CommonResult(500, "添加失败");

        }
    }

    @GetMapping("/getPaymentById/{id}")
    public CommonResult getPaymentById(@PathVariable("id") Long id) {
        Payment payment = paymentService.getPaymentById(id);
        if (payment != null) {
            return new CommonResult(200, "查询成功,端口号：" + serverPort, payment);
        } else {
            return new CommonResult(500, "查询失败");
        }
    }

    @GetMapping("/discovery")
    public Object discovery() {

        List<String> services = discoveryClient.getServices();
        for (String service : services) {
            log.info(service);
        }
        List<ServiceInstance> instances = this.discoveryClient.getInstances("CLOUD-PAYMENT-SERVICE");
        for (ServiceInstance instance : instances) {
            log.info("\n" + instance.getServiceId() + "\t" + instance.getHost() + "\t" + instance.getPort());
        }
        return this.discoveryClient;
    }

    @GetMapping("/getZk")
    public String getZk() {

        return "zookeeper:" + serverPort + "\t" + UUID.randomUUID().toString();
    }
}
