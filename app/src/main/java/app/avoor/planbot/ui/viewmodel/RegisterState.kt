package app.avoor.planbot.ui.viewmodel

data class RegisterState (
    /**
     * The email.
     */
    val email: String = "",
    /**
     * The name.
     */
    val name: String = "",
    /**
     * The password.
     */
    val password: String = "",
    /**
     * The password confirmation.
     */
    val confirmPassword: String = "",

    /**
     * A password error resource ID, if any.
     */
    val passwordError: Int? = null,
    /**
     * Does the password match its confirmation?
     */
    val passwordsMatch: Boolean = true,

    /**
     * Should the password be shown?
     */
    val showPassword: Boolean = false,
    /**
     * Should the password confirmation be shown?
     */
    val showConfirmPassword: Boolean = false,

    /**
     * Is an action in progress?
     */
    val progress: Boolean = false,
    /**
     * Has the connection failed?
     */
    val showConnectionError: Boolean = false,
    val showActionError: Boolean = false,

    /**
     * Is the email valid?
     */
    val emailValid: Boolean = false,
    /**
     * Does this account already exist?
     */
    val accountExists: Boolean = false
)
