package com.deepak.codechallenge;

import com.deepak.codechallenge.controller.TransactionController;
import com.deepak.codechallenge.domain.StatisticsDTO;
import com.deepak.codechallenge.domain.TransactionRequest;
import com.deepak.codechallenge.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

//@RunWith(SpringRunner.class)
@RunWith(MockitoJUnitRunner.class)
@WebMvcTest(TransactionController.class)
public class TransactionControllerTest {

    @Autowired
    private MockMvc  mvc;

    @Mock
    TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private JacksonTester<StatisticsDTO> jsonStatisticsDTO;
    private JacksonTester<TransactionRequest> transactionRequest;


    @Before
    public void setup(){
        JacksonTester.initFields(this, new ObjectMapper());
        mvc = MockMvcBuilders.standaloneSetup(transactionController)
                .build();
    }

    @Test
    public void getStatisticsEmptyTest() throws Exception {

        //Given
        given(transactionService.getStatistics())
                .willReturn(new StatisticsDTO("0.00", "0.00", "0.00","0.00", 0));
        //When
        MockHttpServletResponse response = mvc.perform(get("/statistics").accept(MediaType.APPLICATION_JSON)).andReturn().getResponse();

        //then
        System.out.println("response="+response.getContentAsString());
        assertEquals(response.getStatus(), HttpStatus.OK.value());
        assertEquals(response.getContentAsString(), jsonStatisticsDTO.write(new StatisticsDTO("0.00", "0.00", "0.00","0.00", 0)).getJson());

    }


    @Test
    public void createTransactionTestInvalidRequest() throws Exception {

        //Given
        /*given(transactionService.getStatistics())
                .willReturn(new StatisticsDTO("0.00", "0.00", "0.00","0.00", 0));*/
        //When
        MockHttpServletResponse response = mvc.perform(
                post("/transactions").
                contentType(MediaType.APPLICATION_JSON).
                content(
                        transactionRequest.write(new TransactionRequest("24.65",null)).getJson()
                )).
                andReturn().getResponse();

        //then
        System.out.println("response="+response.getContentAsString());
        assertEquals(response.getStatus(), HttpStatus.BAD_REQUEST.value());

    }

    @Test
    public void createTransactionTestInValidDateFormat() throws Exception {

        //Given
        /*given(transactionService.getStatistics())
                .willReturn(new StatisticsDTO("0.00", "0.00", "0.00","0.00", 0));*/
        //When
        MockHttpServletResponse response = mvc.perform(
                post("/transactions").
                        contentType(MediaType.APPLICATION_JSON).
                        content(
                                transactionRequest.write(new TransactionRequest("24.65","2018-10-20T22:20:51.312AAAAAAAAAAAAAZ")).getJson()
                        )).
                andReturn().getResponse();

        //then
        assertEquals(response.getStatus(), HttpStatus.UNPROCESSABLE_ENTITY.value());

    }

    @Test
    public void createTransactionTestInValidAmountFormat() throws Exception {

        //Given
        /*given(transactionService.getStatistics())
                .willReturn(new StatisticsDTO("0.00", "0.00", "0.00","0.00", 0));*/
        //When
        MockHttpServletResponse response = mvc.perform(
                post("/transactions").
                        contentType(MediaType.APPLICATION_JSON).
                        content(
                                transactionRequest.write(new TransactionRequest("24.65$","2018-10-20T22:20:51.312AAAAAAAAAAAAAZ")).getJson()
                        )).
                andReturn().getResponse();

        //then
        assertEquals(response.getStatus(), HttpStatus.UNPROCESSABLE_ENTITY.value());

    }
}
