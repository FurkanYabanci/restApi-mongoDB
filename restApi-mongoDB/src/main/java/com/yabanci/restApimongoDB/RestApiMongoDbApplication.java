package com.yabanci.restApimongoDB;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;



@SpringBootApplication
public class RestApiMongoDbApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestApiMongoDbApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(StudentRepository repository,MongoTemplate mongoTemplate) {
		return args -> {
			Address address = new Address(
					"Turkey",
					"Adana",
					"01920"
					);
			String email = "yabancifurkan01@gmail.com";
			Student student = new Student(
					"Furkan",
					"Yabanci",
					email,
					Gender.MALE,
					address,
					java.util.List.of("Computer Science","Maths"),
					BigDecimal.TEN,
					LocalDateTime.now()
					);
			
			// usingMongoTemplateAndQuery(repository, mongoTemplate, email, student);
			
			repository.findStudentByEmail(email).ifPresentOrElse(s -> {
				System.out.println(s+ " already exists"); // email daha önce kullanılmışsa zaten var şeklinde geri dönüş yapar.

			},()->{
				System.out.println("Inserting student "+student); // eklenen öğrencinin emaili daha önce kullanılmamışsa veritabanına ekler
				repository.insert(student);
			});
		};
	
	}

	
	private void usingMongoTemplateAndQuery(StudentRepository repository, MongoTemplate mongoTemplate, String email, Student student) {
		Query query = new Query();
		query.addCriteria(Criteria.where("email").is(email));
		
		List<Student> students = mongoTemplate.find(query, Student.class);
		if (students.size() > 1) {
			throw new IllegalStateException("found many students with email "+email);
		}
		
		if (students.isEmpty()) {
			System.out.println("Inserting student "+student); 
			repository.insert(student);
		}else {
			System.out.println(student+ " already exists"); 
		}
	}
}
