package framework.chain

import framework.models.BaseObject

interface ReadableLedgerClient<T: BaseObject>: LedgerClient<T> {

    // Read the latest state for a particular transaction type
    // the type is determined by the set of key/value pairs
    // ex: get the latest balance for a particular asset type and particular state
    fun read(vararg kvp: Pair<String, String>): T

    // Read the history for a particular transaction type
    // ex: get all transactions to/from a particular address for a particular asset type and state
    fun readAll( vararg kvp: Pair<String, String>): List<T>
}