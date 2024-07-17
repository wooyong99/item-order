package com.example.core.kafka.producer;

import com.example.core.kafka.dto.PaymentCancleMessage;
import com.example.core.kafka.dto.PaymentConfirmMessage;
import com.example.core.kafka.dto.PaymentRequestMessage;
import com.example.core.kafka.dto.StatusCancleMessage;
import com.example.core.kafka.dto.StatusSuccessMessage;
import com.example.core.kafka.dto.StockDecreaseMessage;
import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public ProducerFactory<String, PaymentConfirmMessage> orderMessageProducerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public ProducerFactory<String, StockDecreaseMessage> itemMessageProducerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public ProducerFactory<String, PaymentCancleMessage> paymentCancleProducerProducerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public ProducerFactory<String, StatusCancleMessage> statusCancleMessageProducerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public ProducerFactory<String, StatusSuccessMessage> statusSuccessMessageProducerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public ProducerFactory<String, PaymentRequestMessage> paymentRequestMessageProducerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, PaymentConfirmMessage> orderMessageKafkaTemplate() {
        return new KafkaTemplate<String, PaymentConfirmMessage>(orderMessageProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, StockDecreaseMessage> itemMessageKafkaTemplate() {
        return new KafkaTemplate<String, StockDecreaseMessage>(itemMessageProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, PaymentCancleMessage> paymentCancleMessageKafkaTemplate() {
        return new KafkaTemplate<String, PaymentCancleMessage>(
            paymentCancleProducerProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, StatusCancleMessage> statusCancleMessageKafkaTemplate() {
        return new KafkaTemplate<String, StatusCancleMessage>(
            statusCancleMessageProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, StatusSuccessMessage> statusSuccessMessageKafkaTemplate() {
        return new KafkaTemplate<String, StatusSuccessMessage>(
            statusSuccessMessageProducerFactory());
    }

    @Bean
    public KafkaTemplate<String, PaymentRequestMessage> paymentRequestMessageKafkaTemplate() {
        return new KafkaTemplate<String, PaymentRequestMessage>(
            paymentRequestMessageProducerFactory());
    }
}
