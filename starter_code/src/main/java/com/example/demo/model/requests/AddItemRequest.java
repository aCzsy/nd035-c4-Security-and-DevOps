package com.example.demo.model.requests;


import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class AddItemRequest {
    @JsonProperty
    @NotNull
    private String name;
    @JsonProperty
    @NotNull
    private BigDecimal price;
    @JsonProperty
    @NotNull
    private String description;

    public AddItemRequest(String name, BigDecimal price, String description) {
        this.name = name;
        this.price = price;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
