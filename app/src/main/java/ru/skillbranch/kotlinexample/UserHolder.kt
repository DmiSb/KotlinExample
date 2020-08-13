package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting
import ru.skillbranch.kotlinexample.extensions.clearPhone

object UserHolder {
    private val map = mutableMapOf<String, User>()

    fun registerUser(
        fullName: String,
        email: String,
        password: String
    ) : User {
        return User.makeUser(fullName, email = email, password = password)
            .also { user ->
                if (map[user.login] == null) map[user.login] = user
                else throw IllegalArgumentException("A user with this email already exists")
            }
    }

    fun registerUserByPhone(
        fullName: String,
        phone: String
    ) : User {
        val clearPhone = phone.clearPhone()
        return if (clearPhone.first() == '+' && clearPhone.length == 12) {
            User.makeUser(fullName = fullName, phone = phone)
                .also { user ->
                    if (map[phone] == null) map[phone] = user
                    else throw IllegalArgumentException("A user with this email already exists")
                }
        } else throw IllegalArgumentException("Enter a valid phone number starting with a + and containing 11 digits")
    }

    fun loginUser(login: String, password: String): String? {
        return map[login.trim()]?.run {
            if (checkPassword(password)) this.userInfo
            else null
        }
    }

    fun requestAccessCode(login: String) {
        map[login]?.requestAccessCode()
    }

    fun importUsers(list: List<String>): List<User> =
        list.map { item ->
            val fields = item.split(";").map { it.trim() }
            when {
                fields.isEmpty() -> throw IllegalArgumentException("User fields not present")
                fields[0].isBlank() -> throw IllegalArgumentException("FirstName must be not blank")
                (fields.getOrNull(1).isNullOrBlank() || fields.getOrNull(2).isNullOrBlank()) &&
                        fields.getOrNull(3).isNullOrBlank() -> throw IllegalArgumentException("Email or phone must be not blank")
                else -> {
                    val passAndSalt = fields.getOrNull(2)?.split(":")
                    User.importUser(
                        fullName = fields[0],
                        email = fields.getOrNull(1),
                        password = passAndSalt?.getOrNull(0),
                        salt = passAndSalt?.getOrNull(1),
                        rawPhone = fields.getOrNull(3)
                    ).apply {
                        map[if (!fields.getOrNull(3).isNullOrBlank()) fields[3] else fields[1]] = this
                    }
                }
            }

        }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder(){
        map.clear()
    }
}