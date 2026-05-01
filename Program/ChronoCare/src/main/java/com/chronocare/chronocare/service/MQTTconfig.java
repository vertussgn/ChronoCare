package com.chronocare.chronocare.service;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.messaging.MessageChannel;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

@Configuration
public class MQTTconfig {

    // TLS titkosított kapcsolat (ssl://) a nyílt tcp:// helyett
    // HiveMQ publikus broker TLS portja: 8883
    private static final String BROKER_URL = "ssl://broker.hivemq.com:8883";

    // Wildcard topic: minden beteg mérési adatát fogadja
    // Szerver feliratkozik: "patient/+/measurements"  (+ = bármely patientId)
    private static final String TOPIC = "patient/+/measurements";

    @Bean
    public MqttConnectOptions mqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setServerURIs(new String[]{ BROKER_URL });

        //TLS SSLSocketFactory beállítása
        try {
            SSLContext sslContext = SSLContext.getInstance("TLSv1.3");
            sslContext.init(null, null, null); // JVM alapértelmezett truststore
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            options.setSocketFactory(sslSocketFactory);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("TLS inicializálási hiba – MQTT kapcsolat nem indítható", e);
        }

        // Automatikus újracsatlakozás
        options.setAutomaticReconnect(true);

        // Keep-alive: 60 másodperc (illeszkedik az ESP32 setKeepAlive(60)-jához)
        options.setKeepAliveInterval(60);

        // Kapcsolódáskor törölje a régi session állapotot
        options.setCleanSession(true);

        // Kapcsolódási timeout (másodperc)
        options.setConnectionTimeout(10);

        return options;
    }

    /**
     * MqttPahoClientFactory: a kapcsolati beállításokat (beleértve a TLS-t) átadja az adapternek.
     */
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        factory.setConnectionOptions(mqttConnectOptions());
        return factory;
    }

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound() {
        // Egyedi client ID futási időben generálva
        String clientId = "chronocare-server-" + System.currentTimeMillis();

        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                        clientId,
                        mqttClientFactory(),
                        TOPIC
                );

        // QoS 1: minden üzenet legalább egyszer kézbesítve
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());

        // Kapcsolat felépítési timeout (ms)
        adapter.setCompletionTimeout(5000);

        return adapter;
    }
}