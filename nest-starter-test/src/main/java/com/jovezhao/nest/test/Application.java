package com.jovezhao.nest.test;

import com.jovezhao.nest.activemq.ActiveMQChannelProvider;
import com.jovezhao.nest.activemq.ActiveMQProviderConfig;
import com.jovezhao.nest.ddd.event.*;
import com.jovezhao.nest.exception.CustomException;
import com.jovezhao.nest.log.Log;
import com.jovezhao.nest.log.LogAdapter;
import com.jovezhao.nest.test.api.TestDto;
import com.jovezhao.nest.test.api.UserService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by zhaofujun on 2017/6/17.
 */

@SpringBootApplication
@MapperScan("com.jovezhao.nest.test.repositories.mappers")
public class Application implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Autowired
    private UserService userService;

    @Override
    public void run(String[] args) throws Exception {
        ChannelProvider channelProvider = new ActiveMQChannelProvider();

        ActiveMQProviderConfig providerConfig = new ActiveMQProviderConfig();
        providerConfig.setBrokers("tcp://127.0.0.1:61616");
        channelProvider.setProviderConfig(providerConfig);
        channelProvider.init();

        EventConfigItem eventConfigItem = new EventConfigItem();
        eventConfigItem.setEventName("event1");
        eventConfigItem.setChannelProvider(channelProvider);
        EventConfigManager.put(eventConfigItem);


        EventBus.registerHandler(new TestHandler());
        EventBus.registerHandler(new TestHandler());


        userService.changeName("new 55");


    }

    public class TestHandler implements EventHandler<TestDto> {
        private Log log = new LogAdapter(this.getClass());

        @Override
        public String getEventName() {
            return "event1";
        }

        @Override
        public Class<TestDto> getTClass() {
            return TestDto.class;
        }

        @Override
        public void handle(TestDto data) throws Exception {
            log.info(data.getAbs());
            //System.out.println("fffffff::" + data.getAbs());
        }
    }
}
