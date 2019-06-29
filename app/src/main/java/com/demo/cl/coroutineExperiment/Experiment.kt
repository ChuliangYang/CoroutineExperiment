package com.demo.cl.coroutineExperiment

import android.content.ClipData.Item
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.channels.*
import java.math.BigInteger
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
//    val job = GlobalScope.launch {
//           val sendChannel= actor<Int> {
//                var count=0
//                for(i in channel){
//                    count+=i
//                    println("${Thread.currentThread()}---${count}")
//                }
//            }
//        repeat(5000) {
//            launch(Dispatchers.Default){
////                mutex.withLock {
//                sendChannel.send(1)
////                }
//            }
//        }
//        sendChannel.close()
//    }
//    job.join()


//    GlobalScope.launch(MyContinuationInterceptor()){
////        repeat(10){
////            Thread.sleep(1000)
////            println("${Thread.currentThread()}")
////        }
//        launch {
//
//        }
//        timeConsumingTask()
//
//    }.join()

    withContext(Dispatchers.Default){
        //构造子协程,模拟耗时任务
        println("${Thread.currentThread()}-coroutineScope")
    }
    coroutineScope{
        println("${Thread.currentThread()}-coroutineScope")

    }
    GlobalScope.launch(MyContinuationInterceptor()){
        log.d("这里会被调度到")
        delay(1000)//这里由suspend的函数自调度
        log.d("这里也会被调度到")
        withContext(Dispatchers.Default){//这里由协程内部自行调度
            println("这里受协程内自调度")
        }
        log.d("这里也会被调度到")
    }

}

class MyContinuationInterceptor : ContinuationInterceptor {
    override val key = ContinuationInterceptor

    override fun <T> interceptContinuation(continuation: Continuation<T>): Continuation<T> {
        return MyContinuation(continuation)
    }

}

class MyContinuation<T>(val continuation: Continuation<T>) : Continuation<T> {
    private val executor = Executors.newSingleThreadExecutor { Thread(it, "myThread") }
    override val context: CoroutineContext = continuation.context
    override fun resumeWith(result: Result<T>) {
        executor.submit {
            continuation.resumeWith(result)
        }
    }

}

suspend fun timeConsumingTask() {
//    withContext(Dispatchers.Default){
//        //构造子协程,模拟耗时任务
//        Thread.sleep(1000)
//    }
    //调用suspend函数
    coroutineScope(){

    }
    println("${Thread.currentThread()}-1")
    withContext(Dispatchers.Default){
        //构造子协程,模拟耗时任务
        println("${Thread.currentThread()}-coroutineScope")
    }
    println("${Thread.currentThread()}-2")
    withContext(Dispatchers.Default){
        //构造子协程,模拟耗时任务
        println("${Thread.currentThread()}-coroutineScope")
    }
    println("${Thread.currentThread()}-3")
    withContext(Dispatchers.Default){
        //构造子协程,模拟耗时任务
        println("${Thread.currentThread()}-coroutineScope")
    }
    println("${Thread.currentThread()}-4")
}

suspend fun timeConsumingTask1() {}

fun sequential(){
    invoke1()
    invoke2()
    invoke3()
}

