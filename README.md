# Jackson Databind XML with builder class issue

This is a reproducer for https://github.com/FasterXML/jackson-dataformat-xml/issues/646 and demonstrates a failure on
parsing and unwrapped collection using a setter with an Iterable.

## Summary
When using builder classes with method signatures like `Builder orderLines(Iterable<? extends OrderLine> lines)` for
collections, deserialization fails with the following.

```text
com.fasterxml.jackson.databind.exc.MismatchedInputException: Cannot construct instance of `com.example.OrderLine$Builder` (although at least one Creator exists): no String-argument constructor/factory method to deserialize from String value ('1')
 at [Source: (BufferedInputStream); line: 5, column: 14] (through reference chain: com.example.Order$Builder["line"]->java.util.ArrayList[0])
```

## Detailed Example
Consider the following objects:

```java
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
```

```java
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
```
When deserializing the following XMl document:

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<order>
    <id>1</id>
    <line>
        <id>1</id>
        <description>order line 1</description>
    </line>
    <line>
        <id>2</id>
        <description>order line 2</description>
    </line>
</order>
```
Deserializing fails with:

```text
com.fasterxml.jackson.databind.exc.MismatchedInputException: Cannot construct instance of `com.example.OrderLine$Builder` (although at least one Creator exists): no String-argument constructor/factory method to deserialize from String value ('1')
 at [Source: (BufferedInputStream); line: 5, column: 14] (through reference chain: com.example.Order$Builder["line"]->java.util.ArrayList[0])
```
This is caused by the following builder method:

```java
@JsonProperty("line")
public Builder lines(Iterable<? extends OrderLine> lines) { // fails
//public Builder lines(Collection<? extends OrderLine> lines) { // works
    this.lines.clear();
    for (OrderLine line : lines) {
        this.lines.add(line);
    }
    return this;
}
```
If you use `Iterable<? extends OrderLine>` as method parameter it fails. If you use `Collection<? extends OrderLine>`, see commented line above, it works. I expected both to work as is the case for Json binding.
