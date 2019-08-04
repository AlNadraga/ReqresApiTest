package steps

import io.restassured.RestAssured
import io.restassured.path.json.JsonPath.from
import io.restassured.response.Response
import net.serenitybdd.core.Serenity
import net.thucydides.core.annotations.Step

import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.hamcrest.Matchers.hasKey
import org.hamcrest.Matchers.nullValue

const val LIST_USERS_URL = "http://reqres.in/api/users?page="
const val TOTAL_USERS = 12

open class ListUsersResponse {

    private var response: Response? = null
    private var users_counter:Int = 0

    //Получаем ответ тут и сохраняем его
    @Step
    open fun getUserListByNumber(page_num: Int) {
        response = RestAssured.`when`().get(LIST_USERS_URL + "$page_num")
    }
    //Проверяем, что ответ пришел и все хорошо
    @Step
    open fun isExecutedSuccessful() {
        response!!.then().statusCode(200)
    }
    //Получаем юзера из ответа по id и проверяем на соответсвие
    @Step
    open fun checkUserById(id: Int, first_name: String, last_name: String) {
        response!!.then().body("data.find {it.id == $id}.first_name", `is`(first_name))
            .body("data.find {it.id == $id}.last_name", `is`(last_name))
    }
    //Тут проверяем, что все поля присутсвуют
    @Step
    open fun checkDataField() {
        val users_per_page = from(response!!.asString()).getInt("per_page")
        //Если вернулось поле data нулевой длины то далее нет смысла проверять
        response!!.then().body("data.size()", not(0))
        //Проверяем каждый элемент в data и смотрим, что везде есть нужные поля. Касательно полей было сказано, что
        //все поля обязательные. Немного не понял, обязательно, всмысле, их наличие, или то, что они не пустые, поэтому
        //проверяю и на наличие полей и на то, что они не пустые
        for (i in 0 until users_per_page) {
            response!!.then().body("data[$i]", hasKey("id")).and().body("data[$i].id", not(nullValue()))
                .body("data[$i]", hasKey("email")).and().body("data[$i].email", not(nullValue()))
                .body("data[$i]", hasKey("first_name")).and().body("data[$i].first_name", not(nullValue()))
                .body("data[$i]", hasKey("last_name")).and().body("data[$i].last_name", not(nullValue()))
        }
    }
    //Проверка на количество пользователей.Проверяю, что поле total равно 12, а также считаю ползователей, чтобы потом
    //сравнить уже количество пользователей из data с 12
    @Step
    open fun checkTotalUserAtPage() {
        //Сравниваю с константой
        response!!.then().body("total", `is`(TOTAL_USERS))
            //Также смотрим что поле с количеством пользователей на странице соответствует количеству в data
            .body("data.size()", `is`(from(response!!.asString()).getInt("per_page")))
        users_counter += from(response!!.asString()).getInt("data.size()")
    }
    //Тут уже проверяю, что именно посчитанных пользователей по страницам совпадает с полем total
    @Step
    open fun checkTotalUsers(){
        response!!.then().body("total",`is`(users_counter))
        users_counter = 0
    }
}