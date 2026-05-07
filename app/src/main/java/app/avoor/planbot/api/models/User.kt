package app.avoor.planbot.api.models

/**
 * A user as returned by the API.
 *
 * You likely don't need this; use [User] instead.
 */
// TODO maybe merge with User, they have the same fields now
data class UserApi(
    /**
     * The user's ID.
     */
    val id: Int,
    /**
     * The user's email.
     */
    val email: String,
    /**
     * The user's name.
     */
    val name: String,
    /**
     * The user's access token.
     */
    val accessToken: String,
    /**
     * The user's refresh token.
     */
    val refreshToken: String,
    /**
     * The URL of the user's profile picture.
     *
     * Can be null, in which case a default picture should be shown.
     */
    val photoUrl: String?,
    /**
     * Is this user confirmed (i.e. email verified)?
     */
    val confirmed: Boolean,
    /**
     * The user's plancoin balance.
     */
    val plancoins: Int,
    /**
     * The user's current streak.
     */
    val currentStreak: Int,
    /**
     * The user's longest streak.
     */
    val longestStreak: Int
) {
    /**
     * Converts this [UserApi] into a [UserWithoutTokens].
     */
    fun removeTokens(): UserWithoutTokens {
        return UserWithoutTokens(
            id = this.id,
            name = this.name,
            email = this.email,
            confirmed = this.confirmed,
            currentStreak = this.currentStreak,
            longestStreak = this.longestStreak,
            plancoins = this.plancoins,
            photoUrl = this.photoUrl
        )
    }
}

/**
 * A user.
 */
data class User (
    /**
     * The user's ID.
     */
    val id: Int,
    /**
     * The user's email.
     */
    val email: String,
    /**
     * The user's name.
     */
    val name: String,
    /**
     * The URL of the user's profile picture.
     *
     * Can be null, in which case a default picture should be shown.
     */
    val photoUrl: String?,
    /**
     * Is this user confirmed (i.e. email verified)?
     */
    val confirmed: Boolean,
    /**
     * The user's plancoin balance.
     */
    val plancoins: Int,
    /**
     * The user's current streak.
     */
    val currentStreak: Int,
    /**
     * The user's longest streak.
     */
    val longestStreak: Int
) {
    /**
     * Converts this [User] into a [UserWithoutTokens].
     */
    fun removeTokens(): UserWithoutTokens {
        return UserWithoutTokens(
            id = this.id,
            name = this.name,
            email = this.email,
            confirmed = this.confirmed,
            currentStreak = this.currentStreak,
            longestStreak = this.longestStreak,
            plancoins = this.plancoins,
            photoUrl = this.photoUrl
        )
    }
}

/**
 * A user object returned in responses (without tokens).
 */
data class UserWithoutTokens(
    /**
     * The user's ID.
     */
    val id: Int,
    /**
     * The user's email.
     */
    val email: String,
    /**
     * The user's name.
     */
    val name: String,
    /**
     * The URL of the user's profile picture.
     *
     * Can be null, in which case a default picture should be shown.
     */
    val photoUrl: String?,
    /**
     * Is this user confirmed (i.e. email verified)?
     */
    val confirmed: Boolean,
    /**
     * The user's plancoin balance.
     */
    val plancoins: Int,
    /**
     * The user's current streak.
     */
    val currentStreak: Int,
    /**
     * The user's longest streak.
     */
    val longestStreak: Int
)

/**
 * Converts this [UserApi] object to a simplified [User].
 */
fun UserApi.simplify() = User(
    id = id,
    email = email,
    name = name,
    photoUrl = photoUrl,
    confirmed = confirmed,
    plancoins = plancoins,
    currentStreak = currentStreak,
    longestStreak = longestStreak
)

/**
 * Converts this [UserWithoutTokens] object to a [User].
 */
fun UserWithoutTokens.toUser() = User(
    id = id,
    email = email,
    name = name,
    photoUrl = photoUrl,
    confirmed = confirmed,
    plancoins = plancoins,
    currentStreak = currentStreak,
    longestStreak = longestStreak
)