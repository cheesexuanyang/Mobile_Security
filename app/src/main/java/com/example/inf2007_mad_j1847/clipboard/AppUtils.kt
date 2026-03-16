package com.example.inf2007_mad_j1847.clipboard

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

object AppUtils {
    fun getInstalledAppList(context: Context): List<String> {
        val pm = context.packageManager
        val apps = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getInstalledApplications(PackageManager.ApplicationInfoFlags.of(0))
        } else {
            pm.getInstalledApplications(PackageManager.GET_META_DATA)
        }
        return apps.mapNotNull { app ->
            val name = pm.getApplicationLabel(app).toString()
            // filter out entries with no readable label (ugly system packages)
            if (name.startsWith("com.") || name.startsWith("android.")) null
            else "$name (${app.packageName})"
        }
    }
}