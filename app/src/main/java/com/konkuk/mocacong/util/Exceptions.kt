package com.konkuk.mocacong.util

class AccessTokenExpiredException(override val message: String) : RuntimeException(message)

class RefreshTokenExpiredException(override val message: String) : RuntimeException(message)
