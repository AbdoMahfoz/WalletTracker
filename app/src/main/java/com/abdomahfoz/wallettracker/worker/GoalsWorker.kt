package com.abdomahfoz.wallettracker.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.*
import com.abdomahfoz.wallettracker.R
import com.abdomahfoz.wallettracker.entities.GoalEntity
import com.abdomahfoz.wallettracker.entities.SpendEntity.Important
import com.abdomahfoz.wallettracker.entities.SpendEntity
import com.abdomahfoz.wallettracker.repository.IGoalsRepository
import com.abdomahfoz.wallettracker.repository.ISpendRepository
import com.abdomahfoz.wallettracker.ui.MainActivity
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.generic.instance
import java.util.*
import java.util.concurrent.TimeUnit

class GoalsNotificationManager(val context: Context){
    private val channelId : String by lazy { createNotificationChannel() }
    private var idIncrement = 0
    private fun createNotificationChannel() : String {
        val channelId = "defaultChannel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Default",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Default" }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
        return channelId
    }
    fun pushNotification(title : String, smallMsg : String, bigMsg : String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, 0)
        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(smallMsg)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigMsg))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()
        with(NotificationManagerCompat.from(context)) {
            notify(idIncrement++, notification)
        }
    }
}

class GoalsWorker(context: Context, params : WorkerParameters) : CoroutineWorker(context, params), KodeinAware {
    override val kodein: Kodein by closestKodein(context)
    private val goalRepo by instance<IGoalsRepository>()
    private val spendRepo by instance<ISpendRepository>()
    private val notificationManager = GoalsNotificationManager(context)
    private fun insert(goal : GoalEntity, date : Date? = null) {
        spendRepo.insert(
            SpendEntity(
                amount = goal.dailyAmount,
                comment = goal.name,
                date = date ?: Calendar.getInstance().time,
                importance = Important.VeryImportant,
                goalId = goal.id
            )
        )
        goal.achieved += goal.dailyAmount
        goalRepo.update(goal)
        val smallMsg = "${goal.name}: Spend ${goal.dailyAmount.toLong()} EGP"
        val bigMsg = smallMsg + "\nAchieved ${goal.achieved.toLong()}/${goal.amount.toLong()} EGP (${goal.percent}%)"
        notificationManager.pushNotification("Goal daily transaction", smallMsg, bigMsg)
    }
    override suspend fun doWork(): Result {
        goalRepo.getAllSync().forEach{ goal ->
            Log.i("Worker", "Processing goal ${goal.name}")
            val spend = goalRepo.getLastSpendOfGoal(goal.id)
            if(spend != null) Log.i("Worker", "Last spend isn't null")
            val startDate = spend?.date ?: goal.start
            val x = (Calendar.getInstance().time.time - startDate.time) / 86400000
            Log.i("Worker", "Falling $x days behind")
            if (x > 0) {
                for(i in 1..x){
                    Log.i("Worker", "Inserting $i")
                    insert(goal, Date(startDate.time + (i * 86400000)))
                }
            }
            Log.i("Worker", "Done")
        }
        val now = Calendar.getInstance()
        val seconds = ((24L - now.get(Calendar.HOUR_OF_DAY)) * 60 * 60) -
                      ((now.get(Calendar.MINUTE) * 60) + now.get(Calendar.SECOND))
        val oneTimeRequest = OneTimeWorkRequestBuilder<GoalsWorker>()
            .setInitialDelay(seconds, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            GoalsWorker::class.simpleName!! + "2",
            ExistingWorkPolicy.REPLACE,
            oneTimeRequest
        )
        return Result.success()
    }
    companion object{
        fun setUp(applicationContext : Context){
            val oneTimeRequest = OneTimeWorkRequestBuilder<GoalsWorker>().build()
            WorkManager.getInstance(applicationContext).enqueueUniqueWork(
                GoalsWorker::class.simpleName!!,
                ExistingWorkPolicy.REPLACE,
                oneTimeRequest
            )
        }
    }
}