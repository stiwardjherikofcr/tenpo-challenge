package cl.tenpo.sjcr.percentage_calculator_service;

import org.springframework.boot.SpringApplication;

public class TestPercentageCalculatorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(PercentageCalculatorServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
