package com.deepak.codechallenge.controller;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import javax.validation.Valid;

import com.deepak.codechallenge.domain.StatisticsDTO;
import com.deepak.codechallenge.domain.TransactionDTO;
import com.deepak.codechallenge.domain.TransactionRequest;
import com.deepak.codechallenge.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

//Commenting this annotation because it is not working in this IDE
//@Slf4j
@RestController
public class TransactionController {

    @Autowired
    public TransactionService transactionService;

    //In Ideal case @Sl4j gives a log variable which is not working and hence initating the logger explictly
    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);


    /*
     * method to post a transactions to save to a map
     **/
    @PostMapping("/transactions")
    public ResponseEntity<?> createTransaction(@Valid @RequestBody TransactionRequest transactionRequest ){
        logger.debug("start");

        //Basic mandatory Validation fails with @Valid annotation and returns 400

        TransactionDTO transactionDTO = getTransactionDTOFromRequest(transactionRequest);

        HttpHeaders responseHeaders = new HttpHeaders();

        if(transactionDTO==null){ //Some exception when parsing the amount and date fields
            return new ResponseEntity<String>("Parsing failed or transaction date is a future date", responseHeaders, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        boolean isTransactionOlderThan60Secs = transactionService.persistTransaction(transactionDTO);



        if(isTransactionOlderThan60Secs){
            //Return 204 response code
            return new ResponseEntity<String>("Successfully created the transaction", responseHeaders, HttpStatus.NO_CONTENT);
        }
        else{
            // Response code 201
            return new ResponseEntity<String>("Successfully created the transaction", responseHeaders, HttpStatus.CREATED);
        }

    }

    /*
     * method to map request to the DTO
     **/
    private TransactionDTO getTransactionDTOFromRequest(TransactionRequest transactionRequest){
        logger.debug("getTransactionDTOFromRequest method, transactionRequest= {}",transactionRequest );

        //TODO can use builder
        TransactionDTO transactionDTO = new TransactionDTO();
        BigDecimal amount = null;
        ZonedDateTime zonedDateTime=null;

        try{
            amount = new BigDecimal(transactionRequest.getAmount());
            zonedDateTime = ZonedDateTime.parse(transactionRequest.getTimestamp());
        }catch(Exception exception){
            //Any parsing exceptions we return null object and return the valid code in the controller
            //We can either use the Controller Advise and do the response re direction with the required status code
            logger.error("Parsing Exception , e ="+exception);
            return null;
        }

        ZonedDateTime zonedDateTimeNow = ZonedDateTime.now().withZoneSameLocal(ZoneId.of("Z"));

        //If it is a transaction future date, return 422 status code
        if(!StringUtils.isEmpty(zonedDateTime) && zonedDateTime.isAfter(zonedDateTimeNow)){
            logger.error("Future transaction dates are not allowed!");
             return null;
        }

        transactionDTO.setAmount(amount);
        transactionDTO.setTimeStamp(zonedDateTime);

        return transactionDTO;

    }


    /*
     * method to get statistics of transactions from the map
     **/
    @GetMapping("/statistics")
    public ResponseEntity<?> getStatistics(){
        logger.debug("start");

        StatisticsDTO statisticsDTO = transactionService.getStatistics();

        return new ResponseEntity<>(statisticsDTO, HttpStatus.OK);
    }


    /*
     * method to get a statistics of transactions from the map
     **/
    @DeleteMapping("/transactions")
    public ResponseEntity<?> deleteTransactions(){
        logger.debug("start");

        transactionService.deleteTransactions();

        return new ResponseEntity<>("Deleted", HttpStatus.NO_CONTENT);
    }
}

