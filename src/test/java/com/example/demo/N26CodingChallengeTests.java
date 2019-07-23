package com.example.demo;

import com.example.demo.model.Statistics;
import com.example.demo.model.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.deploy.net.HttpResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class N26CodingChallengeTests {

    @Autowired
    private MockMvc mvc;


    @Test
    public void postTransactionInvalidInput() throws Exception {
        String uri = "/transactions";

        String inputJson = "{\"amount\":\"10.3\",\"timestamp\":\"1563703754\"}";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        Assert.assertEquals(HttpStatus.CREATED.value(), status);
    }

    @Test
    public void postTransaction() throws Exception {
        String uri = "/transactions";
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal("10.3"));
        //transaction.setTimestamp(new Date());

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(transaction);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(json)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        Assert.assertEquals(HttpStatus.CREATED.value(), status);
    }

    @Test
    public void postTransactionPastDate() throws Exception {
        String uri = "/transactions";
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal("10.3"));
        //transaction.setTimestamp(new Date());

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(transaction);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(json)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        Assert.assertEquals(HttpStatus.NO_CONTENT.value(), status);
    }

    @Test
    public void getStatistics() throws Exception {
        String uri = "/statistics";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(uri).contentType(MediaType.APPLICATION_JSON)).
                andReturn();

        int status = mvcResult.getResponse().getStatus();
        Assert.assertEquals(HttpStatus.OK.value(), status);

        ObjectMapper objectMapper = new ObjectMapper();
        Statistics  statistics = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Statistics.class);

        //Assert.assertEquals(statistics.getSum(), 0, 0.001);
       // Assert.assertEquals(statistics.getAvg(), 0, 0.001);
        //Assert.assertEquals(statistics.getMin(), 0, 0.001);
        //Assert.assertEquals(statistics.getMax(), 0, 0.001);
        Assert.assertEquals(statistics.getCount(), 0, 0.001);
    }

    @Test
    public void getStatistics_one_transaction() throws Exception {


        String uriTransaction = "/transactions";
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal(10.3));
        //transaction.setTimestamp(new Date());

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(transaction);

        MvcResult mvcResultTransaction = mvc.perform(MockMvcRequestBuilders.post(uriTransaction)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(json)).andReturn();

        int statusTransaction = mvcResultTransaction.getResponse().getStatus();
        Assert.assertEquals(HttpStatus.CREATED.value(), statusTransaction);

        String uriStatistics = "/statistics";

        MvcResult mvcResultStatistics = mvc.perform(MockMvcRequestBuilders.get(uriStatistics).contentType(MediaType.APPLICATION_JSON)).
                andReturn();

        int statusStatistics = mvcResultStatistics.getResponse().getStatus();
        Assert.assertEquals(HttpStatus.OK.value(), statusStatistics);

        Statistics  statistics = objectMapper.readValue(mvcResultStatistics.getResponse().getContentAsString(), Statistics.class);

        //Assert.assertEquals(statistics.getSum(), 10.3, 0.001);
       // Assert.assertEquals(statistics.getAvg(), 10.3, 0.001);
      //  Assert.assertEquals(statistics.getMin(), 10.3, 0.001);
       // Assert.assertEquals(statistics.getMax(), 10.3, 0.001);
        Assert.assertEquals(statistics.getCount(), 1);
    }
}
