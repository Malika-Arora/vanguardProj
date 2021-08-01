package com.vanguard.application;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.vanguard.exceptions.CustomException;
import com.vanguard.service.EventBuilderService;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@SpringBootApplication(scanBasePackages = { "com.vanguard" })
@EnableJpaRepositories("com.vanguard.repository")
@EntityScan(basePackages = { "com.vanguard.models" })
public class VanguardEventServiceApplication {
	@Autowired
	private EventBuilderService eventBuilderService;
	
	public static void main(String[] args) {
		SpringApplication.run(VanguardEventServiceApplication.class, args);
	}

	@Bean
	public OpenAPI customOpenAPI(@Value("${application-description}") String appDesciption,
			@Value("${build.version}") String appVersion) {
		return new OpenAPI().info(new Info().title("Vanguard service").version(appVersion).description(appDesciption)
				.termsOfService("http://swagger.io/terms/")
				.license(new License().name("Apache 2.0").url("http://springdoc.org")));
	}

	@Bean
	public void readXMLData() {

		ClassLoader classLoader = getClass().getClassLoader();
		URL resource = classLoader.getResource("eventXmls");
		List<File> fileList = null;
		try {
			fileList = Files.walk(Paths.get(resource.toURI())).filter(Files::isRegularFile).map(x -> x.toFile())
					.collect(Collectors.toList());
		} catch (IOException e) {
			throw new CustomException("Error reading xml files");
		} catch (URISyntaxException e2) {
			throw new CustomException("Error reading xml files");
		}
		
		eventBuilderService.buildEventDetails(fileList);
	}

}
