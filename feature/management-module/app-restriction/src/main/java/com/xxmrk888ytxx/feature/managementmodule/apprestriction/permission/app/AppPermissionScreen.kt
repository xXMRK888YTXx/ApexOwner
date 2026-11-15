package com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.app

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.xxmrk888ytxx.core.base.android.mvi.SideEffect
import com.xxmrk888ytxx.core.base.compose.asString
import com.xxmrk888ytxx.core.base.compose.extension.HandleSideEffects
import com.xxmrk888ytxx.core.base.compose.ui.CoreTopBar
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.R
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.app.model.AppPermissionPolicy
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.app.model.AppPermissionScreenState
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.app.model.AppPermissionUiEvent
import com.xxmrk888ytxx.feature.managementmodule.apprestriction.permission.app.model.AppPermissionUiModel
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AppPermissionScreen(
    screenState: AppPermissionScreenState,
    onEvent: (AppPermissionUiEvent) -> Unit,
    sideEffect: Flow<SideEffect>
) {
    HandleSideEffects(sideEffect)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(),
        topBar = {
            CoreTopBar(
                title = {
                    Text(text = stringResource(R.string.app_permissions_title))
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: App Info Card
            item {
                AppHeaderCard(
                    packageName = screenState.packageName,
                    appName = screenState.appName,
                    requestedCount = screenState.totalRequestedCount,
                    forceGrantedCount = screenState.forceGrantedCount,
                    forceDeniedCount = screenState.forceDeniedCount
                )
            }

            // Search Bar & Filter
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = screenState.searchQuery,
                        onValueChange = { onEvent(AppPermissionUiEvent.ChangeSearchQuery(it)) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = {
                            Text(
                                text = stringResource(R.string.perm_search_placeholder),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = screenState.onlyRequested,
                            onClick = { onEvent(AppPermissionUiEvent.ToggleOnlyRequested(true)) },
                            label = {
                                Text(
                                    text = "${stringResource(R.string.app_perm_only_requested)} (${screenState.totalRequestedCount})"
                                )
                            }
                        )

                        FilterChip(
                            selected = !screenState.onlyRequested,
                            onClick = { onEvent(AppPermissionUiEvent.ToggleOnlyRequested(false)) },
                            label = {
                                Text(text = stringResource(R.string.app_perm_all))
                            }
                        )
                    }
                }
            }

            // List of permissions
            items(
                items = screenState.permissions,
                key = { it.id }
            ) { item ->
                AppPermissionCard(
                    modifier = Modifier.animateItem(),
                    model = item,
                    onPolicyChange = { newPolicy ->
                        onEvent(AppPermissionUiEvent.ChangePolicy(item.id, newPolicy))
                    }
                )
            }
        }
    }
}

@Composable
private fun AppHeaderCard(
    packageName: String,
    appName: String,
    requestedCount: Int,
    forceGrantedCount: Int,
    forceDeniedCount: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f),
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AppIcon(
                    packageName = packageName,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(10.dp))
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = appName.ifBlank { packageName },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = packageName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                BadgeChip(
                    text = "${stringResource(R.string.app_perm_stats_requested)}: $requestedCount",
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (forceGrantedCount > 0) {
                    BadgeChip(
                        text = "${stringResource(R.string.app_perm_stats_granted)}: $forceGrantedCount",
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                }

                if (forceDeniedCount > 0) {
                    BadgeChip(
                        text = "${stringResource(R.string.app_perm_stats_denied)}: $forceDeniedCount",
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
private fun AppPermissionCard(
    modifier: Modifier = Modifier,
    model: AppPermissionUiModel,
    onPolicyChange: (AppPermissionPolicy) -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (model.policy) {
                AppPermissionPolicy.FORCE_GRANTED -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                AppPermissionPolicy.FORCE_DENIED -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.25f)
                AppPermissionPolicy.DEFAULT -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = model.iconRes),
                    contentDescription = null,
                    modifier = Modifier.size(26.dp),
                    tint = when (model.policy) {
                        AppPermissionPolicy.FORCE_GRANTED -> MaterialTheme.colorScheme.primary
                        AppPermissionPolicy.FORCE_DENIED -> MaterialTheme.colorScheme.error
                        AppPermissionPolicy.DEFAULT -> MaterialTheme.colorScheme.primary
                    }
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = model.title.asString(),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = model.technicalName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 0.5.sp
                                ),
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        if (model.isRequestedByApp) {
                            BadgeChip(
                                text = stringResource(R.string.app_perm_requested_badge),
                                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        } else {
                            BadgeChip(
                                text = stringResource(R.string.app_perm_not_requested_badge),
                                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (model.isGloballyDisabled) {
                            BadgeChip(
                                text = stringResource(R.string.app_perm_globally_disabled_badge),
                                containerColor = MaterialTheme.colorScheme.errorContainer,
                                contentColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Explanation text
            val statusDesc = when (model.policy) {
                AppPermissionPolicy.FORCE_GRANTED -> stringResource(R.string.app_perm_forced_granted_desc)
                AppPermissionPolicy.FORCE_DENIED -> stringResource(R.string.app_perm_forced_denied_desc)
                AppPermissionPolicy.DEFAULT -> {
                    if (!model.canForceGrant) {
                        stringResource(R.string.sensor_grant_not_supported_profile_owner)
                    } else if (model.isGloballyDisabled) {
                        stringResource(R.string.app_perm_default_globally_denied_desc)
                    } else {
                        stringResource(R.string.app_perm_default_system_desc)
                    }
                }
            }

            Text(
                text = statusDesc,
                style = MaterialTheme.typography.bodySmall,
                color = when (model.policy) {
                    AppPermissionPolicy.FORCE_GRANTED -> MaterialTheme.colorScheme.primary
                    AppPermissionPolicy.FORCE_DENIED -> MaterialTheme.colorScheme.error
                    AppPermissionPolicy.DEFAULT -> MaterialTheme.colorScheme.onSurfaceVariant
                },
                fontWeight = if (model.policy != AppPermissionPolicy.DEFAULT) FontWeight.Medium else FontWeight.Normal,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 3-Segmented button row for policy selection
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                SegmentedButton(
                    selected = model.policy == AppPermissionPolicy.DEFAULT,
                    onClick = { onPolicyChange(AppPermissionPolicy.DEFAULT) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 3)
                ) {
                    Text(
                        text = stringResource(R.string.app_perm_policy_default),
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                SegmentedButton(
                    selected = model.policy == AppPermissionPolicy.FORCE_DENIED,
                    onClick = { onPolicyChange(AppPermissionPolicy.FORCE_DENIED) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 3),
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.errorContainer,
                        activeContentColor = MaterialTheme.colorScheme.onErrorContainer
                    )
                ) {
                    Text(
                        text = stringResource(R.string.app_perm_policy_force_deny),
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                SegmentedButton(
                    selected = model.policy == AppPermissionPolicy.FORCE_GRANTED,
                    onClick = { onPolicyChange(AppPermissionPolicy.FORCE_GRANTED) },
                    shape = SegmentedButtonDefaults.itemShape(index = 2, count = 3),
                    enabled = model.canForceGrant,
                    colors = SegmentedButtonDefaults.colors(
                        activeContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        activeContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                ) {
                    Text(
                        text = stringResource(R.string.app_perm_policy_force_grant),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
private fun BadgeChip(
    text: String,
    containerColor: Color,
    contentColor: Color
) {
    Box(
        modifier = Modifier
            .background(containerColor, RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
        )
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
            val appInfo = context.packageManager.getApplicationInfo(
                packageName,
                PackageManager.MATCH_UNINSTALLED_PACKAGES or PackageManager.MATCH_DISABLED_COMPONENTS
            )
            val drawable = context.packageManager.getApplicationIcon(appInfo)
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
