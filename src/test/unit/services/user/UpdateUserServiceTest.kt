package test.unit.services.user

import io.kotlintest.*
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.extension.ExtendWith
import main.daos.*
import kotlinserverless.framework.models.Handler
import kotlinserverless.framework.services.SOAResultType
import main.services.user_account.UpdateUserService
import org.jetbrains.exposed.sql.transactions.transaction
import test.TestHelper

@ExtendWith(MockKExtension::class)
class UpdateUserServiceTest : WordSpec() {
    private lateinit var userAccount: UserAccount

    override fun beforeTest(description: Description) {
        Handler.connectAndBuildTables()
        transaction {
            val newUserAccounts = TestHelper.generateUserAccounts(1)
            userAccount = newUserAccounts[0].value
        }
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute with valid user data" should {
            "allow for the user metadata to be changed" {
                transaction {
                    var result = UpdateUserService.execute(
                        userAccount,
                        UserNamespace("arya@ncent.io", "Arya", "Soltanieh")
                    )
                    result.result shouldBe SOAResultType.SUCCESS
                    result.data!!.email shouldBe "arya@ncent.io"
                    userAccount.refresh(true)
                    userAccount.userMetadata.email shouldBe "arya@ncent.io"
                }
            }
        }
    }
}