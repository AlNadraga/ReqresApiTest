package tests

import net.serenitybdd.junit.runners.SerenityRunner
import net.thucydides.core.annotations.Steps
import org.junit.Test
import steps.ListUsersResponse

import org.junit.runner.RunWith

@RunWith(SerenityRunner::class)
class UserListTest {
    //Все тесты разбиты на шаги, которые объединяем в один тест. Код шагов - UserListSteps.kt
    @Steps
    lateinit var usersValid: ListUsersResponse
    //Валидация значений полей JSON структуры респонза, например проверяем, что юзера с id=1
    //зовут “George”.
    @Test
    fun verifyThatUserWithId1HasNameGeorge(){
        //Шаг получения ответа на запрос
        usersValid.getUserListByNumber(1)
        //Проверяем, что успешно получили ответ
        usersValid.isExecutedSuccessful()
        //Проверяем, что пользовтеля с id 1 зовут George Bluth
        usersValid.checkUserById(1,"George", "Bluth")
    }
    //валидация структуры объекта data в респонзе (т.е. что все обязательные поля всегда
    //присутствуют. Мы полагаем, что все поля mandatory)
    @Test
    fun verifyDataFields(){
        usersValid.getUserListByNumber(1)
        usersValid.isExecutedSuccessful()
        usersValid.checkDataField()
    }
    //проверка, что сервис всегда отдает фиксированное кол-во юзеров суммарно по всем страницам. Проверяем, что
    //на каждой странице поле total равно 12, а также считаем пользователей сами и сравниваем уже посчитанных
    @Test
    fun verifyTotalUser(){
        for(i in 1..4) {
            usersValid.getUserListByNumber(i)
            usersValid.isExecutedSuccessful()
            usersValid.checkDataField()
            usersValid.checkTotalUserAtPage()
        }
        usersValid.checkTotalUsers()
    }
}