package org.sid.scolariteservice;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.data.rest.core.config.Projection;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private Date birthDate;
    @ManyToOne
	private Laboratory laboratory;
}

@RepositoryRestResource
interface StudentRepository extends JpaRepository<Student, Long> {
	@RestResource(path = "/byName")
	public List<Student> findByNameContains(@Param(value = "mc") String mc);
}

@Entity
@Data @NoArgsConstructor @AllArgsConstructor
class Laboratory {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotNull
    @Size(min = 2, max = 20)
	private String name;
	private String contact;
	@OneToMany(mappedBy = "laboratory")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private Collection<Student> students;
}

@RepositoryRestResource
interface LaboratoryRepository extends JpaRepository<Laboratory,Long>{

}


@RestController
@RequestMapping("/api")
class ScolariteRestController {

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/students")
    public List<Student> students() {
        return studentRepository.findAll();
    }

	@GetMapping("/students/{id}")
	public Student getOne( @PathVariable(name = "id") Long id) {
		return studentRepository.findById(id).get();
	}

	@PostMapping("/students")
	public Student save (@RequestBody Student student){
    	return studentRepository.save(student);
	}

	@PutMapping("/students/{id}")
	public Student update(@PathVariable(name = "id") Long id, @RequestBody Student student){
		student.setId(id);
		return studentRepository.save(student);
	}

	@DeleteMapping("/students/{id}")
	public void delete(@PathVariable Long id){
		studentRepository.deleteById(id);
	}

}

@Projection(name = "p1", types = Student.class)
interface StudentProjection {

    public String getEmail();

    public String getName();

    public Laboratory getLaboratory();
}

@SpringBootApplication
public class ScolariteServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScolariteServiceApplication.class, args);
    }

    @Bean
    CommandLineRunner start(StudentRepository studentRepository, RepositoryRestConfiguration restConfiguration, LaboratoryRepository laboratoryRepository) {
        return args -> {
            restConfiguration.exposeIdsFor(Student.class);

            Laboratory l1 = laboratoryRepository.save(new Laboratory(null,"Informatique","contact@gmail.com",null));
			Laboratory l2 = laboratoryRepository.save(new Laboratory(null,"Biologie","contact@gmail.com",null));

            studentRepository.save(new Student(null, "Hassan", "hassan@gmail.com", new Date(), l1));
            studentRepository.save(new Student(null, "Mohamed", "mohamed@gmail.com", new Date(),l1));
            studentRepository.save(new Student(null, "Samira", "samira@gmail.com", new Date(),l2));
            studentRepository.save(new Student(null, "Hasna", "hasna@gmail.com", new Date(),l2));

           studentRepository.findAll().forEach(st -> System.out.println(st.getName()));
        };
    }
}
