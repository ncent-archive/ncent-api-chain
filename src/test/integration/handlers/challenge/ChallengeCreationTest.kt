package test.integration.handlers.challenge

import com.amazonaws.services.lambda.runtime.Context
import framework.models.idValue
import io.kotlintest.shouldBe
import io.kotlintest.Description
import io.kotlintest.TestResult
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinserverless.framework.models.Handler
import main.daos.*
import org.junit.jupiter.api.extension.ExtendWith
import test.TestHelper

@ExtendWith(MockKExtension::class)
class ChallengeCreationTest : WordSpec() {
    private lateinit var handler: Handler
    private lateinit var contxt: Context
    private lateinit var user: UserAccount
    private lateinit var parentChallenge: Challenge
    private lateinit var distributionFeeRewardNamespace: RewardNamespace
    private lateinit var challengeSettingNamespace: ChallengeSettingNamespace
    private lateinit var completionCriteriaNamespace: CompletionCriteriaNamespace
    private lateinit var challengeNamespace: ChallengeNamespace
    private val map = mutableMapOf(
            Pair("path", "/challenge/"),
            Pair("httpMethod", "POST")
    )

    override fun beforeTest(description: Description): Unit {
        Handler.connectAndBuildTables()
        handler = Handler()
        contxt = mockk()
        user = TestHelper.generateUserAccounts().first()
        parentChallenge = TestHelper.generateChallenge(user).first()
        distributionFeeRewardNamespace = TestHelper.generateRewardNamespace(RewardTypeName.SINGLE)
        challengeSettingNamespace = TestHelper.generateChallengeSettingsNamespace(user).first()
        completionCriteriaNamespace = TestHelper.generateCompletionCriteriaNamespace(user).first()
        challengeNamespace = ChallengeNamespace(
                parentChallenge = parentChallenge.idValue,
                challengeSettings = challengeSettingNamespace,
                subChallenges = null,
                completionCriteria = completionCriteriaNamespace,
                distributionFeeReward = distributionFeeRewardNamespace
        )
        map["userId"] = user.idValue.toString()
        map["challengeNamespace"] = challengeNamespace.toString()
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "correct path" should {
            "should return a valid new challenge" {
                val response = handler.handleRequest(map, contxt)
                response.statusCode shouldBe 200
            }
        }
    }
}