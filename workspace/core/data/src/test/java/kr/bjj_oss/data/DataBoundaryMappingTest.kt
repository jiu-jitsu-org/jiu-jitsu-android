package kr.bjj_oss.data

import kr.bjj_oss.data.api.common.ApiResult
import kr.bjj_oss.data.api.common.mapEnvelope
import kr.bjj_oss.data.model.dto.request.toRequest
import kr.bjj_oss.data.model.dto.response.CheckNicknameResponse
import kr.bjj_oss.model.AppErrorKind
import kr.bjj_oss.model.AppResult
import kr.bjj_oss.model.BELT_RANK
import kr.bjj_oss.model.BELT_STRIPE
import kr.bjj_oss.model.CommunityProfileField
import kr.bjj_oss.model.CommunityProfileUpdate
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
