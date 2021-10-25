/*
 * Copyright 2020 New Vector Ltd - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 */

package im.vector.matrix.android.sdk.internal.session.contentscanning.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ServerPublicKeyResponse(
        @Json(name = "public_key")
        val publicKey : String?
)
