package roomescape.member.controller;

import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.jdbc.Sql;

@Sql(statements = "DELETE FROM members")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberControllerTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void 회원가입을_한다() {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "사용자");
        params.put("email", "member@example.com");
        params.put("password", "password");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/members")
                .then().log().all()
                .statusCode(201)
                .body("name", is("사용자"))
                .body("email", is("member@example.com"));
    }

    @Test
    void 이미_가입된_이메일로_회원가입하면_409를_응답한다() {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "사용자");
        params.put("email", "member@example.com");
        params.put("password", "password");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/members")
                .then().log().all()
                .statusCode(201);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/members")
                .then().log().all()
                .statusCode(409)
                .body("message", is("이미 가입된 이메일입니다. email = member@example.com"));
    }

    @Test
    void 이메일_형식이_올바르지_않으면_400을_응답한다() {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "사용자");
        params.put("email", "invalid-email");
        params.put("password", "password");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/members")
                .then().log().all()
                .statusCode(400);
    }
}
