package kr.bjj_oss.data.session

import kr.bjj_oss.model.*
import kotlinx.coroutines.*
import org.junit.Assert.*
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

class RefreshGateTest {
    @Test fun simultaneousRestAndWebRequestsRotateOnlyOnce() = runBlocking {
        val state = AtomicReference(TokenSnapshot(0, "FAKE_OLD", "FAKE_REFRESH"))
        val calls = AtomicInteger()
        val gate = RefreshGate({ state.get() }, {
            calls.incrementAndGet(); delay(40); AppResult.Success("FAKE_NEW" to "FAKE_ROTATED")
        }, { before, access, refresh -> state.compareAndSet(before, TokenSnapshot(before.revision, access, refresh)) })
        val results = (1..12).map { async(Dispatchers.Default) { gate.refresh("FAKE_OLD") } }.awaitAll()
        assertEquals(1, calls.get())
        assertTrue(results.all { it == AppResult.Success("FAKE_NEW") })
    }
    @Test fun logoutDuringRefreshCannotResurrectSession() = runBlocking {
        val state = AtomicReference(TokenSnapshot(0, "FAKE_OLD", "FAKE_REFRESH"))
        val started = CompletableDeferred<Unit>(); val finish = CompletableDeferred<Unit>()
        val gate = RefreshGate({ state.get() }, {
            started.complete(Unit); finish.await(); AppResult.Success("FAKE_NEW" to "FAKE_ROTATED")
        }, { before, access, refresh -> state.compareAndSet(before, TokenSnapshot(before.revision, access, refresh)) })
        val result = async { gate.refresh("FAKE_OLD") }
        started.await(); state.set(TokenSnapshot(1, null, null)); finish.complete(Unit)
        assertTrue(result.await() is AppResult.Failure)
        assertNull(state.get().accessToken)
    }
    @Test fun accountSwitchDuringRefreshCannotReplaceNewAccount() = runBlocking {
        val state = AtomicReference(TokenSnapshot(0, "FAKE_A", "FAKE_REFRESH_A"))
        val gate = RefreshGate({ state.get() }, {
            state.set(TokenSnapshot(1, "FAKE_B", "FAKE_REFRESH_B"))
            AppResult.Success("FAKE_A_NEW" to "FAKE_ROTATED")
        }, { before, access, refresh -> state.compareAndSet(before, TokenSnapshot(before.revision, access, refresh)) })
        assertTrue(gate.refresh("FAKE_A") is AppResult.Failure)
        assertEquals("FAKE_B", state.get().accessToken)
    }
    @Test fun expiredRequestFromPreviousAccountDoesNotRetryWithNewAccountsToken() = runBlocking {
        val gate = RefreshGate({ TokenSnapshot(1, "FAKE_B", "FAKE_REFRESH_B") }, {
            error("Must not fetch for an old account request")
        }, { _, _, _ -> error("Must not write") })
        assertTrue(gate.refresh("FAKE_A", expectedRevision = 0) is AppResult.Failure)
    }
    @Test fun concurrentFailuresShareOneAttemptButLaterRetryIsAllowed() = runBlocking {
        val calls = AtomicInteger()
        val started = CompletableDeferred<Unit>()
        val finish = CompletableDeferred<Unit>()
        val gate = RefreshGate({ TokenSnapshot(0, "FAKE_OLD", "FAKE_REFRESH") }, {
            calls.incrementAndGet(); started.complete(Unit); finish.await()
            AppResult.Failure(AppError(AppErrorKind.NETWORK))
        }, { _, _, _ -> error("Must not write") })
        val first = async { gate.refresh("FAKE_OLD") }
        started.await()
        val others = (1..5).map { async(start = CoroutineStart.UNDISPATCHED) { gate.refresh("FAKE_OLD") } }
        finish.complete(Unit)
        assertTrue(first.await() is AppResult.Failure)
        others.awaitAll()
        assertEquals(1, calls.get())
        gate.refresh("FAKE_OLD")
        assertEquals(2, calls.get())
    }
    @Test fun networkFailurePreservesDurableSession() = runBlocking {
        var writes = 0
        val gate = RefreshGate({ TokenSnapshot(0, "FAKE_OLD", "FAKE_REFRESH") }, {
            AppResult.Failure(AppError(AppErrorKind.NETWORK, retryable = true))
        }, { _, _, _ -> writes++; true })
        assertTrue(gate.refresh("FAKE_OLD") is AppResult.Failure)
        assertEquals(0, writes)
    }
}
