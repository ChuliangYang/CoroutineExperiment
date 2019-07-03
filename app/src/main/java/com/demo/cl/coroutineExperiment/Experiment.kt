package com.demo.cl.coroutineExperiment

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.Executors
import kotlin.coroutines.Continuation
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.system.measureTimeMillis


suspend fun main() {
    var count = 0
    val mutex = Mutex()

    val time = measureTimeMillis {
        val job = GlobalScope.launch {
            val sendChannel = actor<Int> {
                for (i in channel) {
                    count += i
                }
            }
            //使用父协程包裹10000个子协程,这样可以等待所有子协程完工后(即10000个数据都产生之后)才让channel关掉
            //如果不用父协程包裹而直接在repeat之外关channel的话,就会导致10000个数据来不及产生完就被close掉
            val plusJob = launch {
                repeat(100000) {
                    launch(Dispatchers.Default) {
                        sendChannel.send(1)
                    }
                }
            }
            plusJob.join()//等待10000个数据产生完毕
            sendChannel.close()
        }
        job.join()
    }
    println("$count/$time")

    count = 0

    val time2 = measureTimeMillis {
        val job = GlobalScope.launch {
            repeat(100000) {
                launch(Dispatchers.Default) {
                    mutex.withLock {
                        count++
                    }
                }
            }
        }
        job.join()
    }
    println("$count/$time2")

    highOrderFunction(5, 4) { a: Int, b: Int -> a + b }
    highOrderFunctionR(5, 4) {
        //这里的接收器(context)是Int,也就是相当于这块代码写在Int类中
        //this指向接收器,这里是5,it指向唯一的入参,这里是4
        this + it
    }

    val list: List<*> = listOf<Int>(1, 2, 3, 4)

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
    coroutineScope() {

    }
    println("${Thread.currentThread()}-1")
    withContext(Dispatchers.Default) {
        //构造子协程,模拟耗时任务
        println("${Thread.currentThread()}-coroutineScope")
    }
    println("${Thread.currentThread()}-2")
    withContext(Dispatchers.Default) {
        //构造子协程,模拟耗时任务
        println("${Thread.currentThread()}-coroutineScope")
    }
    println("${Thread.currentThread()}-3")
    withContext(Dispatchers.Default) {
        //构造子协程,模拟耗时任务
        println("${Thread.currentThread()}-coroutineScope")
    }
    println("${Thread.currentThread()}-4")
}

suspend fun timeConsumingTask1() {}

fun highOrderFunction(first: Int, second: Int, transform: (Int, Int) -> Int): Int {
    return transform(first, second)
}

fun highOrderFunctionR(first: Int, second: Int, transform: Int.(Int) -> Int): Int {
//    transform(first, second)//可以这么写
    return first.transform(second)//扩展函数写法
}

fun operation(opCode: Int): ((Int, Int) -> Int)? {
//    returnNothing()
//    returnUnit()
    when (opCode) {
        0 -> return { a, b -> a + b }
        1 -> return { a: Int, b: Int -> a - b }
        2 -> return { a, b -> a * b }
        3 -> return { a, b -> a / b }
        else -> return null
    }

    println("${Thread.currentThread()}-4")
}

inline fun <reified T> testReified() {
    val i: Boolean = "Test" is T
}



