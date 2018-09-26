package main.userAccounts.services

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAServiceInterface
import main.userAccounts.models.UserAccount

/**
 * Validate the accuracy of the passed ApiKey and Secret key in the UserAccount
 */
class ValidateApiKeyService: SOAServiceInterface<UserAccount> {
    override fun execute(caller: Int?, data: UserAccount?, params: Map<String, String>?) : SOAResult<UserAccount> {
        throw NotImplementedError()
    }
}