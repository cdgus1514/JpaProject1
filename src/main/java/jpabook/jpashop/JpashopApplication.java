package jpabook.jpashop;

import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class JpashopApplication {

	public static void main(String[] args) {
		SpringApplication.run(JpashopApplication.class, args);
	}

	// 엔티티를 직접 노출하지 않으면 사용하지 않아도 됨...
	@Bean
	Hibernate5Module hibernate5Module() {

		// 연관관계 중 지연로딩이면 null로 가져옴
		return new Hibernate5Module();

		// 강제로 지연로딩 실행 -> 연관된 객체 전부다 가져옴(권장X)
		//Hibernate5Module hibernate5Module = new Hibernate5Module();
		//hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true);
		//return hibernate5Module;
	}

}
