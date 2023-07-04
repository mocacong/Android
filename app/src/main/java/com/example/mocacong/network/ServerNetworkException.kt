package com.example.mocacong.network

import java.io.IOException

class ServerNetworkException(val responseCode: Int, val responseMessage: String) : IOException() {

}