package com.deepak.codechallenge.service;

import com.deepak.codechallenge.domain.StatisticsDTO;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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

        //Given
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setAmount(new BigDecimal("24.55"));
        ZonedDateTime zonedDateTime =  ZonedDateTime.now().withZoneSameLocal(ZoneId.of("Z")).minusMinutes(1);
        transactionDTO.setTimeStamp(zonedDateTime);
        //When
        boolean isTransactionOlderThan60Secs = transactionService.persistTransaction(transactionDTO);

        //then
        System.out.println("isTransactionOlderThan60Secs="+isTransactionOlderThan60Secs);
        assert isTransactionOlderThan60Secs==true;

    }

    @Test
    public void whenGetStatisticsOnEmptyDataSetInTheMap(){

       //Given an Empty Map
       transactionService.deleteTransactions();

       //when
       StatisticsDTO statisticsDTO =  transactionService.getStatistics();

       //then
       assertNotNull(statisticsDTO);
       assertEquals(statisticsDTO.getCount(), 0);
       assertEquals(statisticsDTO.getSum(), "0.00");
       assertEquals(statisticsDTO.getMin(), "0.00");
       assertEquals(statisticsDTO.getMax(), "0.00");
       assertEquals(statisticsDTO.getAvg(), "0.00");

    }


    @Test
    public void whenGetStatisticsOnSingleDataSetInTheMap(){

        //Given a Map with a record
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setAmount(new BigDecimal("45.55"));
        ZonedDateTime zonedDateTime =  ZonedDateTime.now().withZoneSameLocal(ZoneId.of("Z")).minusSeconds(20);
        transactionDTO.setTimeStamp(zonedDateTime);
        boolean isTransactionOlderThan60Secs = transactionService.persistTransaction(transactionDTO);

        //When
        StatisticsDTO statisticsDTO =transactionService.getStatistics();

        //Then
        System.out.println("statisticsDTO="+statisticsDTO);
        assertNotNull(statisticsDTO);
        assertEquals(statisticsDTO.getAvg(), "45.55");
        assertEquals(statisticsDTO.getSum(), "45.55");
        assertEquals(statisticsDTO.getMax(), "45.55");
        assertEquals(statisticsDTO.getMin(), "45.55");
        assertEquals(statisticsDTO.getCount(), 1);

    }

    @Test
    public void whenGetStatisticsWithCoupleOfRecordsInTheMap(){

        //Given a Map with a record
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setAmount(new BigDecimal("45.55"));
        ZonedDateTime zonedDateTime =  ZonedDateTime.now().withZoneSameLocal(ZoneId.of("Z")).minusSeconds(20);
        transactionDTO.setTimeStamp(zonedDateTime);

        TransactionDTO transactionDTO1 = new TransactionDTO();
        transactionDTO1.setAmount(new BigDecimal("50.45"));
        ZonedDateTime zonedDateTime1 =  ZonedDateTime.now().withZoneSameLocal(ZoneId.of("Z")).minusSeconds(10);
        transactionDTO1.setTimeStamp(zonedDateTime1);

        TransactionDTO transactionDTO2 = new TransactionDTO();
        transactionDTO2.setAmount(new BigDecimal("10.25"));
        ZonedDateTime zonedDateTime2 =  ZonedDateTime.now().withZoneSameLocal(ZoneId.of("Z")).minusSeconds(15);
        transactionDTO2.setTimeStamp(zonedDateTime2);

        transactionService.persistTransaction(transactionDTO);
        transactionService.persistTransaction(transactionDTO1);
        transactionService.persistTransaction(transactionDTO2);

        //When
        StatisticsDTO statisticsDTO =transactionService.getStatistics();

        //Then
        System.out.println("statisticsDTO="+statisticsDTO);
        assertNotNull(statisticsDTO);
        assertEquals(statisticsDTO.getAvg(), "35.42");
        assertEquals(statisticsDTO.getSum(), "106.25");
        assertEquals(statisticsDTO.getMax(), "50.45");
        assertEquals(statisticsDTO.getMin(), "10.25");
        assertEquals(statisticsDTO.getCount(), 3);

    }

    @Test
    public void whenGetStatisticsWithCoupleOfNegativeRecordsInTheMap(){

        //Given a Map with a record
        TransactionDTO transactionDTO = new TransactionDTO();
        transactionDTO.setAmount(new BigDecimal("-30.00"));
        ZonedDateTime zonedDateTime =  ZonedDateTime.now().withZoneSameLocal(ZoneId.of("Z")).minusSeconds(20);
        transactionDTO.setTimeStamp(zonedDateTime);

        TransactionDTO transactionDTO1 = new TransactionDTO();
        transactionDTO1.setAmount(new BigDecimal("50.00"));
        ZonedDateTime zonedDateTime1 =  ZonedDateTime.now().withZoneSameLocal(ZoneId.of("Z")).minusSeconds(10);
        transactionDTO1.setTimeStamp(zonedDateTime1);

        TransactionDTO transactionDTO2 = new TransactionDTO();
        transactionDTO2.setAmount(new BigDecimal("10.00"));
        ZonedDateTime zonedDateTime2 =  ZonedDateTime.now().withZoneSameLocal(ZoneId.of("Z")).minusSeconds(15);
        transactionDTO2.setTimeStamp(zonedDateTime2);

        transactionService.persistTransaction(transactionDTO);
        transactionService.persistTransaction(transactionDTO1);
        transactionService.persistTransaction(transactionDTO2);

        //When
        StatisticsDTO statisticsDTO =transactionService.getStatistics();

        //Then
        System.out.println("statisticsDTO="+statisticsDTO);
        assertNotNull(statisticsDTO);
        assertEquals(statisticsDTO.getAvg(), "10.00");
        assertEquals(statisticsDTO.getSum(), "30.00");
        assertEquals(statisticsDTO.getMax(), "50.00");
        assertEquals(statisticsDTO.getMin(), "-30.00");
        assertEquals(statisticsDTO.getCount(), 3);

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
