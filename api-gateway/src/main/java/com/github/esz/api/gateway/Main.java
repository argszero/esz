package com.github.esz.api.gateway;

import org.apache.commons.io.FileUtils;
import org.apache.coyote.http11.Http11NioProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.File;

@Configuration
@EnableAutoConfiguration
@ComponentScan("com.github.esz")
@EnableScheduling
public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);
    @Value("${server.port:8080}")
    private int port;

    @Bean
    EmbeddedServletContainerCustomizer containerCustomizer(
            @Value("${keystore.file}") String keystoreFile,
            @Value("${keystore.pass}") String keystorePass) throws Exception {
        File file = new File(keystoreFile);
        if (!file.exists()) {
            FileUtils.copyURLToFile(getClass().getResource("/keystore.p12"), file);
        }

        String absoluteKeystoreFile = file.getAbsolutePath();
        logger.info("KeystoreFile:" + absoluteKeystoreFile);

        return (ConfigurableEmbeddedServletContainer container) -> {
            TomcatEmbeddedServletContainerFactory tomcat = (TomcatEmbeddedServletContainerFactory) container;
            tomcat.addConnectorCustomizers(
                    (connector) -> {
                        connector.setPort(port);
                        connector.setSecure(true);
                        connector.setScheme("https");

                        Http11NioProtocol proto = (Http11NioProtocol) connector.getProtocolHandler();
                        proto.setSSLEnabled(true);
                        proto.setKeystoreFile(absoluteKeystoreFile);
                        proto.setKeystorePass(keystorePass);
                        proto.setKeystoreType("PKCS12");
                        proto.setKeyAlias("tomcat");
                    }
            );

        };
    }

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
