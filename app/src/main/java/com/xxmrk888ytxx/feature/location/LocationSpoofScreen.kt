package com.xxmrk888ytxx.feature.location

import android.content.ClipDescription
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.xxmrk888ytxx.apexowner.R
import com.xxmrk888ytxx.apexowner.location.LocationSpoofError
import com.xxmrk888ytxx.core.base.compose.ui.CoreTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSpoofScreen(viewModel: LocationSpoofViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    var latitude by remember(state.latitude) { mutableStateOf(state.latitude) }
    var longitude by remember(state.longitude) { mutableStateOf(state.longitude) }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { viewModel.refresh() }

    Scaffold(
        topBar = {
            CoreTopBar(title = { Text(stringResource(R.string.location_spoof_title)) })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (state.enabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(stringResource(R.string.location_spoof_current_title), style = MaterialTheme.typography.titleMedium)
                    Text(
                        if (state.enabled) {
                            stringResource(R.string.location_spoof_using_format, state.latitude, state.longitude)
                        } else {
                            stringResource(R.string.location_spoof_not_active)
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(stringResource(R.string.location_spoof_point_title), style = MaterialTheme.typography.titleMedium)
                    Text(stringResource(R.string.location_spoof_description), style = MaterialTheme.typography.bodyMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        CoordinateField(
                            value = latitude,
                            label = stringResource(R.string.location_latitude),
                            onValueChange = { latitude = it },
                            onClipboard = { readCoordinateFromClipboard(context, true)?.let { latitude = it } },
                            modifier = Modifier.weight(1f)
                        )
                        CoordinateField(
                            value = longitude,
                            label = stringResource(R.string.location_longitude),
                            onValueChange = { longitude = it },
                            onClipboard = { readCoordinateFromClipboard(context, false)?.let { longitude = it } },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    OutlinedButton(
                        onClick = {
                            readCoordinatesFromClipboard(context)?.let { (lat, lon) ->
                                latitude = lat
                                longitude = lon
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.location_from_clipboard_pair))
                    }
                    Button(
                        onClick = { viewModel.applyCoordinates(latitude, longitude) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = latitude.isNotBlank() && longitude.isNotBlank()
                    ) {
                        Text(stringResource(R.string.location_apply))
                    }
                    Text(
                        stringResource(
                            when (state.error) {
                                LocationSpoofError.INVALID_COORDINATES -> R.string.location_spoof_invalid_coordinates
                                LocationSpoofError.MOCK_LOCATION_APP_REQUIRED -> R.string.location_spoof_mock_app_required
                                LocationSpoofError.LOCATION_PERMISSION_REQUIRED -> R.string.location_spoof_permission_required
                                null -> if (state.enabled) R.string.location_spoof_enabled else R.string.location_spoof_disabled
                            }
                        ),
                        color = if (state.error == null) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(stringResource(R.string.location_spoof_switch))
                        Switch(checked = state.enabled, onCheckedChange = viewModel::setEnabled)
                    }
                }
            }

            if (!state.hasLocationPermission) {
                OutlinedButton(
                    onClick = {
                        permissionLauncher.launch(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION))
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text(stringResource(R.string.location_spoof_grant_location)) }
            }
            if (!state.isMockLocationApp) {
                OutlinedButton(
                    onClick = {
                        runCatching { context.startActivity(viewModel.mockLocationSettingsIntent()) }
                            .onFailure { context.startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS)) }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) { Text(stringResource(R.string.location_spoof_open_mock_settings)) }
            }
        }
    }
}

@Composable
private fun CoordinateField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    onClipboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        OutlinedTextField(value = value, onValueChange = onValueChange, label = { Text(label) }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedButton(onClick = onClipboard, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.location_from_clipboard))
        }
    }
}

private fun readCoordinateFromClipboard(context: Context, first: Boolean): String? {
    val clipboard = context.getSystemService(android.content.ClipboardManager::class.java) ?: return null
    if (!clipboard.hasPrimaryClip()) return null
    val clip = clipboard.primaryClip ?: return null
    if (clip.description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN).not()) return null
    val text = clip.getItemAt(0).coerceToText(context).toString().trim()
    val parts = text.split(',', ';', ' ', '\n', '\t').filter { it.isNotBlank() }
    return if (parts.size >= 2) parts[if (first) 0 else 1] else text
}

private fun readCoordinatesFromClipboard(context: Context): Pair<String, String>? {
    val clipboard = context.getSystemService(android.content.ClipboardManager::class.java) ?: return null
    if (!clipboard.hasPrimaryClip()) return null
    val clip = clipboard.primaryClip ?: return null
    if (!clip.description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) return null
    val parts = clip.getItemAt(0).coerceToText(context).toString()
        .trim()
        .split(',', ';', ' ', '\n', '\t')
        .filter { it.isNotBlank() }
    return if (parts.size >= 2) parts[0] to parts[1] else null
}
