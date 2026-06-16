package com.example.mypartyapp.core.network

import android.util.Log
import io.github.jan.supabase.exceptions.HttpRequestException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import kotlin.random.Random

private const val TAG = "NetworkRetry"

/**
 * Выполняет ИДЕМПОТЕНТНУЮ сетевую операцию (чтение) с защитой от подвисаний:
 *  - каждая попытка ограничена [attemptTimeoutMs] — зависшее соединение бросаем рано
 *    (защита соединения на уровне движка живёт в [supabaseClient]);
 *  - до [maxAttempts] попыток с экспоненциальной паузой и джиттером;
 *  - повторяем ТОЛЬКО зависания и сетевые сбои; ошибки HTTP/разбора данных
 *    пробрасываем сразу, без повторов.
 *
 * НЕ применять к НЕидемпотентным операциям (регистрация, оплата, запись на ивент):
 * если запрос дошёл до сервера и выполнился, но ответ потерялся в сети, повтор
 * выполнит операцию повторно (дубль/двойное списание).
 *
 * @param label короткая метка операции для логов (напр. "getFeed")
 */
suspend fun <T> retryingRead(
    label: String,
    maxAttempts: Int = 2,
    attemptTimeoutMs: Long = 5_000L,
    baseBackoffMs: Long = 400L,
    block: suspend () -> T,
): T {
    var lastError: Exception? = null
    repeat(maxAttempts) { attempt ->
        try {
            return withTimeout(attemptTimeoutMs) { block() }
        } catch (e: TimeoutCancellationException) {
            lastError = e
            Log.w(TAG, "$label: попытка ${attempt + 1}/$maxAttempts прервана по таймауту ($attemptTimeoutMs мс)")
        } catch (e: HttpRequestException) {
            lastError = e
            Log.w(TAG, "$label: попытка ${attempt + 1}/$maxAttempts — сетевой сбой: ${e.message}")
        }
        if (attempt < maxAttempts - 1) {
            val backoff = baseBackoffMs shl attempt          // экспоненциальная база
            delay(backoff + Random.nextLong(baseBackoffMs))  // + джиттер
        }
    }
    Log.e(TAG, "$label: не удалось загрузить за $maxAttempts попыток", lastError)
    throw lastError!!
}
