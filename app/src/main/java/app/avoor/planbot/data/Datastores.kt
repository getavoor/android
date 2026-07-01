package app.avoor.planbot.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

/**
 * A preference data store for regular data.
 */
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "gmprst")

/**
 * A preference data store for secure data.
 *
 * This data store won't be backed up.
 */
val Context.secureDataStore: DataStore<Preferences> by preferencesDataStore(name = "avrtmp")