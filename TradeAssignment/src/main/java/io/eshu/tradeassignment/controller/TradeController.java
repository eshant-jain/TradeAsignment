package io.eshu.tradeassignment.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.eshu.tradeassignment.constants.ChannelType;
import io.eshu.tradeassignment.entity.Trade;
import io.eshu.tradeassignment.entity.TradeRepository;
import io.eshu.tradeassignment.exceptions.InvalidMaturityDate;
import io.eshu.tradeassignment.exceptions.InvalidVersion;

@RestController
@RequestMapping("/api")
public class TradeController {

	@Autowired
	private TradeRepository repository;

	private static final Logger log = LoggerFactory.getLogger(TradeController.class);

	@GetMapping("/trades")
	public List<Trade> getTrades() {
		return (List<Trade>) repository.findAll();
	}

	@GetMapping("/expiredTrades")
	public List<Trade> getExpiredTrades() {
		return (List<Trade>) repository.findByExpired('N');
	}

	@GetMapping("/loadData/{fileName}")
	public List<String> loadDataFromFile(@PathVariable String fileName) {
		List<String> statusReport = new ArrayList<String>();
		try {
			Path path = Paths.get(getClass().getClassLoader().getResource(fileName + ".txt").toURI());
			List<String> loadData = Files.lines(path).collect(Collectors.toList());
			for (String line : loadData) {
				String[] split = line.split(",");
				String tradeId = split[0];
				int inputVersion = Integer.parseInt(split[1]);
				String counterPartyId = split[2];
				String bookId = split[3];
				String maturityDate = split[4];
				String createdDate = split[5];
				char expired = split[6].charAt(0);
				Trade trade = new Trade(tradeId, inputVersion, counterPartyId, bookId, maturityDate, createdDate,
						expired);
				statusReport.add(saveUpdate(trade, ChannelType.FILE));
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return statusReport;
	}

	private String saveUpdate(Trade trade, ChannelType channelType) {
		String tradeId = trade.getTradeId();
		int inputVersion = trade.getVersion();
		String counterPartyId = trade.getCounterPartyId();
		String bookId = trade.getBookId();
		String maturityDate = trade.getMaturityDate();
		String createdDate = trade.getCreatedDate();
		if (ifExists(tradeId)) {
			Trade existingTrade = repository.findByTradeId(tradeId);
			int dbVersion = existingTrade.getVersion();
			if (versionCheck(inputVersion, dbVersion)) {
				existingTrade.setBookId(bookId);
				existingTrade.setCounterPartyId(counterPartyId);
				existingTrade.setCreatedDate(createdDate);
				existingTrade.setMaturityDate(maturityDate);
				existingTrade.setVersion(inputVersion);
				if (!maturityCheck(maturityDate)) {
					existingTrade.setExpired('Y');
				}
				log.info("Updating trade");
				repository.save(existingTrade);
				return "Updating exixting trade " + tradeId;
			} else {
				log.info("Received trade {} version is lower than already saved trade", tradeId);
				if (ChannelType.API == channelType) {
					throw new InvalidVersion();
				} else {
					return tradeId + " version is lower than already saved trade";
				}

			}
		} else {
			if (maturityCheck(maturityDate)) {
				log.info("Saving trade");
				repository.save(trade);
				return "New trade created " + tradeId;
			} else {
				log.info("Trade already expired");
				if (ChannelType.API == channelType) {
					throw new InvalidMaturityDate();
				} else {
					return "Trade already expired " + tradeId;
				}

			}

		}
	}

	private boolean ifExists(String tradeId) {
		return repository.existsByTradeId(tradeId);
	}

	private boolean versionCheck(int inputVersion, int dbVersion) {
		return (inputVersion == dbVersion || inputVersion > dbVersion);
	}

	private boolean maturityCheck(String maturityDate) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu");
		// String formattedDate = LocalDateTime.now().format(formatter);
		String format = LocalDateTime.now().format(formatter);
		boolean validMaturity = LocalDate.parse(format, formatter).isBefore(LocalDate.parse(maturityDate, formatter));
		return validMaturity;
	}

	@PostMapping("/trades")
	public String newTrade(@RequestBody Trade newTrade) {
		return saveUpdate(newTrade, ChannelType.API);

	}

	@GetMapping("/refreshFlags")
	public String refreshFlags() {
		// toDo
		return "Trade saved with id ";
	}

	// ToDo
	// add a logic to check file upload and return stats also skip the invalid
	// result and add it in list

}
