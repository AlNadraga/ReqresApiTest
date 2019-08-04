package tests

import net.serenitybdd.junit.runners.SerenityRunner
import net.thucydides.core.annotations.Steps
import org.junit.Test
import steps.ListUsersResponse

import org.junit.runner.RunWith

@RunWith(SerenityRunner::class)
class UserListTest {
    @Steps
    lateinit var usersValid: ListUsersResponse
    @Test
    fun verifyThatUserWithId1HasNameGeorge(){
        usersValid.getUserListByNumber(1)
        usersValid.isExecutedSuccessful()
        usersValid.verifyUserById(1,"George", "Bluth")
    }

    @Test
    fun verifyDataFields(){
        usersValid.getUserListByNumber(1)
        usersValid.isExecutedSuccessful()
        usersValid.verifyJSONFields()
    }

    @Test
    fun verifyTotalUser(){
        usersValid.getUserListByNumber(1)
        usersValid.isExecutedSuccessful()
        usersValid.checkTotalUser()
    }
}