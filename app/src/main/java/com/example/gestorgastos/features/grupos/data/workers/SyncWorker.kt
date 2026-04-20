package com.example.gestorgastos.features.grupos.data.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.ListenableWorker
import com.example.gestorgastos.core.database.dao.GastoDao
import com.example.gestorgastos.core.network.GastosApi
import com.example.gestorgastos.features.grupos.data.datasources.remote.model.GastoCreateRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val api: GastosApi,
    private val gastoDao: GastoDao
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): ListenableWorker.Result {
        val unsyncedGastos = gastoDao.getUnsyncedGastos()
        if (unsyncedGastos.isEmpty()) return ListenableWorker.Result.success()

        var allSuccessful = true

        unsyncedGastos.forEach { gasto ->
            try {
                api.agregarGasto(
                    gasto.grupoId,
                    GastoCreateRequest(
                        persona = gasto.persona,
                        monto = gasto.monto,
                        descripcion = gasto.descripcion,
                        tipo = gasto.tipo,
                        comprobanteUri = gasto.comprobanteUri
                    )
                )
                gastoDao.updateGasto(gasto.copy(isSynced = true))
            } catch (e: Exception) {
                allSuccessful = false
            }
        }

        return if (allSuccessful) ListenableWorker.Result.success() else ListenableWorker.Result.retry()
    }
}
