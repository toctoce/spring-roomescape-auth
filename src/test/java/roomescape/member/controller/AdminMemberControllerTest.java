package roomescape.member.controller;

import static org.hamcrest.Matchers.is;

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
        "DELETE FROM stores",
        "ALTER TABLE members ALTER COLUMN id RESTART WITH 1",
        "ALTER TABLE stores ALTER COLUMN id RESTART WITH 1",
        "INSERT INTO stores (name) VALUES ('잠실점')"
})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class AdminMemberControllerTest {

    @Test
    void 매니저_회원을_생성한다() {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "매니저");
        params.put("email", "manager@example.com");
        params.put("password", "password");
        params.put("storeId", 1L);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/admin/members")
                .then().log().all()
                .statusCode(201)
                .body("name", is("매니저"))
                .body("email", is("manager@example.com"));
    }

    @Test
    void 존재하지_않는_매장으로_매니저_회원을_생성하면_404를_응답한다() {
        Map<String, Object> params = new HashMap<>();
        params.put("name", "매니저");
        params.put("email", "manager@example.com");
        params.put("password", "password");
        params.put("storeId", 999L);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/admin/members")
                .then().log().all()
                .statusCode(404)
                .body("message", is("존재하지 않는 매장입니다. id=999"));
    }
}
