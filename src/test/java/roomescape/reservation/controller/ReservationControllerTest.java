package roomescape.reservation.controller;

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
class ReservationControllerTest {

    @Test
    void 비로그인_사용자는_예약을_생성할_수_없다() {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(reservationParams("2099-05-03", 1L, 1L))
                .when().post("/reservations")
                .then().log().all()
                .statusCode(401)
                .body("message", is("로그인이 필요합니다."));
    }

    @Test
    void 로그인한_사용자는_예약을_생성한다() {
        String sessionId = login("milan@example.com");

        RestAssured.given().log().all()
                .cookie("JSESSIONID", sessionId)
                .contentType(ContentType.JSON)
                .body(reservationParams("2099-05-03", 1L, 1L))
                .when().post("/reservations")
                .then().log().all()
                .statusCode(201)
                .body("memberId", is(2))
                .body("date", is("2099-05-03"))
                .body("time.id", is(1))
                .body("theme.id", is(1));
    }

    @Test
    void 로그인한_사용자는_내_예약을_조회한다() {
        String memberSessionId = login("milan@example.com");
        String otherMemberSessionId = login("brown@example.com");

        createReservation(memberSessionId, "2099-05-03", 1L, 1L);
        createReservation(memberSessionId, "2099-05-04", 2L, 1L);
        createReservation(otherMemberSessionId, "2099-05-05", 3L, 1L);

        RestAssured.given().log().all()
                .cookie("JSESSIONID", memberSessionId)
                .when().get("/reservations")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(2))
                .body("[0].memberId", is(2))
                .body("[0].date", is("2099-05-04"))
                .body("[1].memberId", is(2))
                .body("[1].date", is("2099-05-03"));
    }

    private Integer createReservation(String sessionId, String date, Long timeId, Long themeId) {
        return RestAssured.given().log().all()
                .cookie("JSESSIONID", sessionId)
                .contentType(ContentType.JSON)
                .body(reservationParams(date, timeId, themeId))
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

    private Map<String, Object> reservationParams(String date, Long timeId, Long themeId) {
        Map<String, Object> params = new HashMap<>();
        params.put("date", date);
        params.put("timeId", timeId);
        params.put("themeId", themeId);
        return params;
    }
}
