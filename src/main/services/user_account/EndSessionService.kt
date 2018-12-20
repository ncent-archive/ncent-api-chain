package main.services.user_account

import kotlinserverless.framework.services.SOAResult
import kotlinserverless.framework.services.SOAResultType
import kotlinserverless.framework.services.SOAServiceInterface
import main.daos.Session
import main.daos.Sessions
import org.joda.time.DateTime

/**
 * Used to end a session (cache)
 */
object EndSessionService: SOAServiceInterface<Session> {
    override fun execute(caller: Int?, key: String?) : SOAResult<Session> {
        var session = Session.find { Sessions.sessionKey eq key!! }.first()
        if(session.expiration > DateTime.now()) {
            session.expiration = DateTime.now()
        }
        return SOAResult(SOAResultType.SUCCESS, null, session)
    }
}