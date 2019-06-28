package com.demo.cl.coroutineExperiment

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.channels.*
import java.util.concurrent.Executors
import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext


suspend fun main() {
    var count = 0
    val mutex=Mutex()

//    runBlocking {
//        Thread.sleep(4000)
//        println("${Thread.currentThread()}-MyContinuation-resumeWith-1")
//    }
//    println("${Thread.currentThread()}-MyContinuation-resumeWith")

/*
 父协程职责:1. 派生职责:所有子协程扩展自父协程  2.管理职责: 父协程取消时会去(软)取消所有子协程; 父协程要等待所有子协程执行完毕才算执行完毕
*/
    val job = GlobalScope.launch {
           val sendChannel= actor<Int> {
                var count=0
                for(i in channel){
                    count+=i
                    println("${Thread.currentThread()}---${count}")
                }
            }
        repeat(5000) {
            launch(Dispatchers.Default){
//                mutex.withLock {
                sendChannel.send(1)
//                }
            }
        }
        sendChannel.close()
    }
    job.join()



}

class MyContinuationInterceptor : ContinuationInterceptor {
    override val key = ContinuationInterceptor

    override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> {
        println("${Thread.currentThread()}-interceptContinuation")
        return MyContinuation(continuation)
    }

}

class MyContinuation<T>(val continuation: Continuation<T>) : Continuation<T> {
    private val executor = Executors.newSingleThreadExecutor { Thread(it, "myThread") }
    override val context: CoroutineContext = continuation.context
    override fun resumeWith(result: Result<T>) {
        println("${Thread.currentThread()}-MyContinuation-resumeWith-1")
        executor.submit {
            println("${Thread.currentThread()}-MyContinuation-executor-1")
            continuation.resumeWith(result)
            println("${Thread.currentThread()}-MyContinuation-executor-2")
        }
        println("${Thread.currentThread()}-MyContinuation-resumeWith-2")
    }

}

suspend fun timeConsumingTask() {
//    println("${Thread.currentThread()}-startNormalTask")
//    Thread.sleep(1000)
//    println("${Thread.currentThread()}-endNormalTask")
//
//    withContext(Dispatchers.Default){
//        println("${Thread.currentThread()}-startSuspendTask")
//        Thread.sleep(1000)
//        println("${Thread.currentThread()}-endSuspendTask")
//    }
//    println("${Thread.currentThread()}-afterCoroutine")
    timeConsumingTask1()

}

suspend fun timeConsumingTask1() {}