package com.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@JsonDeserialize(builder = Order.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName="order")
public class Order {
    private final Long id;
    private final List<OrderLine> lines;

    private Order(Long id, List<OrderLine> lines) {
        this.id = id;
        this.lines = lines;
    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("line")
    List<OrderLine> getOrderLines() {
        return lines;
    }

    public Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private List<OrderLine> lines = new ArrayList<>();

        @JsonProperty("id")
        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        @JsonProperty("line")
        public Builder lines(Iterable<? extends OrderLine> lines) { // fails
        //public Builder lines(Collection<? extends OrderLine> lines) { // works
            this.lines.clear();
            for (OrderLine line : lines) {
                this.lines.add(line);
            }
            return this;
        }

        public Order build() {
            return new Order(id, lines);
        }
    }
}
