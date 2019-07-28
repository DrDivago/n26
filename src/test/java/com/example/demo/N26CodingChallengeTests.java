package com.example.demo;

import com.example.demo.cache.StatisticsCache;
import com.example.demo.cache.StatisticsCacheImpl;
import com.example.demo.model.Statistics;
import com.example.demo.model.Transaction;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;

import static org.awaitility.Awaitility.await;

@SuppressWarnings("SpellCheckingInspection")
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class N26CodingChallengeTests {

    @Autowired
    private MockMvc mvc;

    private ObjectMapper objectMapper;


    @Before
    public void setUp() {
        Jackson2ObjectMapperBuilder.json().featuresToDisable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
    }

    private String createJson(String amount, LocalDateTime timeTransaction) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        String timestamp = formatter.format(timeTransaction);

        return "{\"amount\":\""+amount+"\",\"timestamp\":\""+timestamp+"\"}";
    }

    @Test
    public void post_transaction_valid() throws Exception {
        String uri = "/transactions";

        LocalDateTime now = LocalDateTime.now().minusSeconds(1);
        String inputJson = createJson("10.334", now);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        Assert.assertEquals(HttpStatus.CREATED.value(), status);
    }

    @Test
    public void post_transaction_older_then_sixty_seconds() throws Exception {
        String uri = "/transactions";

        LocalDateTime now = LocalDateTime.now().minusSeconds(120);
        String inputJson = createJson("10.334", now);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        Assert.assertEquals(HttpStatus.NO_CONTENT.value(), status);
    }

    @Test
    public void post_transaction_invalid_json() throws Exception {
        String uri = "/transactions";

        String amount = "10.3";
        String timestamp = "fkd";

        String inputJson = "{\"amoukjnt\":\""+amount+"\",\"timjjestamp\":\""+timestamp+"\"}";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), status);
    }

    @Test
    public void post_transaction_not_parsable_amount() throws Exception {
        String uri = "/transactions";

        LocalDateTime now = LocalDateTime.now().minusSeconds(120);
        String inputJson = createJson("10.334y", now);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        Assert.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), status);
    }

    @Test
    public void post_transaction_not_parsable_timestamp() throws Exception {
        String uri = "/transactions";

        LocalDateTime less = LocalDateTime.now().minusSeconds(120);
        String timestamp = less.toString() + "kljj";
        String inputJson=  "{\"amount\":\"10.3\",\"timestamp\":\""+timestamp+"\"}";

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        Assert.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), status);
    }

    @Test
    public void post_transaction_in_future() throws Exception {
        String uri = "/transactions";

        LocalDateTime transactionInFuture = LocalDateTime.now().plusMinutes(1);
        String inputJson=  createJson("11.04", transactionInFuture);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(inputJson)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        Assert.assertEquals(HttpStatus.UNPROCESSABLE_ENTITY.value(), status);
    }

    @Test
    public void postTransaction() throws Exception {
        String uri = "/transactions";
        Transaction transaction = new Transaction();
        LocalDateTime validDate = LocalDateTime.now().minusSeconds(30);

        transaction.setAmount(new BigDecimal("10.3"));
        transaction.setTimestamp(validDate);

        String json = objectMapper.writeValueAsString(transaction);

        MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(uri)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(json)).andReturn();

        int status = mvcResult.getResponse().getStatus();
        Assert.assertEquals(HttpStatus.CREATED.value(), status);
        delete_transactions();
    }

    @Test
    public void postTransactionPastDate() throws Exception {
        String uri = "/transactions";

        LocalDateTime localDateTime = LocalDateTime.now().minusMinutes(2);

        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal("10.3"));
        transaction.setTimestamp(localDateTime);

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

        Statistics  statistics = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Statistics.class);

        Assert.assertEquals(statistics.getSum().doubleValue(), 0, 0.001);
        Assert.assertEquals(statistics.getAvg().doubleValue(), 0, 0.001);
        Assert.assertEquals(statistics.getMin().doubleValue(), Double.MAX_VALUE, 0.001);
        Assert.assertEquals(statistics.getMax().doubleValue(), Double.MIN_VALUE, 0.001);
        Assert.assertEquals(0, statistics.getCount(),0.001);
    }

    @Test
    public void getStatistics_one_transaction() throws Exception {
        String uriTransaction = "/transactions";

        LocalDateTime localDateTime = LocalDateTime.now().minusSeconds(30);
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal(12.3343));
        transaction.setTimestamp(localDateTime);

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

        Statistics statistics = objectMapper.readValue(mvcResultStatistics.getResponse().getContentAsString(), Statistics.class);

        Assert.assertEquals(statistics.getSum().doubleValue(), 12.33, 0.01);
        Assert.assertEquals(statistics.getAvg().doubleValue(), 12.33, 0.01);
        Assert.assertEquals(statistics.getMin().doubleValue(), 12.33, 0.01);
        Assert.assertEquals(statistics.getMax().doubleValue(), 12.33, 0.01);
        Assert.assertEquals(1, statistics.getCount());
        delete_transactions();
    }

    @Test
    public void getStatistics_two_transaction_same_time() throws Exception {
        String uriTransaction = "/transactions";

        LocalDateTime localDateTime = LocalDateTime.now().minusSeconds(30);
        Transaction transaction = new Transaction();
        transaction.setAmount(new BigDecimal(12.3388));
        transaction.setTimestamp(localDateTime);

        Transaction transaction1 = new Transaction();
        transaction1.setAmount(new BigDecimal(10.2573));
        transaction1.setTimestamp(localDateTime);

        String json1 = objectMapper.writeValueAsString(transaction);

        MvcResult mvcResultTransaction = mvc.perform(MockMvcRequestBuilders.post(uriTransaction)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(json1)).andReturn();

        int statusTransaction = mvcResultTransaction.getResponse().getStatus();
        Assert.assertEquals(HttpStatus.CREATED.value(), statusTransaction);

        String json2 = objectMapper.writeValueAsString(transaction1);
        MvcResult mvcResultTransaction2 = mvc.perform(MockMvcRequestBuilders.post(uriTransaction)
                .contentType(MediaType.APPLICATION_JSON_VALUE).content(json2)).andReturn();

        int statusTransaction2 = mvcResultTransaction2.getResponse().getStatus();
        Assert.assertEquals(HttpStatus.CREATED.value(), statusTransaction2);

        String uriStatistics = "/statistics";

        MvcResult mvcResultStatistics = mvc.perform(MockMvcRequestBuilders.get(uriStatistics).contentType(MediaType.APPLICATION_JSON)).
                andReturn();

        int statusStatistics = mvcResultStatistics.getResponse().getStatus();
        Assert.assertEquals(HttpStatus.OK.value(), statusStatistics);

        Statistics  statistics = objectMapper.readValue(mvcResultStatistics.getResponse().getContentAsString(), Statistics.class);

        Assert.assertEquals(22.60, statistics.getSum().doubleValue(),0.001);
        Assert.assertEquals( 11.30, statistics.getAvg().doubleValue(), 0.001);
        Assert.assertEquals(10.26,  statistics.getMin().doubleValue(), 0.001);
        Assert.assertEquals(12.34, statistics.getMax().doubleValue(), 0.001);
        Assert.assertEquals(2, statistics.getCount());
        delete_transactions();
    }

    @Test
    public void delete_transactions() throws Exception {
        String uriTransaction = "/transactions";

        MvcResult mvcResultTransaction = mvc.perform(MockMvcRequestBuilders.delete(uriTransaction)).andReturn();
        int statusTransaction = mvcResultTransaction.getResponse().getStatus();
        Assert.assertEquals(HttpStatus.NO_CONTENT.value(), statusTransaction);
    }

    @Test
    public void add_same_transaction_time() {

        StatisticsCache<Transaction, Statistics> statisticsCache = new StatisticsCacheImpl();

        LocalDateTime localDateTime = LocalDateTime.now();

        int size = 10000;

        Transaction[] t = new Transaction[size];
        for (int i = 0; i < size; i++) {
            t[i] = new Transaction();
            t[i].setAmount(BigDecimal.valueOf(10));
            t[i].setTimestamp(localDateTime);
        }

        for (int i = 0; i < size; i++) {
            int j = i;
            new Thread(() -> statisticsCache.add(t[j])).start();
        }

        await().until(getStatisticsSize(statisticsCache, size, 10*size));

    }

    @Test
    public void add_same_transaction_different_time()  {

        StatisticsCache<Transaction, Statistics> statisticsCache = new StatisticsCacheImpl();

        LocalDateTime localDateTime = LocalDateTime.now();
        int size = 60;

        Transaction[] t = new Transaction[size];
        for (int i = 0; i < size; i++) {
            localDateTime = localDateTime.minusSeconds(1);
            t[i] = new Transaction();
            t[i].setAmount(BigDecimal.valueOf(10));
            t[i].setTimestamp(localDateTime);
        }

        for (int i = 0; i < size; i++) {
            int j = i;
            new Thread(() -> statisticsCache.add(t[j])).start();
        }

        await().until(getStatisticsSize(statisticsCache, size, 10*size));
    }

    @Test
    public void add_get_transaction()  {

        StatisticsCache<Transaction, Statistics> statisticsCache = new StatisticsCacheImpl();

        LocalDateTime localDateTime = LocalDateTime.now();
        int size = 10000;

        Transaction[] t = new Transaction[size];
        for (int i = 0; i < size; i++) {
            t[i] = new Transaction();
            t[i].setAmount(BigDecimal.valueOf(10));
            t[i].setTimestamp(localDateTime);
        }

        for (int i = 0; i < size; i++) {
            int j = i;
            new Thread(() -> statisticsCache.mapReduce(x ->true));
            new Thread(() -> statisticsCache.add(t[j])).start();
        }

        await().until(getStatisticsSize(statisticsCache, size, 10*size));
    }


    private Callable<Boolean> getStatisticsSize(StatisticsCache<Transaction, Statistics> statisticsCache, int size, double expectedSum) {
        return () -> statisticsCache.mapReduce(x ->true).getCount() == size
                && statisticsCache.mapReduce(x ->true).getSum().doubleValue() == expectedSum;
    }
}
