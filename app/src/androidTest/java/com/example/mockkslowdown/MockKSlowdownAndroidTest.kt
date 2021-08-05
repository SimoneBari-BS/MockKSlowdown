package com.example.mockkslowdown

import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.unmockkAll
import kotlin.system.measureNanoTime
import org.junit.After
import org.junit.Before
import org.junit.Test

/** A class that will get mocked because it is returned by the class we want to mock */
private data class ReturnClassAndroid(val first: Int, val second: Int)

/** A class that wont get mocked because it is not returned by the class we want to mock */
private data class ArgumentClassAndroid(val first: Int, val second: Int)

/** The only class we want to mock */
private interface MockedInterfaceAndroid {
    fun function(size: ArgumentClassAndroid): ReturnClassAndroid
}

class MockKSlowdownAndroidTest {

    @MockK
    private lateinit var mock: MockedInterfaceAndroid

    private val numCycles = 10000

    @Before
    fun setup() {
        // This is not yet mocked, as it should be
        val a = ReturnClassAndroid(10, 10)
        // This is not mocked, as it should be
        val b = ArgumentClassAndroid(10, 10)

        val timeReturnClassBeforeMock = measureNanoTime {
            for (i in 0 until numCycles) {
                a.first
                a.second
            }
        }
        println("Testing ReturnClass before mocking took $timeReturnClassBeforeMock")

        val timeArgumentClassBeforeMock = measureNanoTime {
            for (i in 0 until numCycles) {
                b.first
                b.second
            }
        }
        println("Testing ArgumentClass before mocking took $timeArgumentClassBeforeMock")

        MockKAnnotations.init(this)

        every { mock.function(any()) } returns ReturnClassAndroid(10, 10)
        // Now ReturnClass is mocked!

        val timeReturnClassAfterMock = measureNanoTime {
            for (i in 0 until numCycles) {
                a.first
                a.second
            }
        }
        println("Testing ReturnClass after mocking took $timeReturnClassAfterMock")

        val timeArgumentClassAfterMock = measureNanoTime {
            for (i in 0 until numCycles) {
                b.first
                b.second
            }
        }
        println("Testing ArgumentClass after mocking took $timeArgumentClassAfterMock")
    }

    @Test
    fun fakeTest() {
    }

    @After
    fun cleanup() {

        unmockkAll()
        clearAllMocks()

        // This is still mocked!!!
        val a = ReturnClassAndroid(10, 10)

        val time = measureNanoTime {
            for (i in 0 until numCycles) {
                a.first
                a.second
            }
        }
        println("Testing ReturnClass after attempt to clear mocking took $time")
    }
}
