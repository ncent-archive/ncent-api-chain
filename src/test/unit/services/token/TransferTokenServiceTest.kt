package test.unit.services.token

import framework.models.idValue
import io.kotlintest.Description
import io.kotlintest.TestResult
import io.kotlintest.shouldBe
import io.kotlintest.specs.WordSpec
import io.mockk.junit5.MockKExtension
import kotlinserverless.framework.models.Handler
import kotlinserverless.framework.services.SOAResultType
import main.daos.*
import main.services.token.GenerateTokenService
import main.services.token.GetTokenService
import main.services.token.TransferTokenService
import main.services.user_account.GenerateUserAccountService
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class TransferTokenServiceTest : WordSpec() {
    private var service = TransferTokenService()
    private lateinit var nCentTokenNamespace: TokenNamespace

    override fun beforeTest(description: Description): Unit {
        Handler.connectAndBuildTables()
        nCentTokenNamespace = TokenNamespace(
            amount = 100,
            tokenType = TokenTypeNamespace(
                id = null,
                name = "nCent",
                parentToken = null,
                parentTokenConversionRate = null
            )
        )
    }

    override fun afterTest(description: Description, result: TestResult) {
        Handler.disconnectAndDropTables()
    }

    init {
        "calling execute with a user transfer, having sufficient funds" should {
            "return the transaction generated" {
                var newUserAccount = GenerateUserAccountService().execute(
                    null,
                    mapOf(
                        Pair("firstname", "Arya"),
                        Pair("lastname", "Soltanieh"),
                        Pair("email", "as@ncent.io")
                    )
                ).data!!

                var newUserAccount2 = GenerateUserAccountService().execute(
                    null,
                    mapOf(
                        Pair("firstname", "Adam"),
                        Pair("lastname", "Foos"),
                        Pair("email", "af@ncent.io")
                    )
                ).data!!

                val token = GenerateTokenService().execute(newUserAccount.idValue, nCentTokenNamespace, null).data!!

                transaction {
                    var result = service.execute(newUserAccount.idValue, mapOf(
                        Pair("to", newUserAccount2.cryptoKeyPair.publicKey),
                        Pair("from", newUserAccount.cryptoKeyPair.publicKey),
                        Pair("name", "nCent"),
                        Pair("amount", "5")
                    ))
                    result.result shouldBe SOAResultType.SUCCESS
                    val tx = result.data as Transaction
                    tx.from shouldBe newUserAccount.cryptoKeyPair.publicKey
                    tx.to shouldBe newUserAccount2.cryptoKeyPair.publicKey
                    tx.action.type shouldBe ActionType.TRANSFER
                    tx.action.dataType shouldBe Token::class.simpleName!!
                    tx.action.data shouldBe token.idValue
                    tx.metadatas.first().value shouldBe "5.0"
                }
            }
        }

        "calling execute with a user transfer, having insufficient funds" should {
            "return failure" {
                var newUserAccount = GenerateUserAccountService().execute(
                    null,
                    mapOf(
                        Pair("firstname", "Arya"),
                        Pair("lastname", "Soltanieh"),
                        Pair("email", "as@ncent.io")
                    )
                ).data!!

                var newUserAccount2 = GenerateUserAccountService().execute(
                    null,
                    mapOf(
                        Pair("firstname", "Adam"),
                        Pair("lastname", "Foos"),
                        Pair("email", "af@ncent.io")
                    )
                ).data!!

                GenerateTokenService().execute(newUserAccount.idValue, nCentTokenNamespace, null)

                transaction {
                    var result = service.execute(newUserAccount.idValue, mapOf(
                        Pair("to", newUserAccount2.cryptoKeyPair.publicKey),
                        Pair("from", newUserAccount.cryptoKeyPair.publicKey),
                        Pair("name", "nCent"),
                        Pair("amount", "105")
                    ))
                    result.result shouldBe SOAResultType.FAILURE
                    result.message shouldBe "Insufficient funds"
                }
            }
        }
    }
}