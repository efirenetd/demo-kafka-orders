package org.efire.net.common;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static springfox.documentation.builders.RequestHandlerSelectors.basePackage;

@Configuration
@Profile({"!prod && local"})
//@EnableSwagger2
@ConfigurationProperties("app.api")
@Getter @Setter
public class Swagger3Config {

    private String version;
    private String title;
    private String description;
    private String contactName;
    private String contactEmail;
    private String basePackage;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(basePackage(basePackage))
                .paths(PathSelectors.regex("/.*"))
                .build()
                .directModelSubstitute(LocalDate.class, java.sql.Date.class)
                .directModelSubstitute(LocalDateTime.class, java.util.Date.class)
                .apiInfo(getApiInfo());
    }

    private ApiInfo getApiInfo() {
        return new ApiInfoBuilder()
                .title(title)
                .description(description)
                .version(version)
                .contact(new Contact(contactName,null, contactEmail))
                .build();
    }

}
