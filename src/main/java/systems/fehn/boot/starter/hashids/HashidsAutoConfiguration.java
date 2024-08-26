package systems.fehn.boot.starter.hashids;

import org.hashids.Hashids;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@AutoConfiguration
@EnableConfigurationProperties(HashidsProperties.class)
public class HashidsAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    Hashids hashids(final HashidsProperties properties, final HashidsProvider provider) {
        return provider.getFromProperties(
                properties.getSalt(),
                properties.getMinHashLength(),
                properties.getAlphabet()
        );
    }

    @Bean
    HashidsProvider hashidsProvider(final HashidsProperties properties) {
        return new HashidsProvider(properties);
    }

    @Configuration
    public static class HashidsWebMvcConfigurer implements WebMvcConfigurer {

        private final HashidsProvider provider;

        public HashidsWebMvcConfigurer(final HashidsProvider provider) {
            this.provider = provider;
        }

        @Override
        public void addFormatters(final FormatterRegistry registry) {
            registry.addFormatterForFieldAnnotation(new HashidsAnnotationFormatterFactory(provider));
        }
    }
}
