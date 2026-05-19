package roomescape.auth.controller;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@Sql(statements = {
        "DELETE FROM reservation",
        "DELETE FROM members",
        "ALTER TABLE members ALTER COLUMN id RESTART WITH 1"
})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class AuthControllerTest {

    @Test
    void 로그인한다() {
        createMember("사용자", "member@example.com", "password");

        Map<String, Object> params = new HashMap<>();
        params.put("email", "member@example.com");
        params.put("password", "password");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/login")
                .then().log().all()
                .statusCode(200)
                .cookie("JSESSIONID", notNullValue())
                .body("id", is(1))
                .body("name", is("사용자"))
                .body("email", is("member@example.com"));
    }

    @Test
    void 존재하지_않는_이메일로_로그인하면_401을_응답한다() {
        Map<String, Object> params = new HashMap<>();
        params.put("email", "unknown@example.com");
        params.put("password", "password");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/login")
                .then().log().all()
                .statusCode(401)
                .body("message", is("이메일 또는 비밀번호가 올바르지 않습니다."));
    }

    @Test
    void 비밀번호가_다르면_401을_응답한다() {
        createMember("사용자", "member@example.com", "password");

        Map<String, Object> params = new HashMap<>();
        params.put("email", "member@example.com");
        params.put("password", "wrong-password");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/login")
                .then().log().all()
                .statusCode(401)
                .body("message", is("이메일 또는 비밀번호가 올바르지 않습니다."));
    }

    @Test
    void 로그아웃한다() {
        createMember("사용자", "member@example.com", "password");

        Map<String, Object> loginParams = new HashMap<>();
        loginParams.put("email", "member@example.com");
        loginParams.put("password", "password");

        String sessionId = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(loginParams)
                .when().post("/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .cookie("JSESSIONID");

        RestAssured.given().log().all()
                .cookie("JSESSIONID", sessionId)
                .when().post("/logout")
                .then().log().all()
                .statusCode(204);
    }

    private void createMember(String name, String email, String password) {
        Map<String, Object> params = new HashMap<>();
        params.put("name", name);
        params.put("email", email);
        params.put("password", password);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/members")
                .then().log().all()
                .statusCode(201);
    }
}
