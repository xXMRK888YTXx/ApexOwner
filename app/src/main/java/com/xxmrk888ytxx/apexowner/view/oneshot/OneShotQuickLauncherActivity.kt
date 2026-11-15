package com.xxmrk888ytxx.apexowner.view.oneshot

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.xxmrk888ytxx.apexowner.R
import com.xxmrk888ytxx.apexowner.bridge.ApexBridgeProvider
import com.xxmrk888ytxx.apexowner.bridge.WorkProfileDispatcherActivity
import com.xxmrk888ytxx.apexowner.view.mainActivity.MainActivity
import com.xxmrk888ytxx.core.base.android.logs.Logger
import com.xxmrk888ytxx.core.base.compose.theme.AppTheme
import com.xxmrk888ytxx.core.devicepolicy.OneShotAppManagerOwnerComponent
import com.xxmrk888ytxx.core.devicepolicy.ProfileOwnerManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalMaterial3Api::class)
@AndroidEntryPoint
class OneShotQuickLauncherActivity : ComponentActivity() {

    @Inject
    lateinit var oneShotAppManagerOwnerComponent: OneShotAppManagerOwnerComponent

    @Inject
    lateinit var profileOwnerManager: ProfileOwnerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppTheme {
                val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            var apps by remember { mutableStateOf<List<QuickLaunchApp>>(emptyList()) }
            var isLoading by remember { mutableStateOf(true) }
            var searchQuery by remember { mutableStateOf("") }

            LaunchedEffect(Unit) {
                apps = loadOneShotApps()
                isLoading = false
            }

            val filteredApps = remember(apps, searchQuery) {
                if (searchQuery.isBlank()) apps
                else apps.filter {
                    it.name.contains(searchQuery, ignoreCase = true) ||
                            it.packageName.contains(searchQuery, ignoreCase = true)
                }
            }

            ModalBottomSheet(
                onDismissRequest = { finish() },
                sheetState = sheetState,
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .padding(bottom = 36.dp)
                ) {
                    // Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_bolt),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.one_shot_launcher_title),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (apps.isNotEmpty()) "Приложений: ${apps.size}" else "0 приложений",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!isLoading && apps.isEmpty()) {
                        // Empty state card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "⚡",
                                    fontSize = 36.sp
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = stringResource(R.string.one_shot_launcher_empty_title),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(R.string.one_shot_launcher_empty_desc),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                    onClick = {
                                        openMainActivity()
                                        finish()
                                    },
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(stringResource(R.string.one_shot_open_profile))
                                }
                            }
                        }
                    } else {
                        // Search bar if apps count >= 4
                        if (apps.size >= 4) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text(stringResource(R.string.one_shot_search_hint)) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        LazyColumn(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredApps, key = { it.packageName }) { app ->
                                QuickLaunchAppItem(
                                    modifier = Modifier.animateItem(),
                                    app = app,
                                    onClick = { launchApp(app.packageName) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

    private suspend fun loadOneShotApps(): List<QuickLaunchApp> = withContext(Dispatchers.IO) {
        val pm = packageManager
        val (oneShotPkgs, autoFreezePkgs) = if (profileOwnerManager.isInsideWorkProfile || profileOwnerManager.isAppDeviceOwner) {
            val oneShot = oneShotAppManagerOwnerComponent.oneShotAppPackages.first()
            val autoFreeze = oneShotAppManagerOwnerComponent.autoFreezeAppPackages.first()
            oneShot to autoFreeze
        } else {
            try {
                val uri = Uri.parse("content://${ApexBridgeProvider.AUTHORITY}")
                val bundle = contentResolver.call(uri, ApexBridgeProvider.METHOD_GET_ONE_SHOT_APPS, null, null)
                val oneShotList = bundle?.getStringArrayList(ApexBridgeProvider.EXTRA_ONE_SHOT_PACKAGES)?.toSet() ?: emptySet()
                val autoFreezeList = bundle?.getStringArrayList(ApexBridgeProvider.EXTRA_AUTO_FREEZE_PACKAGES)?.toSet() ?: emptySet()
                oneShotList to autoFreezeList
            } catch (_: Exception) {
                val oneShot = oneShotAppManagerOwnerComponent.oneShotAppPackages.first()
                val autoFreeze = oneShotAppManagerOwnerComponent.autoFreezeAppPackages.first()
                oneShot to autoFreeze
            }
        }

        val allPackages = oneShotPkgs + autoFreezePkgs
        allPackages.map { pkg ->
            val label = try {
                val appInfo = pm.getApplicationInfo(
                    pkg,
                    PackageManager.MATCH_UNINSTALLED_PACKAGES or PackageManager.MATCH_DISABLED_COMPONENTS
                )
                appInfo.loadLabel(pm).toString()
            } catch (_: Exception) {
                pkg
            }
            val isHidden = try {
                profileOwnerManager.isApplicationHidden(pkg)
            } catch (_: Exception) {
                true
            }
            QuickLaunchApp(
                packageName = pkg,
                name = label,
                isFrozen = isHidden,
                isOneShot = oneShotPkgs.contains(pkg),
                isAutoFreeze = autoFreezePkgs.contains(pkg)
            )
        }.sortedBy { it.name.lowercase() }
    }

    private fun launchApp(packageName: String) {
        lifecycleScope.launch {
            if (profileOwnerManager.isInsideWorkProfile || profileOwnerManager.isAppDeviceOwner) {
                oneShotAppManagerOwnerComponent.launchOneShotApp(packageName)
            } else {
                try {
                    val intent = Intent(WorkProfileDispatcherActivity.ACTION_LAUNCH_APP).apply {
                        addCategory(Intent.CATEGORY_DEFAULT)
                        putExtra(WorkProfileDispatcherActivity.EXTRA_PACKAGE_NAME, packageName)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    startActivity(intent)
                } catch (e: Exception) {
                    Logger.writeErrorLog("Failed to dispatch launch intent: ${e.message}")
                }
            }
            finish()
        }
    }

    private fun openMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        startActivity(intent)
    }
}

data class QuickLaunchApp(
    val packageName: String,
    val name: String,
    val isFrozen: Boolean,
    val isOneShot: Boolean = false,
    val isAutoFreeze: Boolean = false
)

@Composable
private fun QuickLaunchAppItem(
    modifier: Modifier = Modifier,
    app: QuickLaunchApp,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppIcon(
                packageName = app.packageName,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    modifier = Modifier.padding(top = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (app.isOneShot) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.tertiaryContainer)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.badge_one_shot),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                    if (app.isAutoFreeze) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(MaterialTheme.colorScheme.secondaryContainer)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.badge_auto_freeze),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            FilledTonalButton(
                onClick = onClick,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(stringResource(R.string.one_shot_launch_btn))
            }
        }
    }
}

@Composable
private fun AppIcon(
    packageName: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val iconBitmap = remember(packageName) {
        try {
            val pm = context.packageManager
            val drawable = try {
                pm.getApplicationIcon(packageName)
            } catch (_: Exception) {
                val appInfo = pm.getApplicationInfo(
                    packageName,
                    PackageManager.MATCH_UNINSTALLED_PACKAGES or PackageManager.MATCH_DISABLED_COMPONENTS
                )
                appInfo.loadIcon(pm)
            }
            val bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth.coerceAtLeast(64),
                drawable.intrinsicHeight.coerceAtLeast(64),
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap.asImageBitmap()
        } catch (_: Exception) {
            null
        }
    }

    if (iconBitmap != null) {
        Image(
            bitmap = iconBitmap,
            contentDescription = null,
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier.background(
                MaterialTheme.colorScheme.surfaceContainerHighest,
                RoundedCornerShape(8.dp)
            )
        )
    }
}
