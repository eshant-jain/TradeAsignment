package io.eshu.tradeassignment;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.eshu.tradeassignment.entity.Trade;
import io.eshu.tradeassignment.entity.TradeRepository;

@SpringBootApplication
public class TradeAssignmentApplication {

	public static void main(String[] args) {
		SpringApplication.run(TradeAssignmentApplication.class, args);
	}
	
	@Bean
	  public CommandLineRunner init(TradeRepository repository) {
	    return (args) -> {
	      repository.save(new Trade("T1", 1, "CP-1", "B1", "20/05/2020", "13/06/2020", 'N'));
	      repository.save(new Trade("T2", 2, "CP-2", "B1", "20/05/2021", "13/06/2020", 'N'));
	    };
	}

}
