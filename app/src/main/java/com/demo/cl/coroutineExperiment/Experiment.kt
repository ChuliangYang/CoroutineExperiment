package com.demo.cl.coroutineExperiment

import kotlinx.coroutines.*


fun main(){
    GlobalScope.launch(Dispatchers.IO) {
        delay(2000)

        async {

        }
    }
}