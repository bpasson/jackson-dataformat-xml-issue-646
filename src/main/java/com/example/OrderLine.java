package com.example;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JsonDeserialize(builder = OrderLine.Builder.class)
@JsonIgnoreProperties(ignoreUnknown = true)
@JacksonXmlRootElement(localName = "line")
public class OrderLine {

    private final Long id;
    private final String description;

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("description")
    String description() {
        return description;
    }

    private OrderLine(Long id, String description) {
        this.id = id;
        this.description = description;
    }

    public Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Long id;
        private String description;

        @JsonProperty("id")
        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        @JsonProperty("description")
        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public OrderLine build() {
            return new OrderLine(id, description);
        }
    }
}
