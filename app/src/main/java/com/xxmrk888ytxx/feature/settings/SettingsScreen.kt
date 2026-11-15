package com.xxmrk888ytxx.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.xxmrk888ytxx.apexowner.BuildConfig
import com.xxmrk888ytxx.apexowner.R
import com.xxmrk888ytxx.core.base.compose.ui.CoreTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    var showClearDeviceOwnerDialog by remember { mutableStateOf(false) }
    var showRemoveWorkProfileDialog by remember { mutableStateOf(false) }

    if (showClearDeviceOwnerDialog) {
        AlertDialog(
            onDismissRequest = { showClearDeviceOwnerDialog = false },
            title = { Text(stringResource(R.string.clear_device_owner_title)) },
            text = { Text(stringResource(R.string.clear_device_owner_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        showClearDeviceOwnerDialog = false
                        viewModel.clearDeviceOwner()
                    },
                    enabled = !state.isBusy,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.clear_device_owner_action))
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = { showClearDeviceOwnerDialog = false }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
    if (showRemoveWorkProfileDialog) {
        AlertDialog(
            onDismissRequest = { showRemoveWorkProfileDialog = false },
            title = { Text(stringResource(R.string.remove_work_profile_title)) },
            text = { Text(stringResource(R.string.remove_work_profile_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        showRemoveWorkProfileDialog = false
                        viewModel.removeWorkProfile()
                    },
                    enabled = !state.isBusy,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.remove_work_profile_action))
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(
                    onClick = { showRemoveWorkProfileDialog = false }
                ) {
                    Text(stringResource(R.string.cancel))
                }
            }
        )
    }
    Scaffold(
        topBar = {
            CoreTopBar(
                title = { Text(stringResource(R.string.settings)) },
                isShouldShowBackArrow = false
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = stringResource(
                            R.string.settings_version_format,
                            BuildConfig.VERSION_CODE,
                            BuildConfig.VERSION_NAME
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (state.isDeviceOwner) {
                Button(
                    onClick = { showClearDeviceOwnerDialog = true },
                    enabled = !state.isBusy,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(stringResource(R.string.clear_device_owner_action))
                }
            }

            if (state.hasWorkProfile) {
                OutlinedButton(
                    onClick = { showRemoveWorkProfileDialog = true },
                    enabled = !state.isBusy,
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(stringResource(R.string.remove_work_profile_action))
                }
            }

            if (state.actionFailed) {
                Text(
                    text = stringResource(R.string.clear_device_owner_failed),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
