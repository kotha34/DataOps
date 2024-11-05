package com.example.demo;


import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumerService {

    private static final String INPUT_TOPIC = "user-login";
    private static final String OUTPUT_TOPIC = "processed-user-login";
    private static final String BOOTSTRAP_SERVERS = "localhost:29092";

    public static void consume() {
        Properties consumerProperties = new Properties();
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "consumer-group-1");
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consumerProperties);
        consumer.subscribe(Collections.singleton(INPUT_TOPIC));

        Properties producerProperties = new Properties();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        KafkaProducer<String, String> producer = new KafkaProducer<>(producerProperties);

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, String> record : records) {
            	// Print the consumed message
                System.out.printf("Consumed message: Key = %s, Value = %s, Partition = %d, Offset = %d%n",
                                  record.key(), record.value(), record.partition(), record.offset());
                // Process the message here
                String processedMessage = processMessage(record.value());

                // Produce the processed message to the new topic
                ProducerRecord<String, String> producerRecord = new ProducerRecord<>(OUTPUT_TOPIC, record.key(), processedMessage);
                producer.send(producerRecord);
            }
        }
    }

    private static String processMessage(String message) {
        
        return message.trim();
    }
}
