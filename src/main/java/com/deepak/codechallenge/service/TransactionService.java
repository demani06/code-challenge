package com.deepak.codechallenge.service;

import com.deepak.codechallenge.domain.StatisticsDTO;
import com.deepak.codechallenge.domain.TransactionDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

//@Slf4j
@Service
public class TransactionService {


    //In Ideal case @Sl4j gives a log variable which is not working and hence initating the logger explictly
    private static final Logger logger = LoggerFactory.getLogger(TransactionService.class);

    //ConcurrentHashMap is chosen to avoid multithreading problems
    static Map<Long,TransactionDTO> transactionMap= new ConcurrentHashMap<>();

    public boolean persistTransaction(TransactionDTO transactionDTO){
        logger.info("persistTransaction,transactionDTO ={} ",transactionDTO);

        java.util.Random random = new java.util.Random();

        transactionMap.put(random.nextLong(), transactionDTO);

        logger.info("Persisted to Map, ",transactionDTO.getTimeStamp());

        //TODO compare the timestamp with current time and return true only if the difference is less than 60
        Duration timeDiff = Duration.between(transactionDTO.getTimeStamp(), ZonedDateTime.now().withZoneSameLocal(ZoneId.of("Z")));
        logger.info("time difference between current time and the transaction time stamp ={}", timeDiff);
        boolean isTransactionOlderThan60Secs = false;
        if(timeDiff.getSeconds() >= 60){
            isTransactionOlderThan60Secs = true;
        }
        return isTransactionOlderThan60Secs;
    }


    public StatisticsDTO getStatistics(){
        logger.info("getStatistics method start");
        //Duration.between(zonedDateTime2, zonedDateTimeNow).getSeconds()
        long count = transactionMap.
                values().
                stream().
                filter(transactionDTO -> Duration.between(transactionDTO.getTimeStamp(), ZonedDateTime.now().withZoneSameLocal(ZoneId.of("Z"))).getSeconds()< 60).count();
        logger.info("count="+count);

        BigDecimal sum = transactionMap.
                values().
                stream().
                filter(transactionDTO -> Duration.between(transactionDTO.getTimeStamp(), ZonedDateTime.now().withZoneSameLocal(ZoneId.of("Z"))).getSeconds()< 60).
                map(transactionDTO -> transactionDTO.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);

        logger.info("sum="+sum);

        BigDecimal average = null;

        logger.info("");

        if(sum.doubleValue()> 0 && count > 0) {
            average = sum.divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);
            logger.info("average="+average);
        }

        TransactionDTO transactionDTOOptional= transactionMap.
                values().
                stream().
                filter(transactionDTO -> Duration.between(transactionDTO.getTimeStamp(), ZonedDateTime.now().withZoneSameLocal(ZoneId.of("Z"))).getSeconds()< 60).
                min(Comparator.comparing(transactionDTO-> transactionDTO.getAmount())).orElse(new TransactionDTO());

        BigDecimal min = null;
        if(!StringUtils.isEmpty(transactionDTOOptional.getAmount())){
            min = transactionDTOOptional.getAmount();
        }

        logger.info("min="+min);


        TransactionDTO transactionDTOOptionalForMAx = transactionMap.
                values().
                stream().
                filter(transactionDTO -> Duration.between(transactionDTO.getTimeStamp(), ZonedDateTime.now().withZoneSameLocal(ZoneId.of("Z"))).getSeconds()< 60).
                max(Comparator.comparing(transactionDTO-> transactionDTO.getAmount())).orElse(new TransactionDTO());

        BigDecimal max = null;
        if(!StringUtils.isEmpty(transactionDTOOptionalForMAx.getAmount())){
            max = transactionDTOOptionalForMAx.getAmount();
        }

        logger.info("max="+max);

        Set<TransactionDTO> resultSet = transactionMap.
                values().
                stream().
                filter(transactionDTO -> Duration.between(transactionDTO.getTimeStamp(), ZonedDateTime.now().withZoneSameLocal(ZoneId.of("Z"))).getSeconds()< 60).
                collect(Collectors.toSet());

        logger.info("resultSet ={}", resultSet);


        StatisticsDTO statisticsDTO = new StatisticsDTO();
        statisticsDTO.setCount(count);
        if(!StringUtils.isEmpty(average))
            statisticsDTO.setAvg(average.toString());
        if(!StringUtils.isEmpty(max))
            statisticsDTO.setMax(getStringInDecimalFormat(max));
        if(!StringUtils.isEmpty(min))
            statisticsDTO.setMin(getStringInDecimalFormat(min));
        if(!StringUtils.isEmpty(sum))
            statisticsDTO.setSum(getStringInDecimalFormat(sum));

        return statisticsDTO;

    }


    private static String getStringInDecimalFormat(BigDecimal bigDecimalInput){
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String strDouble = String.format("%.2f", 2.00023);
        //return decimalFormat.format(bigDecimalInput.doubleValue());
        return String.format("%.2f", bigDecimalInput.doubleValue());
    }


    public void deleteTransactions(){
        logger.info("Delete Transaction start");
        transactionMap.clear();
        logger.info("Map clear");
    }

    public int getMapSize(){
        logger.info("getMapSize");
        return transactionMap.size();
    }


}

