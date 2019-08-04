package steps

import io.restassured.RestAssured
import io.restassured.path.json.JsonPath.from
import io.restassured.response.Response
import net.thucydides.core.annotations.Step

import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.hasKey
import org.hamcrest.Matchers.nullValue

const val LIST_USERS_URL = "http://reqres.in/api/users?page="
const val TOTAL_USERS = 12

open class ListUsersResponse {

    private var response: Response? = null

    @Step
    open fun getUserListByNumber(page_num: Int) {
        response = RestAssured.`when`().get(LIST_USERS_URL + "$page_num")
    }

    @Step
    open fun isExecutedSuccessful() {
        response!!.then().statusCode(200)
    }

    @Step
    open fun verifyUserById(id: Int, first_name: String, last_name: String) {
        response!!.then().body("data.find {it.id == $id}.first_name", `is`(first_name))
            .body("data.find {it.id == $id}.last_name", `is`(last_name))
    }

    @Step
    open fun verifyJSONFields() {
        val users_per_page = from(response!!.asString()).getInt("per_page")
        response!!.then().body("data.size()", not(0))
        for (i in 0 until users_per_page) {
            response!!.then().body("data[$i]", hasKey("id")).and().body("data[$i].id", not(nullValue()))
                .body("data[$i]", hasKey("email")).and().body("data[$i].email", not(nullValue()))
                .body("data[$i]", hasKey("first_name")).and().body("data[$i].first_name", not(nullValue()))
                .body("data[$i]", hasKey("last_name")).and().body("data[$i].last_name", not(nullValue()))
        }
    }

    @Step
    open fun checkTotalUser() {
        response!!.then().body("total", `is`(TOTAL_USERS))
        val total_users = from(response!!.asString()).getInt("total")
        val total_pages = from(response!!.asString()).getInt("total_pages")
        var users_counter = 0

        for (i in 2..total_pages + 1) {
            response!!.then().body("data.size()", not(0))
                .body("total", `is`(TOTAL_USERS))
                .body("data.size()", `is`(from(response!!.asString()).getInt("per_page")))
            users_counter += from(response!!.asString()).getInt("data.size()")
            getUserListByNumber(i)
        }
        assert(users_counter == total_users, { "Users counter ($users_counter) != total users ($total_users)" })
    }
}