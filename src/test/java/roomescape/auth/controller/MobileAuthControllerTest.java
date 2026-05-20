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

@Sql({"/create_reservation_time.sql", "/create_theme.sql"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class MobileAuthControllerTest {

    @Test
    void 모바일_로그인을_하면_토큰을_응답한다() {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(loginParams("milan@example.com", "password"))
                .when().post("/mobile/login")
                .then().log().all()
                .statusCode(200)
                .body("accessToken", notNullValue())
                .body("tokenType", is("Bearer"))
                .body("expiresIn", is(3600));
    }

    @Test
    void 모바일_로그인에_실패하면_401을_응답한다() {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(loginParams("milan@example.com", "wrong-password"))
                .when().post("/mobile/login")
                .then().log().all()
                .statusCode(401)
                .body("message", is("이메일 또는 비밀번호가 올바르지 않습니다."));
    }

    @Test
    void 모바일_토큰으로_로그인_회원_정보를_조회한다() {
        String accessToken = mobileLogin("milan@example.com");

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .when().get("/members/me")
                .then().log().all()
                .statusCode(200)
                .body("id", is(2))
                .body("name", is("밀란"))
                .body("email", is("milan@example.com"));
    }

    @Test
    void 모바일_토큰으로_예약을_생성한다() {
        String accessToken = mobileLogin("milan@example.com");

        Map<String, Object> params = new HashMap<>();
        params.put("date", "2099-05-03");
        params.put("timeId", 1L);
        params.put("themeId", 1L);

        RestAssured.given().log().all()
                .header("Authorization", "Bearer " + accessToken)
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201)
                .body("memberId", is(2))
                .body("date", is("2099-05-03"));
    }

    @Test
    void 유효하지_않은_모바일_토큰이면_401을_응답한다() {
        RestAssured.given().log().all()
                .header("Authorization", "Bearer invalid-token")
                .when().get("/members/me")
                .then().log().all()
                .statusCode(401)
                .body("message", is("로그인이 필요합니다."));
    }

    @Test
    void Bearer_형식이_아닌_인증_헤더면_401을_응답한다() {
        RestAssured.given().log().all()
                .header("Authorization", "Basic invalid-token")
                .when().get("/members/me")
                .then().log().all()
                .statusCode(401)
                .body("message", is("로그인이 필요합니다."));
    }

    private String mobileLogin(String email) {
        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(loginParams(email, "password"))
                .when().post("/mobile/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .path("accessToken");
    }

    private Map<String, Object> loginParams(String email, String password) {
        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        return params;
    }
}
