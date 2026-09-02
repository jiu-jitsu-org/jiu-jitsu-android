package com.kyu.jiu_jitsu.data

import com.kyu.jiu_jitsu.data.api.common.ApiResult
import com.kyu.jiu_jitsu.data.api.common.mapEnvelope
import com.kyu.jiu_jitsu.data.model.dto.request.toRequest
import com.kyu.jiu_jitsu.data.model.dto.response.CheckNicknameResponse
import com.kyu.jiu_jitsu.model.AppErrorKind
import com.kyu.jiu_jitsu.model.AppResult
import com.kyu.jiu_jitsu.model.BELT_RANK
import com.kyu.jiu_jitsu.model.BELT_STRIPE
import com.kyu.jiu_jitsu.model.CommunityProfileField
import com.kyu.jiu_jitsu.model.CommunityProfileUpdate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DataBoundaryMappingTest {
    @Test
    fun `community update command maps model values to backend wire values`() {
        val request = CommunityProfileUpdate(
            field = CommunityProfileField.BELT_WEIGHT,
            beltRank = BELT_RANK.PURPLE(),
            beltStripe = BELT_STRIPE.STRIPE_0(),
            weightKg = 78.5,
        ).toRequest()

        assertEquals("BELT_WEIGHT", request.profileRequestType)
        assertEquals("PURPLE", request.beltRank)
        assertEquals("STRIPE_0", request.beltStripe)
        assertEquals(78.5, request.weightKg)
    }

    @Test
    fun `successful envelope exposes only mapped payload`() {
        val result = ApiResult.Success(
            CheckNicknameResponse(
                success = true,
                code = "OK",
                message = null,
                data = true,
            ),
        ).mapEnvelope { available -> available }

        assertEquals(AppResult.Success(true), result)
    }

    @Test
    fun `business error envelope preserves server code without exposing dto`() {
        val result = ApiResult.Success(
            CheckNicknameResponse(
                success = false,
                code = "DUPLICATED_NICKNAME",
                message = "Already used.",
                data = null,
            ),
        ).mapEnvelope { available -> available }

        assertTrue(result is AppResult.Failure)
        val error = (result as AppResult.Failure).error
        assertEquals(AppErrorKind.SERVER, error.kind)
        assertEquals("DUPLICATED_NICKNAME", error.serverCode)
    }
}
