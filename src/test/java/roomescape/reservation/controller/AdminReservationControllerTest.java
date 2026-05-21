package roomescape.reservation.controller;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@Sql({"/create_reservation_time.sql", "/create_theme.sql"})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class AdminReservationControllerTest {

    @Test
    void 관리자가_예약_목록을_조회한다() {
        createReservation("milan@example.com", 1L);
        createReservation("brown@example.com", 2L);
        String sessionId = login("milan@example.com");

        RestAssured.given().log().all()
                .cookie("JSESSIONID", sessionId)
                .when().get("/admin/reservations")
                .then().log().all()
                .statusCode(200)
                .body("memberId", hasItem(2))
                .body("date", hasItem("2099-05-03"))
                .body("time.id", hasItem(1))
                .body("size()", is(1));
    }

    @Test
    void 관리자가_예약을_삭제한다() {
        Integer id = createReservation("milan@example.com", 1L);
        String sessionId = login("milan@example.com");

        RestAssured.given().log().all()
                .cookie("JSESSIONID", sessionId)
                .when().delete("/admin/reservations/" + id)
                .then().log().all()
                .statusCode(204);

        RestAssured.given().log().all()
                .cookie("JSESSIONID", sessionId)
                .when().get("/admin/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    void 관리자가_존재하지_않는_예약을_삭제하면_404를_응답한다() {
        String sessionId = login("milan@example.com");

        RestAssured.given().log().all()
                .cookie("JSESSIONID", sessionId)
                .when().delete("/admin/reservations/999")
                .then().log().all()
                .statusCode(404)
                .body("message", is("존재하지 않는 예약입니다. id=999"));
    }

    @Test
    void 관리자가_자기_매장_예약을_수정한다() {
        Integer id = createReservation("milan@example.com", 1L);
        String sessionId = login("milan@example.com");
        Map<String, Object> params = new HashMap<>();
        params.put("date", "2099-05-04");
        params.put("timeId", 2L);
        params.put("themeId", 1L);

        RestAssured.given().log().all()
                .cookie("JSESSIONID", sessionId)
                .contentType(ContentType.JSON)
                .body(params)
                .when().patch("/admin/reservations/" + id)
                .then().log().all()
                .statusCode(200)
                .body("id", is(id))
                .body("date", is("2099-05-04"))
                .body("time.id", is(2));
    }

    @Test
    void 로그인하지_않으면_관리자_예약을_조회할_수_없다() {
        RestAssured.given().log().all()
                .when().get("/admin/reservations")
                .then().log().all()
                .statusCode(401)
                .body("message", is("로그인이 필요합니다."));
    }

    @Test
    void 매니저가_아니면_관리자_예약을_조회할_수_없다() {
        String sessionId = login("bongus@example.com");

        RestAssured.given().log().all()
                .cookie("JSESSIONID", sessionId)
                .when().get("/admin/reservations")
                .then().log().all()
                .statusCode(403)
                .body("message", is("접근 권한이 없습니다."));
    }

    @Test
    void 관리자가_다른_매장_예약을_삭제할_수_없다() {
        Integer id = createReservation("brown@example.com", 2L);
        String sessionId = login("milan@example.com");

        RestAssured.given().log().all()
                .cookie("JSESSIONID", sessionId)
                .when().delete("/admin/reservations/" + id)
                .then().log().all()
                .statusCode(403)
                .body("message", is("접근 권한이 없습니다."));
    }

    @Test
    void 관리자가_다른_매장_예약을_수정할_수_없다() {
        Integer id = createReservation("brown@example.com", 2L);
        String sessionId = login("milan@example.com");
        Map<String, Object> params = new HashMap<>();
        params.put("date", "2099-05-04");
        params.put("timeId", 2L);
        params.put("themeId", 1L);

        RestAssured.given().log().all()
                .cookie("JSESSIONID", sessionId)
                .contentType(ContentType.JSON)
                .body(params)
                .when().patch("/admin/reservations/" + id)
                .then().log().all()
                .statusCode(403)
                .body("message", is("접근 권한이 없습니다."));
    }

    private Integer createReservation(String email, Long storeId) {
        String sessionId = login(email);
        Map<String, Object> params = new HashMap<>();
        params.put("date", "2099-05-03");
        params.put("timeId", 1L);
        params.put("themeId", 1L);
        params.put("storeId", storeId);

        return RestAssured.given().log().all()
                .cookie("JSESSIONID", sessionId)
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201)
                .extract()
                .path("id");
    }

    private String login(String email) {
        Map<String, Object> params = new HashMap<>();
        params.put("email", email);
        params.put("password", "password");

        return RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/login")
                .then().log().all()
                .statusCode(200)
                .extract()
                .cookie("JSESSIONID");
    }
}
