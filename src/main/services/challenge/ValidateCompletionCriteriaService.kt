package main.services.challenge

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import main.daos.CompletionCriteria
import main.daos.UserAccount

/**
 * For the time being, just validate the caller is the criteria address
 */
object ValidateCompletionCriteriaService {
    fun execute(caller: UserAccount, completionCriteria: CompletionCriteria) : SOAResult<Boolean> {
        // TODO -- eventually move this logic to the completion criteria
        // TODO -- in future there will be different completion criteria types, and
        // TODO -- the object should decide if it is valid or not

        val success = caller.cryptoKeyPair.publicKey == completionCriteria.address
        return SOAResult(SOAResultType.SUCCESS, if(!success) "This user cannot change the challenge state" else null, success)
    }
}