package com.deepak.codechallenge.service;

import com.deepak.codechallenge.domain.TransactionDTO;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransactionServiceTest {

    @Autowired
    TransactionService transactionService;

    @After
    public void clearMap(){
        System.out.println("Clearing map after each test");
        //To clear the map after every test case
        transactionService.deleteTransactions();
    }

    @Test
    public void whenPersistTransactionOlderThan60Seconds(){

        TransactionDTO transactionDTO = new TransactionDTO();

        transactionDTO.setAmount(new BigDecimal("24.55"));
        ZonedDateTime zonedDateTime =  ZonedDateTime.now().withZoneSameLocal(ZoneId.of("Z")).minusMinutes(1);
        transactionDTO.setTimeStamp(zonedDateTime);
        boolean isTransactionOlderThan60Secs = transactionService.persistTransaction(transactionDTO);
        System.out.println("isTransactionOlderThan60Secs="+isTransactionOlderThan60Secs);
        assert isTransactionOlderThan60Secs==true;

    }


    @Test
    public void whenPersistTransactionLessThan60Seconds(){

        TransactionDTO transactionDTO = new TransactionDTO();

        transactionDTO.setAmount(new BigDecimal("45.55"));
        ZonedDateTime zonedDateTime =  ZonedDateTime.now().withZoneSameLocal(ZoneId.of("Z")).minusSeconds(30);
        transactionDTO.setTimeStamp(zonedDateTime);
        boolean isTransactionOlderThan60Secs = transactionService.persistTransaction(transactionDTO);
        System.out.println("isTransactionOlderThan60Secs="+isTransactionOlderThan60Secs);
        assert isTransactionOlderThan60Secs==false;

    }

    @Test
    public void whenDeleteTransactionsCalledAfterAddingATransaction(){
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setAmount(new BigDecimal("45.55"));
        ZonedDateTime zonedDateTime =  ZonedDateTime.now().withZoneSameLocal(ZoneId.of("Z")).minusSeconds(30);
        transactionDTO.setTimeStamp(zonedDateTime);
        transactionService.persistTransaction(transactionDTO);
        System.out.println("Map size after adding a transaction ="+ transactionService.getMapSize());
        assert transactionService.getMapSize() == 1;
        transactionService.deleteTransactions();
        System.out.println("Map size after deleting a transaction ="+ transactionService.getMapSize());
        assert transactionService.getMapSize() == 0;
    }

    @Test
    public void whenDeleteTransactionsCalledOnEmptyMap(){
        System.out.println("Map size on an empty map="+ transactionService.getMapSize());
        assert transactionService.getMapSize() == 0;
        transactionService.deleteTransactions();
        //Should not throw any exception
        System.out.println("Map size on an empty map="+ transactionService.getMapSize());
        assert transactionService.getMapSize() == 0;
    }

}
