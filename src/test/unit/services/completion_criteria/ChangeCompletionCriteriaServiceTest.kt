package test.unit.services.completion_criteria

import framework.models.idValue
import io.kotlintest.*
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import kotlinserverless.framework.models.Handler
import main.services.challenge.ChangeCompletionCriteriaService
import org.jetbrains.exposed.sql.transactions.transaction
import test.TestHelper

@ExtendWith(MockKExtension::class)
class ChangeCompletionCriteriaServiceTest : WordSpec() {
    private lateinit var userAccount: UserAccount
    private lateinit var userAccount2: UserAccount
    private lateinit var completionCriteria: CompletionCriteria

    override fun beforeTest(description: Description): Unit {
        Handler.connectAndBuildTables()
        transaction {
            val newUserAccounts = TestHelper.generateUserAccounts(2)
            userAccount = newUserAccounts[0].value
            userAccount2 = newUserAccounts[1].value
            TestHelper.buildGenericReward()
            completionCriteria = CompletionCriteria.all().first()
            completionCriteria.address = userAccount.cryptoKeyPair.publicKey
        }
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute" should {
            "succeed with the correct address passed" {
                transaction {
                    val result = ChangeCompletionCriteriaService.execute(
                        userAccount,
                        completionCriteria.idValue,
                        userAccount2.cryptoKeyPair.publicKey
                    )
                    result.result shouldBe SOAResultType.SUCCESS
                    result.data!!.address shouldBe userAccount2.cryptoKeyPair.publicKey
                }
            }
            "fail with the incorrect address passed" {
                transaction {
                    val result = ChangeCompletionCriteriaService.execute(
                        userAccount2,
                        completionCriteria.idValue,
                        userAccount2.cryptoKeyPair.publicKey
                    )
                    result.result shouldBe SOAResultType.FAILURE
                }
            }
        }
    }
}