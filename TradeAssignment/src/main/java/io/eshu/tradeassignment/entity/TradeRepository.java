package io.eshu.tradeassignment.entity;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

public interface TradeRepository extends CrudRepository<Trade, Long>{

	List<Trade> findByExpired(char c);

	boolean existsByTradeId(String tradeId);

	Trade findByTradeId(String tradeId);
}