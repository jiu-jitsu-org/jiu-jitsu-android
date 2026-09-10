package kr.bjj_oss.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CommunityProfileModelTest {
    @Test
    fun `all documented belt stripes map from backend wire values`() {
        assertTrue("STRIPE_0".toBeltStripe() is BELT_STRIPE.STRIPE_0)
        assertTrue("STRIPE_1".toBeltStripe() is BELT_STRIPE.STRIPE_1)
        assertTrue("STRIPE_2".toBeltStripe() is BELT_STRIPE.STRIPE_2)
        assertTrue("STRIPE_3".toBeltStripe() is BELT_STRIPE.STRIPE_3)
        assertTrue("STRIPE_4".toBeltStripe() is BELT_STRIPE.STRIPE_4)
        assertNull("STRIPE_5".toBeltStripe())
    }

    @Test
    fun `all documented competition outcomes map from backend wire values`() {
        assertTrue("GOLD".toCompetitionRank() is COMPETITION_RANK.GOLD)
        assertTrue("SILVER".toCompetitionRank() is COMPETITION_RANK.SILVER)
        assertTrue("BRONZE".toCompetitionRank() is COMPETITION_RANK.BRONZE)
        assertTrue("PARTICIPATION".toCompetitionRank() is COMPETITION_RANK.PARTICIPATION)
        assertNull("UNKNOWN".toCompetitionRank())
    }

    @Test
    fun `profile edit visibility follows optional profile content`() {
        assertTrue(CommunityProfileInfo().isDataEmpty())
        assertFalse(CommunityProfileInfo(academyName = "Jiu-Jitsu Lab").isDataEmpty())
        assertTrue(CommunityProfileInfo(beltRank = BELT_RANK.BLUE()).isShowModify())
    }
}
