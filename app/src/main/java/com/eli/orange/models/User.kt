package com.eli.orange.models

import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class User(
        var username: String? = "",
        var email: String? = "",
        var userId: String = "",
        var userImageUrl: String = ""
)