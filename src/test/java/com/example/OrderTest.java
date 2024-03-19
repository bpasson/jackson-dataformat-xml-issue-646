package com.example;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OrderTest {

    private XmlMapper mapper;

    @BeforeEach
    public void init() {
        mapper = XmlMapper.builder()
                .defaultUseWrapper(false)
                .build();
    }

    @Test
    public void shouldDeserialize() throws Exception {
        Order order = mapper.readValue(OrderTest.class.getResourceAsStream("/order.xml"), Order.class);

        Assertions.assertNotNull(order);
    }
}
